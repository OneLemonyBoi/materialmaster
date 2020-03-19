package com.fuzs.materialmaster.asm;

import com.fuzs.materialmaster.common.RegisterAttributeHandler;
import com.fuzs.materialmaster.core.PropertySyncManager;
import com.fuzs.materialmaster.core.property.AttributeItemProperty;
import com.fuzs.materialmaster.core.property.SimpleItemProperty;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("unused")
public class Hooks {

    /**
     * add custom attributes to an item when the attribute map is generated in {@link net.minecraft.item.ItemStack#getAttributeModifiers}
     */
    public static Multimap<String, AttributeModifier> adjustAttributeMap(Multimap<String, AttributeModifier> multimap, EquipmentSlotType equipmentSlot, ItemStack stack) {

        // handle armor differently
        if ((!(stack.getItem() instanceof ArmorItem) && equipmentSlot == EquipmentSlotType.MAINHAND || stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getEquipmentSlot() == equipmentSlot)) {

            multimap.putAll(((AttributeItemProperty) PropertySyncManager.getInstance().getProperty("attributes")).getValue(stack.getItem(), HashMultimap.create()));
        }

        return multimap;
    }

    /**
     * separate attack reach from reach distance, this sets the base value
     * in {@link net.minecraft.client.renderer.GameRenderer#getMouseOver}
     */
    @OnlyIn(Dist.CLIENT)
    public static double getAttackReachDistance() {

        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {

            double attrib = player.getAttribute(RegisterAttributeHandler.ATTACK_REACH).getValue();
            return player.abilities.isCreativeMode ? attrib : attrib - 0.5;
        }

        return 0.0;
    }

    /**
     * separate attack reach from reach distance, this sets a squared value
     * in {@link net.minecraft.client.renderer.GameRenderer#getMouseOver}
     */
    @OnlyIn(Dist.CLIENT)
    public static double getSquareAttackDistance(float partialTicks, Entity entity) {

        double attrib = 0.0;
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {

            attrib += player.getAttribute(RegisterAttributeHandler.ATTACK_REACH).getValue();
            attrib -= player.abilities.isCreativeMode ? 0.0F : 0.5;
        }

        RayTraceResult objectMouseOver = entity.func_213324_a(attrib, partialTicks, false);
        Vec3d vec3d = entity.getEyePosition(partialTicks);

        return objectMouseOver.getHitVec().squareDistanceTo(vec3d);
    }

    /**
     * adjust max square distance for finding a pointed entity on the client which is normally hardcoded to 9.0
     * in {@link net.minecraft.client.renderer.GameRenderer#getMouseOver}
     */
    @OnlyIn(Dist.CLIENT)
    public static double getMaxSquareRange(double d0) {

        return Math.pow(d0 + 0.5, 2);
    }

    /**
     * adjust max square distance for finding a pointed entity on the server which is normally hardcoded to 9.0
     * in {@link net.minecraft.network.play.ServerPlayNetHandler#processUseEntity}
     */
    public static double getEntityReachDistance(ServerPlayerEntity player, Entity entity) {

        double d0 = Math.pow(player.getAttribute(RegisterAttributeHandler.ATTACK_REACH).getValue() + 0.5, 2);
        return player.canEntityBeSeen(entity) ? d0 : d0 / 4.0;
    }

    /**
     * set default knockback strength from attribute instead of zero in {@link net.minecraft.entity.player.PlayerEntity#attackTargetEntityWithCurrentItem}
     * the attribute is not registered by default for players, this is done separately
     */
    public static int getKnockbackAttribute(PlayerEntity player) {

        return (int) Math.ceil(player.getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).getValue());
    }

    /**
     * pass through harvest level in {@link net.minecraft.item.Item#getHarvestLevel}
     * just make sure to not change anything if it had not been present before
     */
    public static int getHarvestLevel(int toolLevel, Item item) {

        return toolLevel != -1 ? ((SimpleItemProperty) PropertySyncManager.getInstance().getProperty("harvest_level")).getValue(item, (double) toolLevel).intValue() : toolLevel;
    }

    /**
     * pass through enchantabilty in {@link net.minecraft.enchantment.EnchantmentHelper#calcItemStackEnchantability}
     * and {@link net.minecraft.enchantment.EnchantmentHelper#buildEnchantmentList}
     */
    public static int getItemEnchantability(int enchantability, ItemStack stack) {

        return ((SimpleItemProperty) PropertySyncManager.getInstance().getProperty("enchantability")).getValue(stack.getItem(), (double) enchantability).intValue();
    }

}
