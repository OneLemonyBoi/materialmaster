package com.fuzs.materialmaster.api;

import com.fuzs.materialmaster.api.builder.EntryCollectionBuilder;
import net.minecraftforge.fml.config.ModConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

/**
 * annotation used for two types of objects:
 *
 * property provider classes for automatic registering
 * fields synced to a config option which needs to be build first
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface SyncProvider {

    /**
     * @return config path, consists of category / categories and name
     */
    String[] path() default "";

    /**
     * @return type of forge registry to be used to build this collection
     */
    RegistryType registry() default RegistryType.ITEMS;

    /**
     * @return min value for map entries
     */
    double min() default Integer.MIN_VALUE;

    /**
     * @return max value for map entries
     */
    double max() default Integer.MAX_VALUE;

    /**
     * @return distribution side
     */
    ModConfig.Type type() default ModConfig.Type.COMMON;

    enum RegistryType {

        BLOCKS(PropertyProviderUtils::createBlockBuilder),
        FLUIDS(PropertyProviderUtils::createFluidBuilder),
        ITEMS(PropertyProviderUtils::createItemBuilder),
        BIOMES(PropertyProviderUtils::createBiomeBuilder),
        ENCHANTMENTS(PropertyProviderUtils::createEnchantmentBuilder),
        POTIONS(PropertyProviderUtils::createPotionBuilder),
        POTIONS_TYPES(PropertyProviderUtils::createEffectBuilder),
        ENTITIES(PropertyProviderUtils::createEntityBuilder);

        private final Supplier<EntryCollectionBuilder<?>> builder;

        RegistryType(Supplier<EntryCollectionBuilder<?>> builder) {

            this.builder = builder;
        }

        public EntryCollectionBuilder<?> createBuilder() {

            return this.builder.get();
        }

    }

}
