package com.fuzs.materialmaster.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public class ConfigBuildHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue BYPASS_CONTAINER;

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

        BUILDER.push("general");
        BYPASS_CONTAINER = ConfigBuildHandler.BUILDER.comment("Bypass activation range for containers which normally is 8 blocks. Meant to be used together with increasing the reach distance attribute.\nEnabling this patch may introduce unforeseen issues.").define("Bypass Container Range", false);
        BUILDER.pop();

        BUILDER.comment("Set default values for all attributes used by this mod.\nActual default range might differ with certain mods like \"AttributeFix\" installed.");
        BUILDER.push("default_attribute_values");
        DEFAULT_KNOCKBACK_RESISTANCE = ConfigBuildHandler.BUILDER.comment(createDescription("Chance to not receive any knockback from an attack.", 0.0, 1.0)).defineInRange("Default Knockback Resistance", 0.0, Double.MIN_VALUE, Double.MAX_VALUE);
        DEFAULT_ATTACK_DAMAGE = ConfigBuildHandler.BUILDER.comment(createDescription("How much melee damage is dealt when fighting.", 0.0, 2048.0)).defineInRange("Default Attack Damage", 1.0, Double.MIN_VALUE, Double.MAX_VALUE);
        DEFAULT_ATTACK_KNOCKBACK = ConfigBuildHandler.BUILDER.comment(createDescription("How much knockback is dealt when hitting an entity.", 0.0, 5.0)).defineInRange("Default Attack Knockback", 0.0, Double.MIN_VALUE, Double.MAX_VALUE);
        DEFAULT_ATTACK_SPEED = ConfigBuildHandler.BUILDER.comment(createDescription("Speed at which the attack cooldown recharges. Higher values make it recharge faster.", 0.0, 1024.0)).defineInRange("Default Attack Speed", 4.0, Double.MIN_VALUE, Double.MAX_VALUE);
        DEFAULT_REACH_DISTANCE = ConfigBuildHandler.BUILDER.comment(createDescription("Number of blocks it is possible to interact with the world from.", 0.0, 1024.0)).defineInRange("Default Reach Distance", 5.0, Double.MIN_VALUE, Double.MAX_VALUE);
        DEFAULT_ATTACK_REACH = ConfigBuildHandler.BUILDER.comment(createDescription("From how many blocks away it is possible to interact with entities.", 0.0, 1024.0)).defineInRange("Default Attack Reach", 5.0, Double.MIN_VALUE, Double.MAX_VALUE);
        DEFAULT_ARMOR = ConfigBuildHandler.BUILDER.comment(createDescription("Amount of armor protection.", 0.0, 30.0)).defineInRange("Default Armor", 0.0, Double.MIN_VALUE, Double.MAX_VALUE);
        DEFAULT_ARMOR_TOUGHNESS = ConfigBuildHandler.BUILDER.comment(createDescription("Amount of armor toughness.", 0.0, 20.0)).defineInRange("Default Armor Toughness", 0.0, Double.MIN_VALUE, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.comment("Allows changing various attribute stats of items. Provided values are added to the attribute, they will not replace it.\nFor base values check defaults section of the config. Actual default range might differ with certain mods like \"AttributeFix\" installed.\nFormat for every entry is \"<namespace>:<id>,<value>\".");
        BUILDER.push("attribute_item_stats");
        KNOCKBACK_RESISTANCE = ConfigBuildHandler.BUILDER.comment(createDescription("Chance to not receive any knockback from an attack.", 0.0, 1.0)).define("Knockback Resistance List", Lists.newArrayList());
        ATTACK_DAMAGE = ConfigBuildHandler.BUILDER.comment(createDescription("How much melee damage is dealt when fighting.", 0.0, 2048.0)).define("Attack Damage List", Lists.newArrayList());
        ATTACK_KNOCKBACK = ConfigBuildHandler.BUILDER.comment(createDescription("How much knockback is dealt when hitting an entity.", 0.0, 5.0)).define("Attack Knockback List", Lists.newArrayList());
        ATTACK_SPEED = ConfigBuildHandler.BUILDER.comment(createDescription("Speed at which the attack cooldown recharges. Higher values make it recharge faster.", 0.0, 1024.0)).define("Attack Speed List", Lists.newArrayList());
        REACH_DISTANCE = ConfigBuildHandler.BUILDER.comment(createDescription("Number of blocks it is possible to interact with the world from.", 0.0, 1024.0)).define("Reach Distance List", Lists.newArrayList());
        ATTACK_REACH = ConfigBuildHandler.BUILDER.comment(createDescription("From how many blocks away it is possible to interact with entities.", 0.0, 1024.0)).define("Attack Reach List", Lists.newArrayList());
        ARMOR = ConfigBuildHandler.BUILDER.comment(createDescription("Amount of armor protection.", 0.0, 30.0)).define("Armor List", Lists.newArrayList());
        ARMOR_TOUGHNESS = ConfigBuildHandler.BUILDER.comment(createDescription("Amount of armor toughness.", 0.0, 20.0)).define("Armor Toughness List", Lists.newArrayList());
        BUILDER.pop();

        BUILDER.comment("Allows changing various property stats of items.\nFormat for every entry is \"<namespace>:<id>,<value>\".");
        BUILDER.push("property_item_stats");
        STACK_SIZE = ConfigBuildHandler.BUILDER.comment(createDescription("Specify max stack size for any item.", 0.0, 64.0, "Must not have durability")).define("Stack Size List", Lists.newArrayList());
        DURABILITY = ConfigBuildHandler.BUILDER.comment(createDescription("Change durability for any damageable item.\nSetting to 0 will make the item unbreakable.", "Must have durability")).define("Durability List", Lists.newArrayList());
        DIG_SPEED = ConfigBuildHandler.BUILDER.comment(createDescription("Change dig speed value for any item.\nSetting to 0 will prevent the item from mining anything.")).define("Dig Speed List", Lists.newArrayList());
        HARVEST_LEVEL = ConfigBuildHandler.BUILDER.comment(createDescription("Change harvest level value for any tool item.\nSetting to -1 will remove any harvest level present.", "Must be a tool")).define("Harvest Level List", Lists.newArrayList());
        ENCHANTABILITY = ConfigBuildHandler.BUILDER.comment(createDescription("Change enchantability value for any enchantable item.\nSetting to 0 will make enchanting this item impossible.", "Must be enchantable")).define("Enchantability List", Lists.newArrayList());
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private static String createDescription(String text) {

        return createDescription(text, 0.0, Integer.MAX_VALUE);
    }

    private static String createDescription(String text, String condition) {

        return createDescription(text, 0.0, Integer.MAX_VALUE, condition);
    }

    private static String createDescription(String text, double min, double max) {

        return text + "\nDefault Range: " + min + " ~ " + max;
    }

    private static String createDescription(String text, double min, double max, String condition) {

        return createDescription(text, min, max) + "\nCondition: " + condition;
    }

}