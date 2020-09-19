package com.fuzs.materialmaster.api;

import com.fuzs.materialmaster.api.builder.EntryCollectionBuilder;
import com.fuzs.materialmaster.api.core.ModSyncManager;
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
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class PropertyProviderUtils {

    /**
     * register current mod to be searched for {@link SyncProvider} annotations
     */
    public static void registerSyncProviderMod(String modId, @Nullable ModConfig config) {

        ModSyncManager.getInstance().registerSyncProviderMod(modId, config);
    }

    /**
     * @return attack reach attribute registered by this mod
     */
    public static IAttribute getAttackReachAttribute() {

        return MaterialMasterReference.ATTACK_REACH;
    }

    /**
     * @param player player to get instance from
     * @return attack reach attribute instance registered by this mod
     */
    public static IAttributeInstance getAttackReachForPlayer(PlayerEntity player) {

        return player.getAttribute(MaterialMasterReference.ATTACK_REACH);
    }

    /**
     * @param player player to get value from
     * @return attack reach attribute value registered by this mod
     */
    public static double getAttackReachFromPlayer(PlayerEntity player) {

        return player.getAttribute(MaterialMasterReference.ATTACK_REACH).getValue();
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
