package com.fuzs.materialmaster.core.provider;

import com.fuzs.materialmaster.MaterialMaster;
import com.fuzs.materialmaster.common.RegisterAttributeHandler;
import com.fuzs.materialmaster.config.ConfigBuildHandler;
import com.fuzs.materialmaster.core.builder.AttributeStringListBuilder;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ConfigPropertyProvider extends AbstractPropertyProvider {

    public static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("C29FA54E-C2BD-4137-897D-D7B363DCA34B"), UUID.fromString("45A03570-9501-4902-ADA9-7E2EF5A2C2D8"), UUID.fromString("CE18081D-3CDB-4288-A45F-99CE6C5530AB"), UUID.fromString("DBC90783-1621-453D-A4E4-739903307E4B")};
    public static final UUID MAINHAND_MODIFIER = UUID.fromString("2202169B-0BD6-4597-B623-DA8F7161F135");

    private final AttributeStringListBuilder parser = new AttributeStringListBuilder();

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

        Map<Item, Multimap<String, AttributeModifier>> attributes = super.getAttributes();
        this.parser.buildAttributeMap(ConfigBuildHandler.KNOCKBACK_RESISTANCE.get(), SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), attributes);
        this.parser.buildAttributeMap(ConfigBuildHandler.ATTACK_DAMAGE.get(), SharedMonsterAttributes.ATTACK_DAMAGE.getName(), attributes);
        this.parser.buildAttributeMap(ConfigBuildHandler.ATTACK_KNOCKBACK.get(), SharedMonsterAttributes.ATTACK_KNOCKBACK.getName(), attributes);
        this.parser.buildAttributeMap(ConfigBuildHandler.ATTACK_SPEED.get(), SharedMonsterAttributes.ATTACK_SPEED.getName(), attributes);
        this.parser.buildAttributeMap(ConfigBuildHandler.REACH_DISTANCE.get(), PlayerEntity.REACH_DISTANCE.getName(), attributes);
        this.parser.buildAttributeMap(ConfigBuildHandler.ATTACK_REACH.get(), RegisterAttributeHandler.ATTACK_REACH.getName(), attributes);
        this.parser.buildAttributeMap(ConfigBuildHandler.ARMOR.get(), SharedMonsterAttributes.ARMOR.getName(), attributes);
        this.parser.buildAttributeMap(ConfigBuildHandler.ARMOR_TOUGHNESS.get(), SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), attributes);

        return attributes;
    }

    @Override
    public Map<Item, Double> getStackSize() {

        return this.parser.buildEntryMap(ConfigBuildHandler.STACK_SIZE.get());
    }

    @Override
    public Map<Item, Double> getDurability() {

        return this.parser.buildEntryMap(ConfigBuildHandler.DURABILITY.get());
    }

    @Override
    public Map<Item, Double> getDigSpeed() {

        return this.parser.buildEntryMap(ConfigBuildHandler.DIG_SPEED.get());
    }

    @Override
    public Map<Item, Double> getHarvestLevel() {

        return this.parser.buildEntryMap(ConfigBuildHandler.HARVEST_LEVEL.get());
    }

    @Override
    public Map<Item, Double> getEnchantability() {

        return this.parser.buildEntryMap(ConfigBuildHandler.ENCHANTABILITY.get());
    }

    @Override
    public Set<UUID> getAttributeIds() {

        Set<UUID> ids = Sets.newHashSet(ARMOR_MODIFIERS);
        ids.add(MAINHAND_MODIFIER);

        return ids;
    }

}
