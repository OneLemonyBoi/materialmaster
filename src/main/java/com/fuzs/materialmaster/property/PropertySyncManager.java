package com.fuzs.materialmaster.property;

import com.fuzs.materialmaster.common.RegisterAttributeHandler;
import com.fuzs.materialmaster.config.ConfigBuildHandler;
import com.google.common.collect.Maps;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Map;

public class PropertySyncManager {

    private final AttributeStringListBuilder parser = new AttributeStringListBuilder();

    public static Map<Item, Map<String, AttributeModifier>> attributeMap;
    public static Map<Item, Double> digSpeedMap;
    public static Map<Item, Double> harvestLevelMap;
    public static Map<Item, Double> enchantabilityMap;

    public void onModConfig(final ModConfig.ModConfigEvent evt) {

        if (evt.getConfig().getSpec() == ConfigBuildHandler.SPEC) {

            this.sync();
        }
    }

    private void sync() {

        attributeMap = this.syncAttributeMap();
        this.syncStackSize();
        this.syncDurability();
        digSpeedMap = this.syncDigSpeed();
        harvestLevelMap = this.syncHarvestLevel();
        enchantabilityMap = this.syncEnchantability();
    }

    private Map<Item, Map<String, AttributeModifier>> syncAttributeMap() {

        Map<Item, Map<String, AttributeModifier>> map = Maps.newHashMap();
        this.parser.buildAttributeMap(ConfigBuildHandler.KNOCKBACK_RESISTANCE.get(), SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), map);
        this.parser.buildAttributeMap(ConfigBuildHandler.ATTACK_DAMAGE.get(), SharedMonsterAttributes.ATTACK_DAMAGE.getName(), map);
        this.parser.buildAttributeMap(ConfigBuildHandler.ATTACK_KNOCKBACK.get(), SharedMonsterAttributes.ATTACK_KNOCKBACK.getName(), map);
        this.parser.buildAttributeMap(ConfigBuildHandler.ATTACK_SPEED.get(), SharedMonsterAttributes.ATTACK_SPEED.getName(), map);
        this.parser.buildAttributeMap(ConfigBuildHandler.REACH_DISTANCE.get(), PlayerEntity.REACH_DISTANCE.getName(), map);
        this.parser.buildAttributeMap(ConfigBuildHandler.ATTACK_REACH.get(), RegisterAttributeHandler.ATTACK_REACH.getName(), map);
        this.parser.buildAttributeMap(ConfigBuildHandler.ARMOR.get(), SharedMonsterAttributes.ARMOR.getName(), map);
        this.parser.buildAttributeMap(ConfigBuildHandler.ARMOR_TOUGHNESS.get(), SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), map);
        return map;
    }

    private void syncStackSize() {

        this.parser.buildEntryMapWithCondition(ConfigBuildHandler.STACK_SIZE.get(), (item, value) -> value >= 0.0 && value <= 64.0,
                "Stack size out of bounds").forEach((key, value) -> key.maxStackSize = value.intValue());
    }

    private void syncDurability() {

        this.parser.buildEntryMapWithCondition(ConfigBuildHandler.DURABILITY.get(), (item, value) -> item.isDamageable() && value >= 0.0,
                "Item cannot be damaged or durability out of bounds").forEach((key, value) -> key.maxDamage = value.intValue());
    }

    private Map<Item, Double> syncDigSpeed() {

        return this.parser.buildEntryMapWithCondition(ConfigBuildHandler.DIG_SPEED.get(), (item, value) -> value >= 0.0,
                "Dig speed out of bounds");
    }

    private Map<Item, Double> syncHarvestLevel() {

        return this.parser.buildEntryMapWithCondition(ConfigBuildHandler.HARVEST_LEVEL.get(), (item, value) -> item.getItemEnchantability() > 0 && value >= 0.0,
                "Item cannot be enchanted or enchantability out of bounds");
    }

    private Map<Item, Double> syncEnchantability() {

        return this.parser.buildEntryMapWithCondition(ConfigBuildHandler.ENCHANTABILITY.get(), (item, value) -> item.getItemEnchantability() > 0 && value >= 0.0,
                "Item cannot be enchanted or enchantability out of bounds");
    }

}
