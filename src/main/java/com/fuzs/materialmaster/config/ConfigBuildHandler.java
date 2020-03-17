package com.fuzs.materialmaster.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ConfigBuildHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.DoubleValue DEFAULT_KNOCKBACK_RESISTANCE;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_ATTACK_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_ATTACK_KNOCKBACK;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_ATTACK_SPEED;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_REACH_DISTANCE;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_ATTACK_REACH;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_ARMOR;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_ARMOR_TOUGHNESS;

    public static final ForgeConfigSpec.ConfigValue<List<String>> KNOCKBACK_RESISTANCE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ATTACK_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ATTACK_KNOCKBACK;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ATTACK_SPEED;
    public static final ForgeConfigSpec.ConfigValue<List<String>> REACH_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ATTACK_REACH;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ARMOR;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ARMOR_TOUGHNESS;

    public static final ForgeConfigSpec.ConfigValue<List<String>> STACK_SIZE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> DURABILITY;
    public static final ForgeConfigSpec.ConfigValue<List<String>> DIG_SPEED;
    public static final ForgeConfigSpec.ConfigValue<List<String>> HARVEST_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ENCHANTABILITY;

    static {

        BUILDER.comment("Set default values for all attributes able to be changed by this mod.");
        BUILDER.push("default_attribute_values");
        DEFAULT_KNOCKBACK_RESISTANCE = ConfigBuildHandler.BUILDER.comment("Chance for the player to not receive any knockback from an attack.").defineInRange("Default Knockback Resistance", 0.0, 0.0, 1.0);
        DEFAULT_ATTACK_DAMAGE = ConfigBuildHandler.BUILDER.comment("How much melee damage the player deals when fighting without a dedicated weapon or tool.").defineInRange("Default Attack Damage", 1.0, 0.0, 2048.0);
        DEFAULT_ATTACK_KNOCKBACK = ConfigBuildHandler.BUILDER.comment("How much knockback the player deals when hitting another entity.").defineInRange("Default Attack Knockback", 0.0, 0.0, 5.0);
        DEFAULT_ATTACK_SPEED = ConfigBuildHandler.BUILDER.comment("Speed at which the attack cooldown recharges. Higher values make it recharge faster.").defineInRange("Default Attack Speed", 4.0, 0.0, 1024.0);
        DEFAULT_REACH_DISTANCE = ConfigBuildHandler.BUILDER.comment("Number of blocks the player can interact with the world from.").defineInRange("Default Reach Distance", 5.0, 0.0, 1024.0);
        DEFAULT_ATTACK_REACH = ConfigBuildHandler.BUILDER.comment("From how many blocks away the player can interact with entities. Has been reduced to 3.5 in Combat Test Snapshots.").defineInRange("Default Attack Reach", 5.0, 0.0, 1024.0);
        DEFAULT_ARMOR = ConfigBuildHandler.BUILDER.comment("Amount of armor protection.").defineInRange("Default Armor", 0.0, 0.0, 30.0);
        DEFAULT_ARMOR_TOUGHNESS = ConfigBuildHandler.BUILDER.comment("Amount of armor toughness.").defineInRange("Default Armor Toughness", 0.0, 0.0, 20.0);
        BUILDER.pop();

        BUILDER.comment("Allows changing various attribute stats of items. Format for every entry is \"<namespace>:<id>,<value>\". Value parameter must be of a certain range, check default section for that.");
        BUILDER.push("attribute_item_stats");
        KNOCKBACK_RESISTANCE = ConfigBuildHandler.BUILDER.comment("Specify a value added to the knockback resistance attribute for any item.").define("Knockback Resistance List", Lists.newArrayList());
        ATTACK_DAMAGE = ConfigBuildHandler.BUILDER.comment("Specify a value added to the attack damage attribute for any item.").define("Attack Damage List", Lists.newArrayList());
        ATTACK_KNOCKBACK = ConfigBuildHandler.BUILDER.comment("Specify a value added to the attack knockback attribute for any item.").define("Attack Knockback List", Lists.newArrayList());
        ATTACK_SPEED = ConfigBuildHandler.BUILDER.comment("Specify a value added to the attack speed attribute for any item.").define("Attack Speed List", Lists.newArrayList());
        REACH_DISTANCE = ConfigBuildHandler.BUILDER.comment("Specify a value added to the reach distance attribute for any item.").define("Reach Distance List", Lists.newArrayList());
        ATTACK_REACH = ConfigBuildHandler.BUILDER.comment("Specify a value added to the attack reach attribute for any item.").define("Attack Reach List", Lists.newArrayList());
        ARMOR = ConfigBuildHandler.BUILDER.comment("Specify a value added to the armor attribute for any item.").define("Armor List", Lists.newArrayList());
        ARMOR_TOUGHNESS = ConfigBuildHandler.BUILDER.comment("Specify a value added to the armor toughness attribute for any item.").define("Armor Toughness List", Lists.newArrayList());
        BUILDER.pop();

        BUILDER.comment("Allows changing various property stats of items. Format for every entry is \"<namespace>:<id>,<value>\".");
        BUILDER.push("property_item_stats");
        STACK_SIZE = ConfigBuildHandler.BUILDER.comment("Specify the max stack size for any item. Value has to be between 0 and 64.").define("Stack Size List", Lists.newArrayList());
        DURABILITY = ConfigBuildHandler.BUILDER.comment("Change the durability for any damageable item. Setting it to 0 will make the item unbreakable.").define("Durability List", Lists.newArrayList());
        DIG_SPEED = ConfigBuildHandler.BUILDER.comment("Change the dig speed value for any item. Setting it to 0 will make it impossible to enchant the item.\nThis is a fun little test.\nNow we wait").define("Dig Speed List", Lists.newArrayList());
        HARVEST_LEVEL = ConfigBuildHandler.BUILDER.comment("Change the harvest level value for any tool item. Setting it to 0 will make it impossible to enchant the item.").define("Harvest Level List", Lists.newArrayList());
        ENCHANTABILITY = ConfigBuildHandler.BUILDER.comment("Change the enchantability value for any enchantable item. Setting the value to 0 will make enchanting this item impossible.").define("Enchantability List", Lists.newArrayList());
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

}