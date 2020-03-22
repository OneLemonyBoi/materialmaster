package com.fuzs.materialmaster.api;

import com.fuzs.materialmaster.api.builder.EntryCollectionBuilder;
import com.fuzs.materialmaster.api.provider.AbstractPropertyProvider;
import com.fuzs.materialmaster.common.handler.RegisterAttributeHandler;
import com.fuzs.materialmaster.core.ModSyncManager;
import com.fuzs.materialmaster.core.PropertySyncManager;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
public class PropertyProviderUtils {

    /**
     * @return attack reach attribute registered by this mod
     */
    public static IAttribute getAttackReachAttribute() {

        return RegisterAttributeHandler.ATTACK_REACH;
    }

    /**
     * @param player player to get instance from
     * @return attack reach attribute instance registered by this mod
     */
    public static IAttributeInstance getAttackReachForPlayer(PlayerEntity player) {

        return player.getAttribute(RegisterAttributeHandler.ATTACK_REACH);
    }

    /**
     * @param player player to get value from
     * @return attack reach attribute value registered by this mod
     */
    public static double getAttackReachFromPlayer(PlayerEntity player) {

        return player.getAttribute(RegisterAttributeHandler.ATTACK_REACH).getValue();
    }

    /**
     * old way of registering property providers manually, use {@link #registerModProvider()} instead
     * @param provider provider object to be registered
     */
    @Deprecated
    public static void registerProvider(AbstractPropertyProvider provider) {

        PropertySyncManager.getInstance().registerPropertyProvider(ModLoadingContext.get().getActiveContainer().getModId(), provider);
    }

    /**
     * register a mod to be searched for {@link com.fuzs.materialmaster.api.SyncProvider} annotations
     */
    public static void registerModProvider(String modid) {

        ModSyncManager.getInstance().registerModProvider(modid);
    }

    /**
     * register the current mod to be searched for {@link com.fuzs.materialmaster.api.SyncProvider} annotations
     */
    public static void registerModProvider() {

        registerModProvider(ModLoadingContext.get().getActiveContainer().getModId());
    }

    public static EntryCollectionBuilder<Block> createBlockBuilder() {

        return new EntryCollectionBuilder<>(ForgeRegistries.BLOCKS);
    }

    public static EntryCollectionBuilder<Fluid> createFluidBuilder() {

        return new EntryCollectionBuilder<>(ForgeRegistries.FLUIDS);
    }

    public static EntryCollectionBuilder<Item> createItemBuilder() {

        return new EntryCollectionBuilder<>(ForgeRegistries.ITEMS);
    }

    public static EntryCollectionBuilder<Biome> createBiomeBuilder() {

        return new EntryCollectionBuilder<>(ForgeRegistries.BIOMES);
    }

    public static EntryCollectionBuilder<Enchantment> createEnchantmentBuilder() {

        return new EntryCollectionBuilder<>(ForgeRegistries.ENCHANTMENTS);
    }

    public static EntryCollectionBuilder<Effect> createEffectBuilder() {

        return new EntryCollectionBuilder<>(ForgeRegistries.POTIONS);
    }

    public static EntryCollectionBuilder<Potion> createPotionBuilder() {

        return new EntryCollectionBuilder<>(ForgeRegistries.POTION_TYPES);
    }

    public static EntryCollectionBuilder<EntityType<?>> createEntityBuilder() {

        return new EntryCollectionBuilder<>(ForgeRegistries.ENTITIES);
    }

}
