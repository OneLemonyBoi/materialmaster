package com.fuzs.materialmaster.api.core;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.fuzs.materialmaster.api.SyncProvider;
import com.fuzs.materialmaster.api.core.storage.AbstractProviderInfo;
import com.fuzs.materialmaster.api.core.storage.MapProviderInfo;
import com.fuzs.materialmaster.api.core.storage.SetProviderInfo;
import com.fuzs.materialmaster.api.provider.AbstractPropertyProvider;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
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

    private static final Multimap<String, ModConfig> SYNC_PROVIDERS = HashMultimap.create();
    private static final Multimap<ModConfig, AbstractProviderInfo> CONFIG_PROVIDERS = HashMultimap.create();

    private ModSyncManager() {
    }

    public void registerSyncProviderMod(String modId, @Nullable ModConfig config) {

        // all of this comes from the mod currently registering itself
        SYNC_PROVIDERS.put(modId, config);
    }

    public void processSyncProviders() {

        for (Map.Entry<String, ModConfig> entry : SYNC_PROVIDERS.entries()) {

            String modId = entry.getKey();
            ModConfig config = entry.getValue();
            if (config == null) {

                this.putTypes(modId, this.getFilteredElements(modId, ElementType.TYPE));
            } else {

                this.putFields(config, this.getFilteredElements(modId, ElementType.FIELD));
                this.syncConfigProviders(config);
            }
        }
    }

    private void syncConfigProviders(ModConfig config) {

        CONFIG_PROVIDERS.get(config).forEach(AbstractProviderInfo::sync);
    }

    private Set<ModFileScanData.AnnotationData> getFilteredElements(String modId, ElementType type) {

        return ModList.get().getModFileById(modId).getFile().getScanResult().getAnnotations().stream()
                .filter(data -> Type.getType(SyncProvider.class).equals(data.getAnnotationType()))
                .filter(data -> data.getTargetType().equals(type))
                .collect(Collectors.toSet());
    }

    private void putTypes(String modId, Set<ModFileScanData.AnnotationData> annotated) {

        for (ModFileScanData.AnnotationData annotationData : annotated) {

            try {

                Class<?> clazz = Class.forName(annotationData.getClassType().getClassName());
                if (AbstractPropertyProvider.class.isAssignableFrom(clazz)) {

                    PropertySyncManager.getInstance().registerPropertyProvider(modId, (AbstractPropertyProvider) clazz.newInstance());
                } else {

                    throw new RuntimeException("Unsupported class type");
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {

                e.printStackTrace();
            }
        }
    }

    private void putFields(ModConfig config, Set<ModFileScanData.AnnotationData> annotated) {

        CONFIG_PROVIDERS.putAll(config, this.buildInfoSet(annotated, config.getSpec().getValues(), config.getType()));
    }

    @SuppressWarnings("unchecked")
    private Set<AbstractProviderInfo> buildInfoSet(Set<ModFileScanData.AnnotationData> annotated, UnmodifiableConfig values, ModConfig.Type configType) {

        Set<AbstractProviderInfo> info = Sets.newHashSet();
        for (ModFileScanData.AnnotationData data : annotated) {

            ModConfig.Type type = getEnumValue(data, "type", ModConfig.Type.class, ModConfig.Type.COMMON);
            if (type != configType) {

                continue;
            }

            try {

                Class<?> clazz = Class.forName(data.getClassType().getClassName());
                Field field = clazz.getDeclaredField(data.getMemberName());
                this.validateField(field);
                // this always needs to be present, so no checks required
                List<String> path = (List<String>) data.getAnnotationData().get("path");
                SyncProvider.RegistryType registry = getEnumValue(data, "registry", SyncProvider.RegistryType.class, SyncProvider.RegistryType.ITEMS);

                Object configValue = values.get(path);
                if (!(configValue instanceof ForgeConfigSpec.ConfigValue<?>)) {

                    throw new RuntimeException("Invalid config option path");
                }

                if (Set.class.isAssignableFrom(field.getType())) {

                    info.add(new SetProviderInfo(field, (ForgeConfigSpec.ConfigValue<?>) configValue, registry.createBuilder()));
                } else if (Map.class.isAssignableFrom(field.getType())) {

                    double min = getDoubleValue(data, "min", Integer.MIN_VALUE);
                    double max = getDoubleValue(data, "max", Integer.MAX_VALUE);
                    info.add(new MapProviderInfo(field, (ForgeConfigSpec.ConfigValue<?>) configValue, registry.createBuilder(), min, max));
                } else {

                    throw new RuntimeException("Unsupported field type");
                }
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {

                e.printStackTrace();
            }
        }

        return info;
    }

    private void validateField(Field field) throws NoSuchFieldException, IllegalAccessException {

        final int modifiers = field.getModifiers();
        if (!Modifier.isPublic(modifiers)|| !Modifier.isStatic(modifiers)) {

            throw new RuntimeException(new IllegalAccessException());
        }

        if (Modifier.isFinal(modifiers)) {

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, modifiers & ~Modifier.FINAL);
        }
    }

    public static double getDoubleValue(ModFileScanData.AnnotationData data, String key, double defaultValue) {

        return (double) data.getAnnotationData().getOrDefault(key, defaultValue);
    }

    private static <T extends Enum<T>> T getEnumValue(ModFileScanData.AnnotationData data, String key, Class<T> clazz, T defaultValue) {

        return Optional.ofNullable((ModAnnotation.EnumHolder) data.getAnnotationData().get(key))
                .map(holder -> Enum.valueOf(clazz, holder.getValue()))
                .orElse(defaultValue);
    }

    // config event handler
    public void onModConfig(final ModConfig.ConfigReloading evt) {

        this.syncConfigProviders(evt.getConfig());
    }

    public static ModSyncManager getInstance() {

        return INSTANCE;
    }

}
