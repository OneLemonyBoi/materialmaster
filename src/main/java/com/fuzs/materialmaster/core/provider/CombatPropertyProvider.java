package com.fuzs.materialmaster.core.provider;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Map;
import java.util.UUID;

public class CombatPropertyProvider extends AbstractPropertyProvider {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Sword Blocking Combat";
    }

    @Override
    public Map<Item, Multimap<String, AttributeModifier>> getAttributes() {

        Map<Item, Multimap<String, AttributeModifier>> attributes = Maps.newHashMap();
        this.getAttributeBuilder().putAttackDamage(attributes, Items.DIAMOND_AXE, -1.0);

        return attributes;
    }

    @Override
    public Map<Item, Double> getStackSize() {

        Map<Item, Double> stackSize = Maps.newHashMap();
        stackSize.put(Items.SNOWBALL, 64.0);
        stackSize.put(Items.ENDER_EYE, 16.0);

        return stackSize;
    }

    @Override
    protected UUID getMainhandModifierId() {

        return UUID.fromString("45EDD364-66C9-4D8C-82F3-3CCA76012182");
    }

    @Override
    protected UUID[] getArmorModifierIds() {

        return new UUID[]{UUID.fromString("1ED2D9A5-3DE4-400B-A6D1-1A7F465459FD"), UUID.fromString("728D339B-30FA-48E9-ACD5-E503310CA40F"), UUID.fromString("D907C011-BA14-4253-BBA7-058F108526D1"), UUID.fromString("CFEB08B6-EBC2-406B-8002-8E65A121FD2E")};
    }

}
