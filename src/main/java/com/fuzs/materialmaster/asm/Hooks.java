package com.fuzs.materialmaster.asm;

import com.fuzs.materialmaster.common.RegisterAttributeHandler;
import com.fuzs.materialmaster.property.PropertySyncManager;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
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

import java.util.Map;

@SuppressWarnings("unused")
public class Hooks {

    /**
     * add custom attributes to an item when the attribute mmap is generated in {@link net.minecraft.item.Item#getAttributeModifiers}
     * attributes already present will be modified to ensure the item tooltip displaying values properly
     */
    public static Multimap<String, AttributeModifier> adjustAttributeMap(Multimap<String, AttributeModifier> multimap, EquipmentSlotType equipmentSlot, ItemStack stack) {

        Map<Item, Map<String, AttributeModifier>> itemMap = PropertySyncManager.attributeMap;
        if (itemMap != null && (!(stack.getItem() instanceof ArmorItem) && equipmentSlot == EquipmentSlotType.MAINHAND || stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getEquipmentSlot() == equipmentSlot)) {

            Map<String, AttributeModifier> attributeMap = itemMap.get(stack.getItem());
            if (attributeMap != null) {

                multimap.putAll(Multimaps.forMap(attributeMap));
            }
        }

        return multimap;
    }

    /**
     * separate attack reach from reach distance, this sets the base value
     */
    public static double getAttackReachDistance() {

        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {

            double attrib = player.getAttribute(RegisterAttributeHandler.ATTACK_REACH).getValue();
            return player.abilities.isCreativeMode ? attrib : attrib - 0.5F;
        }

        return 0.0;
    }

    /**
     * separate attack reach from reach distance, this sets a squared value
     */
    public static double getSquareAttackDistance(float partialTicks, Entity entity) {

        double attrib = 0;
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {

            attrib += player.getAttribute(RegisterAttributeHandler.ATTACK_REACH).getValue();
            attrib -= player.abilities.isCreativeMode ? 0.0F : 0.5F;
        }

        RayTraceResult objectMouseOver = entity.pick(attrib, partialTicks, false);
        Vec3d vec3d = entity.getEyePosition(partialTicks);

        return objectMouseOver.getHitVec().squareDistanceTo(vec3d);
    }

    /**
     * adjust max square distance for finding a pointed entity on the client which is normally hardcoded to 9.0
     */
    public static double getMaxSquareRange(double d0) {

        return Math.pow(d0 * 2.0 / 3.0, 2.0);
    }

    /**
     * adjust max square distance for finding a pointed entity on the server which is normally hardcoded to 9.0
     */
    public static double getEntityReachDistance(ServerPlayerEntity player, Entity entity) {

        double d0 = Math.pow(player.getAttribute(RegisterAttributeHandler.ATTACK_REACH).getValue(), 2);
        return player.canEntityBeSeen(entity) ? d0 : d0 / 4.0;
    }

    public static int getKnockbackAttribute(PlayerEntity player) {

        return (int) Math.ceil(player.getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).getValue());
    }

    public static int getItemEnchantability(int i, ItemStack stack) {

        int enchantability = PropertySyncManager.enchantabilityMap != null ? PropertySyncManager.enchantabilityMap.getOrDefault(stack.getItem(),
                -1.0).intValue() : -1;
        return enchantability != -1 ? enchantability : i;
    }

}
