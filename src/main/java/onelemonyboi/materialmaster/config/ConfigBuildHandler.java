package onelemonyboi.materialmaster.config;

import onelemonyboi.materialmaster.common.handler.RegisterAttributeHandler;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public class ConfigBuildHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.DoubleValue DEFAULT_MAX_HEALTH;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_KNOCKBACK_RESISTANCE;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_MOVEMENT_SPEED;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_ATTACK_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_ATTACK_KNOCKBACK;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_ATTACK_SPEED;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_ARMOR;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_ARMOR_TOUGHNESS;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_LUCK;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_SWIM_SPEED;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_GRAVITY;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_REACH_DISTANCE;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_ATTACK_REACH;

    public static final ForgeConfigSpec.ConfigValue<List<String>> MAX_HEALTH;
    public static final ForgeConfigSpec.ConfigValue<List<String>> KNOCKBACK_RESISTANCE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> MOVEMENT_SPEED;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ATTACK_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ATTACK_KNOCKBACK;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ATTACK_SPEED;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ARMOR;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ARMOR_TOUGHNESS;
    public static final ForgeConfigSpec.ConfigValue<List<String>> LUCK;
    public static final ForgeConfigSpec.ConfigValue<List<String>> SWIM_SPEED;
    public static final ForgeConfigSpec.ConfigValue<List<String>> GRAVITY;
    public static final ForgeConfigSpec.ConfigValue<List<String>> REACH_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ATTACK_REACH;

    public static final ForgeConfigSpec.ConfigValue<List<String>> STACK_SIZE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> DURABILITY;
    public static final ForgeConfigSpec.ConfigValue<List<String>> DIG_SPEED;
    public static final ForgeConfigSpec.ConfigValue<List<String>> HARVEST_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ENCHANTABILITY;

    static {

        BUILDER.comment("Set default values for all attributes used by the player.", "Actual range might differ with certain mods like \"AttributeFix\" installed. Changes require relogging to apply.");
        BUILDER.push("default_attribute_values");
        DEFAULT_MAX_HEALTH = ConfigBuildHandler.BUILDER.comment(createDescription("Amount of health when fully healed.", 0.0, 1024.0)).defineInRange("Default Max Health", 20.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        DEFAULT_KNOCKBACK_RESISTANCE = ConfigBuildHandler.BUILDER.comment(createDescription("Chance to not receive any knockback from an attack.", 0.0, 1.0)).defineInRange("Default Knockback Resistance", 0.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        DEFAULT_MOVEMENT_SPEED = ConfigBuildHandler.BUILDER.comment(createDescription("Movement speed when walking on land.", 0.0, 1024.0)).defineInRange("Default Movement Speed", 0.1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        DEFAULT_ATTACK_DAMAGE = ConfigBuildHandler.BUILDER.comment(createDescription("Amount of melee damage dealt when fighting.", 0.0, 2048.0)).defineInRange("Default Attack Damage", 1.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        DEFAULT_ATTACK_KNOCKBACK = ConfigBuildHandler.BUILDER.comment(createDescription("How much knockback is dealt when hitting an entity.", 0.0, 5.0)).defineInRange("Default Attack Knockback", 0.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        DEFAULT_ATTACK_SPEED = ConfigBuildHandler.BUILDER.comment(createDescription("Speed at which the attack cooldown recharges, higher values make it recharge faster.", 0.0, 1024.0)).defineInRange("Default Attack Speed", 4.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        DEFAULT_ARMOR = ConfigBuildHandler.BUILDER.comment(createDescription("Amount of armor protection.", 0.0, 30.0)).defineInRange("Default Armor", 0.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        DEFAULT_ARMOR_TOUGHNESS = ConfigBuildHandler.BUILDER.comment(createDescription("Amount of armor toughness.", 0.0, 20.0)).defineInRange("Default Armor Toughness", 0.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        DEFAULT_LUCK = ConfigBuildHandler.BUILDER.comment(createDescription("Luck property when using loot tables.", -1024.0, 1024.0)).defineInRange("Default Luck", 0.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        DEFAULT_SWIM_SPEED = ConfigBuildHandler.BUILDER.comment(createDescription("Movement speed when swimming in of water.", 0.0, 1024.0)).defineInRange("Default Swim Speed", 1.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        DEFAULT_GRAVITY = ConfigBuildHandler.BUILDER.comment(createDescription("Vertical motion multiplier, mainly used when falling.", -8.0, 8.0)).defineInRange("Default Gravity", 0.08, Integer.MIN_VALUE, Integer.MAX_VALUE);
        DEFAULT_REACH_DISTANCE = ConfigBuildHandler.BUILDER.comment(createDescription("Distance for interacting with blocks in the world, " + RegisterAttributeHandler.REACH_DISTANCE_CREATIVE_BOOST + " is added when in creative mode.", 0.0, 1024.0)).defineInRange("Default Reach Distance", 4.5, Integer.MIN_VALUE, Integer.MAX_VALUE);
        DEFAULT_ATTACK_REACH = ConfigBuildHandler.BUILDER.comment(createDescription("Number of blocks for interacting with entities, " + RegisterAttributeHandler.ATTACK_REACH_CREATIVE_BOOST + " is added when in creative mode.", 0.0, 1024.0)).defineInRange("Default Attack Reach", 3.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.comment("Allows changing various attribute stats of items. Provided values are added to the attribute, they will not replace it.", "For base values check defaults section of the config. Actual range might differ with certain mods like \"AttributeFix\" installed.", "Format for every entry is \"<namespace>:<path>,<value>\". Path may use single asterisk as wildcard parameter.");
        BUILDER.push("attribute_item_stats");
        MAX_HEALTH = ConfigBuildHandler.BUILDER.comment(createDescription("Amount of health when fully healed.", 0.0, 1024.0)).define("Max Health List", Lists.newArrayList());
        KNOCKBACK_RESISTANCE = ConfigBuildHandler.BUILDER.comment(createDescription("Chance to not receive any knockback from an attack.", 0.0, 1.0)).define("Knockback Resistance List", Lists.newArrayList());
        MOVEMENT_SPEED = ConfigBuildHandler.BUILDER.comment(createDescription("Movement speed when walking on land.", 0.0, 1024.0)).define("Movement Speed List", Lists.newArrayList());
        ATTACK_DAMAGE = ConfigBuildHandler.BUILDER.comment(createDescription("Amount of melee damage dealt when fighting.", 0.0, 2048.0)).define("Attack Damage List", Lists.newArrayList());
        ATTACK_KNOCKBACK = ConfigBuildHandler.BUILDER.comment(createDescription("How much knockback is dealt when hitting an entity.", 0.0, 5.0)).define("Attack Knockback List", Lists.newArrayList());
        ATTACK_SPEED = ConfigBuildHandler.BUILDER.comment(createDescription("Speed at which the attack cooldown recharges, higher values make it recharge faster.", 0.0, 1024.0)).define("Attack Speed List", Lists.newArrayList());
        ARMOR = ConfigBuildHandler.BUILDER.comment(createDescription("Amount of armor protection.", 0.0, 30.0)).define("Armor List", Lists.newArrayList());
        ARMOR_TOUGHNESS = ConfigBuildHandler.BUILDER.comment(createDescription("Amount of armor toughness.", 0.0, 20.0)).define("Armor Toughness List", Lists.newArrayList());
        LUCK = ConfigBuildHandler.BUILDER.comment(createDescription("Luck property when using loot tables.", -1024.0, 1024.0)).define("Luck List", Lists.newArrayList());
        SWIM_SPEED = ConfigBuildHandler.BUILDER.comment(createDescription("Movement speed when swimming in of water.", 0.0, 1024.0)).define("Swim Speed List", Lists.newArrayList());
        GRAVITY = ConfigBuildHandler.BUILDER.comment(createDescription("Vertical motion multiplier, mainly used when falling.", -8.0, 8.0)).define("Gravity List", Lists.newArrayList());
        REACH_DISTANCE = ConfigBuildHandler.BUILDER.comment(createDescription("Distance for interacting with blocks in the world.", 0.0, 1024.0)).define("Reach Distance List", Lists.newArrayList());
        ATTACK_REACH = ConfigBuildHandler.BUILDER.comment(createDescription("Number of blocks for interacting with entities.", 0.0, 1024.0)).define("Attack Reach List", Lists.newArrayList());
        BUILDER.pop();

        BUILDER.comment("Allows changing various property stats of items.", "Format for every entry is \"<namespace>:<path>,<value>\". Path may use single asterisk as wildcard parameter.");
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

        return text + "\nActual Range: " + min + " ~ " + max;
    }

    private static String createDescription(String text, double min, double max, String condition) {

        return createDescription(text, min, max) + "\nCondition: " + condition;
    }

}