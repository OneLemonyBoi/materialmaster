package com.fuzs.materialmaster.core.provider;

import com.fuzs.materialmaster.MaterialMaster;
import com.fuzs.materialmaster.config.ConfigBuildHandler;
import com.fuzs.materialmaster.core.builder.AttributeMapBuilder;
import com.fuzs.materialmaster.core.builder.EntryCollectionBuilder;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.UUID;

public class ConfigPropertyProvider extends AbstractPropertyProvider {

    private final EntryCollectionBuilder<Item> entryBuilder = new EntryCollectionBuilder<>(ForgeRegistries.ITEMS);

    @Override
    public boolean isEnabled() {

        return true;
    }

    @Override
    public String getName() {

        return MaterialMaster.NAME;
    }

    @Override
    public Map<Item, Multimap<String, AttributeModifier>> getAttributes() {

        AttributeMapBuilder builder = AttributeMapBuilder.create(this.getMainhandModifierId(), this.getArmorModifierIds());
        this.entryBuilder.buildEntryMap(ConfigBuildHandler.KNOCKBACK_RESISTANCE.get()).forEach(builder::putKnockbackResistance);
        this.entryBuilder.buildEntryMap(ConfigBuildHandler.ATTACK_DAMAGE.get()).forEach(builder::putAttackDamage);
        this.entryBuilder.buildEntryMap(ConfigBuildHandler.ATTACK_KNOCKBACK.get()).forEach(builder::putAttackKnockback);
        this.entryBuilder.buildEntryMap(ConfigBuildHandler.ATTACK_SPEED.get()).forEach(builder::putAttackSpeed);
        this.entryBuilder.buildEntryMap(ConfigBuildHandler.REACH_DISTANCE.get()).forEach(builder::putReachDistance);
        this.entryBuilder.buildEntryMap(ConfigBuildHandler.ATTACK_REACH.get()).forEach(builder::putAttackReach);
        this.entryBuilder.buildEntryMap(ConfigBuildHandler.ARMOR.get()).forEach(builder::putArmor);
        this.entryBuilder.buildEntryMap(ConfigBuildHandler.ARMOR_TOUGHNESS.get()).forEach(builder::putArmorToughness);

        return builder.build();
    }

    @Override
    public Map<Item, Double> getStackSize() {

        return this.entryBuilder.buildEntryMap(ConfigBuildHandler.STACK_SIZE.get());
    }

    @Override
    public Map<Item, Double> getDurability() {

        return this.entryBuilder.buildEntryMap(ConfigBuildHandler.DURABILITY.get());
    }

    @Override
    public Map<Item, Double> getDigSpeed() {

        return this.entryBuilder.buildEntryMap(ConfigBuildHandler.DIG_SPEED.get());
    }

    @Override
    public Map<Item, Double> getHarvestLevel() {

        return this.entryBuilder.buildEntryMap(ConfigBuildHandler.HARVEST_LEVEL.get());
    }

    @Override
    public Map<Item, Double> getEnchantability() {

        return this.entryBuilder.buildEntryMap(ConfigBuildHandler.ENCHANTABILITY.get());
    }

    @Override
    protected UUID getMainhandModifierId() {

        return UUID.fromString("2202169B-0BD6-4597-B623-DA8F7161F135");
    }

    @Override
    protected UUID[] getArmorModifierIds() {

        return new UUID[]{UUID.fromString("C29FA54E-C2BD-4137-897D-D7B363DCA34B"), UUID.fromString("45A03570-9501-4902-ADA9-7E2EF5A2C2D8"), UUID.fromString("CE18081D-3CDB-4288-A45F-99CE6C5530AB"), UUID.fromString("DBC90783-1621-453D-A4E4-739903307E4B")};
    }

}
