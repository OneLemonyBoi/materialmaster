package com.fuzs.materialmaster.api;

import com.fuzs.materialmaster.api.builder.EntryCollectionBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SyncProvider {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface EntrySet {

        String[] path();

        RegistryType type();

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface EntryMap {

        String[] path();

        RegistryType type();

        double min() default Integer.MIN_VALUE;

        double max() default Integer.MAX_VALUE;

    }

    @SuppressWarnings("unused")
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
