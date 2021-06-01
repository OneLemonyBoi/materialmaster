package onelemonyboi.materialmaster.asm;

import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.math.vector.Vector3d;
import onelemonyboi.materialmaster.api.MaterialMasterReference;
import onelemonyboi.materialmaster.common.handler.RegisterAttributeHandler;
import onelemonyboi.materialmaster.api.core.PropertySyncManager;
import onelemonyboi.materialmaster.api.core.storage.AttributeItemProperty;
import onelemonyboi.materialmaster.api.core.storage.SimpleItemProperty;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("unused")
public class Hooks {

    /**
     * add custom attributes to an item when the attribute map is generated in {@link net.minecraft.item.ItemStack#getAttributeModifiers}
     */
    public static Multimap<String, AttributeModifier> adjustAttributeMap(Multimap<String, AttributeModifier> multimap, EquipmentSlotType equipmentSlot, ItemStack stack) {

        // handle armor differently
        if ((!(stack.getItem() instanceof ArmorItem) && equipmentSlot == EquipmentSlotType.MAINHAND
                || stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getEquipmentSlot() == equipmentSlot)) {

            return new ImmutableMultimap.Builder<String, AttributeModifier>()
                    .putAll(multimap)
                    .putAll(((AttributeItemProperty) PropertySyncManager.getInstance()
                            .getProperty(PropertySyncManager.PropertyType.ATTRIBUTES))
                            .getValue(stack.getItem(), HashMultimap.create()))
                    .build();
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

            double attribute = player.getAttribute(MaterialMasterReference.ATTACK_REACH).getValue();
            return player.abilities.isCreativeMode ? attribute : attribute - RegisterAttributeHandler.ATTACK_REACH_CREATIVE_BOOST;
        }

        return 0.0;
    }

    /**
     * separate attack reach from reach distance, this sets a squared value
     * in {@link net.minecraft.client.renderer.GameRenderer#getMouseOver}
     */
    @OnlyIn(Dist.CLIENT)
    public static double getSquareAttackDistance(float partialTicks, Entity entity) {

        double attribute = 0.0;
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {

            attribute += player.getAttribute(MaterialMasterReference.ATTACK_REACH).getValue();
            attribute -= player.abilities.isCreativeMode ? 0.0F : RegisterAttributeHandler.ATTACK_REACH_CREATIVE_BOOST;
        }

        RayTraceResult objectMouseOver = entity.pick(attribute, partialTicks, false);
        Vector3d vec3d = entity.getEyePosition(partialTicks);

        return objectMouseOver.getHitVec().squareDistanceTo(vec3d);
    }

    /**
     * adjust max square distance for finding a pointed entity on the client which is normally hardcoded to 9.0
     * in {@link net.minecraft.client.renderer.GameRenderer#getMouseOver}
     */
    @OnlyIn(Dist.CLIENT)
    public static double getMaxSquareRange(double d0) {

        return Math.pow(d0, 2);
    }

    /**
     * adjust max square distance for finding a pointed entity on the server which is normally hardcoded to 36.0
     * in {@link net.minecraft.network.play.ServerPlayNetHandler#processUseEntity}
     */
    public static double getEntityReachDistance(ServerPlayerEntity player, Entity entity) {

        double d0 = Math.pow(player.getAttribute(MaterialMasterReference.ATTACK_REACH).getValue(), 2);
        return player.canEntityBeSeen(entity) ? d0 : d0 / 4.0;
    }

    /**
     * set default knockback strength from attribute instead of zero in {@link net.minecraft.entity.player.PlayerEntity#attackTargetEntityWithCurrentItem}
     * the attribute is not registered by default for players, this is done separately
     */
    public static int getKnockbackAttribute(PlayerEntity player) {

        return (int) Math.ceil(player.getAttribute(Attributes.ATTACK_KNOCKBACK).getValue());
    }

    /**
     * pass through harvest level in {@link net.minecraft.item.Item#getHarvestLevel}
     * just make sure to not change anything if it had not been present before
     */
    public static int getHarvestLevel(int toolLevel, Item item) {

        return toolLevel != -1 ? ((SimpleItemProperty) PropertySyncManager.getInstance()
                .getProperty(PropertySyncManager.PropertyType.HARVEST_LEVEL))
                .getValue(item, (double) toolLevel).intValue() : toolLevel;
    }

    /**
     * pass through enchantabilty in {@link net.minecraft.enchantment.EnchantmentHelper#calcItemStackEnchantability}
     * and {@link net.minecraft.enchantment.EnchantmentHelper#buildEnchantmentList}
     */
    public static int getItemEnchantability(int enchantability, ItemStack stack) {

        return ((SimpleItemProperty) PropertySyncManager.getInstance()
                .getProperty(PropertySyncManager.PropertyType.ENCHANTABILITY))
                .getValue(stack.getItem(), (double) enchantability).intValue();
    }

}
