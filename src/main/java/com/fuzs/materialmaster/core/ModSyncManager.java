package com.fuzs.materialmaster.core;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.fuzs.materialmaster.api.SyncProvider;
import com.fuzs.materialmaster.api.provider.AbstractPropertyProvider;
import com.fuzs.materialmaster.core.storage.AbstractProviderInfo;
import com.fuzs.materialmaster.core.storage.MapProviderInfo;
import com.fuzs.materialmaster.core.storage.SetProviderInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ModSyncManager {

    private static final ModSyncManager INSTANCE = new ModSyncManager();

    private final Map<String, Set<ModFileScanData.AnnotationData>> mods = Maps.newHashMap();
    private final Map<ForgeConfigSpec, Set<AbstractProviderInfo>> syncable = Maps.newHashMap();

    private ModSyncManager() {
    }

    public void registerModProvider() {

        // all of this comes from the mod currently registering itself
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConfig);
        String modid = ModLoadingContext.get().getActiveContainer().getModId();
        this.mods.put(modid, Sets.newHashSet());
    }

    public void processModProviders() {

        for (Map.Entry<String, Set<ModFileScanData.AnnotationData>> entry : this.mods.entrySet()) {

            String modid = entry.getKey();
            Set<ModFileScanData.AnnotationData> annotated = ModList.get().getModFileById(modid).getFile()
                    .getScanResult().getAnnotations().stream()
                    .filter(data -> Type.getType(SyncProvider.class).equals(data.getAnnotationType()))
                    .collect(Collectors.toSet());

            // will remove classes automatically and only leave fields
            this.processClasses(modid, annotated);
            entry.getValue().addAll(annotated);
        }
    }

    private void processClasses(String modId, Set<ModFileScanData.AnnotationData> annotated) {

        annotated.stream().filter(data -> data.getTargetType().equals(ElementType.TYPE)).forEach(data -> {

            try {

                Class<?> clazz = Class.forName(data.getClassType().getClassName());
                if (AbstractPropertyProvider.class.isAssignableFrom(clazz)) {

                    PropertySyncManager.getInstance().registerPropertyProvider(modId, (AbstractPropertyProvider) clazz.newInstance());
                } else {

                    throw new RuntimeException("Unsupported class type");
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {

                e.printStackTrace();
            }
        });

        annotated.removeIf(data -> data.getTargetType().equals(ElementType.TYPE));
    }

    private void processFields(String modid, ForgeConfigSpec spec) {

        Optional.ofNullable(this.mods.get(modid)).ifPresent(annotated -> {

            Set<AbstractProviderInfo> info = this.buildInfoSet(annotated, spec.getValues());
            if (!info.isEmpty()) {

                info.forEach(AbstractProviderInfo::sync);
                this.syncable.merge(spec, info, (info1, info2) -> {

                    info1.addAll(info2);
                    return info1;
                });
            } else {

                throw new RuntimeException("Empty info set");
            }
        });
    }

    @SuppressWarnings("unchecked")
    private Set<AbstractProviderInfo> buildInfoSet(Set<ModFileScanData.AnnotationData> annotated, UnmodifiableConfig values) {

        Set<AbstractProviderInfo> info = Sets.newHashSet();
        for (ModFileScanData.AnnotationData data : annotated) {

            try {

                Class<?> clazz = Class.forName(data.getClassType().getClassName());
                Field field = clazz.getDeclaredField(data.getMemberName());

                final int modifiers = field.getModifiers();
                if (!Modifier.isPublic(modifiers)|| !Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {

                    throw new RuntimeException(new IllegalAccessException());
                }

                List<String> path = (List<String>) data.getAnnotationData().get("path");
                ModAnnotation.EnumHolder holder = (ModAnnotation.EnumHolder) data.getAnnotationData().get("type");
                SyncProvider.RegistryType type = holder != null ? SyncProvider.RegistryType.valueOf(holder.getValue()) : SyncProvider.RegistryType.ITEMS;
                double min = (double) data.getAnnotationData().getOrDefault("min", (double) Integer.MIN_VALUE);
                double max = (double) data.getAnnotationData().getOrDefault("max", (double) Integer.MAX_VALUE);
                if (Set.class.isAssignableFrom(field.getType())) {

                    Object value = values.get(Lists.newArrayList(path));
                    if (value instanceof ForgeConfigSpec.ConfigValue<?>) {

                        info.add(new SetProviderInfo(field, (ForgeConfigSpec.ConfigValue<?>) value, type.createBuilder()));
                    } else {

                        throw new RuntimeException("Invalid config option path");
                    }
                } else if (Map.class.isAssignableFrom(field.getType())) {

                    Object value = values.get(Lists.newArrayList(path));
                    if (value instanceof ForgeConfigSpec.ConfigValue<?>) {

                        info.add(new MapProviderInfo(field, (ForgeConfigSpec.ConfigValue<?>) value, type.createBuilder(), min, max));
                    } else {

                        throw new RuntimeException("Invalid config option path");
                    }
                } else {

                    throw new RuntimeException("Unsupported field type");
                }
            } catch (ClassNotFoundException | NoSuchFieldException e) {

                e.printStackTrace();
            }
        }

        return info;
    }

    // config event handler
    public void onModConfig(final ModConfig.ConfigReloading evt) {

        String modid = evt.getConfig().getModId();
        ForgeConfigSpec spec = evt.getConfig().getSpec();
        if (this.mods.containsKey(modid)) {

            Set<AbstractProviderInfo> providerInfo = this.syncable.get(spec);
            if (providerInfo != null) {

                providerInfo.forEach(AbstractProviderInfo::sync);
            } else {

                this.processFields(modid, spec);
            }
        }
    }

    public static ModSyncManager getInstance() {

        return INSTANCE;
    }

}
