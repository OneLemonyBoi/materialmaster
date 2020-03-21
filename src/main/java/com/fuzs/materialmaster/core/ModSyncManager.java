package com.fuzs.materialmaster.core;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.fuzs.materialmaster.MaterialMaster;
import com.fuzs.materialmaster.api.SyncProvider;
import com.fuzs.materialmaster.api.builder.EntryCollectionBuilder;
import com.fuzs.materialmaster.api.provider.AbstractPropertyProvider;
import com.fuzs.materialmaster.core.provider.TestProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ModSyncManager {

    private static final ModSyncManager INSTANCE = new ModSyncManager();

    private final Map<ModContainer, Set<Class<?>>> mods = Maps.newHashMap();
    private final Map<String, Set<AbstractProviderInfo>> syncable = Maps.newHashMap();

    private ModSyncManager() {
    }

    public void registerModProvider() {

        this.mods.put(ModLoadingContext.get().getActiveContainer(), Sets.newHashSet());
    }

    public void processModProviders() {

        for (Map.Entry<ModContainer, Set<Class<?>>> entry : this.mods.entrySet()) {

            ModContainer mod = entry.getKey();
            Set<Class<?>> annotated = entry.getValue();
            Set<Class<?>> allAnnotated = this.findAnnotatedClasses(mod.getMod().getClass());
            for (Class<?> clazz : allAnnotated) {

                if (AbstractPropertyProvider.class.isAssignableFrom(clazz)) {

                    try {

                        PropertySyncManager.getInstance().registerPropertyProvider(mod.getModId(), (AbstractPropertyProvider) clazz.newInstance());
                    } catch (InstantiationException | IllegalAccessException ignored) {
                    }
                }
            }

            allAnnotated.removeIf(AbstractPropertyProvider.class::isAssignableFrom);
            annotated.addAll(allAnnotated);
        }
    }

    // config event handler
    public void onModConfig(final ModConfig.ModConfigEvent evt) {

        String modId = evt.getConfig().getModId();
        if (this.mods.keySet().stream().map(ModContainer::getModId).anyMatch(id -> id.equals(modId))) {

            this.processSyncables(modId, evt.getConfig().getSpec().getValues());
            MaterialMaster.LOGGER.error(TestProvider.itemList);

//            if (evt instanceof ModConfig.Loading) {
//
//                this.processSyncables(modId, evt.getConfig().getSpec().getValues());
//            } else {
//
//                this.syncable.get(modId).forEach(AbstractProviderInfo::sync);
//                MaterialMaster.LOGGER.error(TestProvider.itemList);
//            }
        }
    }

    private void processSyncables(String modId, UnmodifiableConfig values) {

        Optional<Map.Entry<ModContainer, Set<Class<?>>>> mod = this.mods.entrySet().stream().filter(entry -> entry.getKey().getModId().equals(modId)).findFirst();
        mod.ifPresent(annotated -> {

            Set<AbstractProviderInfo> info = this.buildInfoSet(annotated, values);
            if (!info.isEmpty()) {

                info.forEach(AbstractProviderInfo::sync);
                this.syncable.merge(modId, info, (info1, info2) -> {

                    info1.addAll(info2);
                    return info1;
                });
            }
        });
    }

    private Set<AbstractProviderInfo> buildInfoSet(Map.Entry<ModContainer, Set<Class<?>>> annotated, UnmodifiableConfig values) {

        Set<AbstractProviderInfo> info = Sets.newHashSet();
        for (Class<?> clazz : annotated.getValue()) {

            for (Field field : clazz.getDeclaredFields()) {

                final int modifiers = field.getModifiers();
                if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {

                    if (Set.class.isAssignableFrom(field.getType()))
                    Optional.ofNullable(field.getAnnotation(SyncProvider.EntrySet.class)).ifPresent(entry -> {

                        Object value = values.get(Lists.newArrayList(entry.path()));
                        if (value instanceof ForgeConfigSpec.ConfigValue<?>) {

                            info.add(new SetProviderInfo(field, (ForgeConfigSpec.ConfigValue<?>) value, entry.type().createBuilder()));
                        }
                    });

                    if (Map.class.isAssignableFrom(field.getType()))
                    Optional.ofNullable(field.getAnnotation(SyncProvider.EntryMap.class)).ifPresent(entry -> {

                        Object value = values.get(Lists.newArrayList(entry.path()));
                        if (value instanceof ForgeConfigSpec.ConfigValue<?>) {

                            info.add(new MapProviderInfo(field, (ForgeConfigSpec.ConfigValue<?>) value, entry.type().createBuilder(), entry.min(), entry.max()));
                        }
                    });
                }
            }
        }

        return info;
    }

    @SuppressWarnings("UnstableApiUsage")
    private Set<Class<?>> findAnnotatedClasses(Class<?> clazz) {

        final ClassLoader loader = clazz.getClassLoader();
        try {

            return ClassPath.from(loader)
                    .getTopLevelClassesRecursive(clazz.getPackage().getName())
                    .stream()
                    .map(info -> {

                        try {

                            // can't use ClassPath.ClassInfo#load as it's using another class loader
                            return loader.loadClass(info.getName());
                        } catch (ClassNotFoundException ignored) {
                        }

                        return null;
                    })
                    .filter(loaded -> Objects.nonNull(loaded) && loaded.isAnnotationPresent(SyncProvider.class))
                    .collect(Collectors.toSet());
        } catch (IOException ignored) {
        }

        return Sets.newHashSet();
    }

    public static ModSyncManager getInstance() {

        return INSTANCE;
    }

    private static abstract class AbstractProviderInfo {

        protected final Field field;
        protected final ForgeConfigSpec.ConfigValue<?> value;
        protected final EntryCollectionBuilder<?> builder;

        AbstractProviderInfo(Field field, ForgeConfigSpec.ConfigValue<?> configValue, EntryCollectionBuilder<?> builder) {

            this.field = field;
            this.value = configValue;
            this.builder = builder;
        }

        protected abstract void sync();

    }

    private static class SetProviderInfo extends AbstractProviderInfo {


        SetProviderInfo(Field field, ForgeConfigSpec.ConfigValue<?> configValue, EntryCollectionBuilder<?> builder) {

            super(field, configValue, builder);
        }

        @SuppressWarnings("unchecked")
        protected void sync() {

            try {

                // static field doesn't have an object associated with it
                this.field.set(null, this.builder.buildEntrySet((List<String>) this.value.get()));
            } catch (IllegalAccessException | ClassCastException ignored) {
            }
        }

    }

    private static class MapProviderInfo extends AbstractProviderInfo {

        private final double min;
        private final double max;

        MapProviderInfo(Field field, ForgeConfigSpec.ConfigValue<?> configValue, EntryCollectionBuilder<?> builder, double min, double max) {

            super(field, configValue, builder);
            this.min = min;
            this.max = max;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void sync() {

            try {

                // static field doesn't have an object associated with it
                this.field.set(null, this.builder.buildEntryMapWithCondition((List<String>) this.value.get(), (entry, value) -> value >= this.min && value <= this.max, "Out of bounds"));
            } catch (IllegalAccessException | ClassCastException ignored) {
            }

        }

    }

}
