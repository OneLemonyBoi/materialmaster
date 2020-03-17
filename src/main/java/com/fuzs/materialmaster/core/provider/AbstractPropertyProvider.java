package com.fuzs.materialmaster.core.provider;

import com.fuzs.materialmaster.core.builder.AttributeMapBuilder;
import com.google.common.collect.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractPropertyProvider {

    private AttributeMapBuilder attributeBuilder;

    protected final AttributeMapBuilder getAttributeBuilder() {

        if (this.attributeBuilder == null) {

            this.attributeBuilder = new AttributeMapBuilder(this.getMainhandModifierId(), this.getArmorModifierIds());
        }

        return this.attributeBuilder;
    }

    public abstract boolean isEnabled();

    public abstract String getName();

    public Map<Item, Multimap<String, AttributeModifier>> getAttributes() {

        return Maps.newHashMap();
    }

    public Map<Item, Double> getStackSize() {

        return Maps.newHashMap();
    }

    public Map<Item, Double> getDurability() {

        return Maps.newHashMap();
    }

    public Map<Item, Double> getDigSpeed() {

        return Maps.newHashMap();
    }

    public Map<Item, Double> getHarvestLevel() {

        return Maps.newHashMap();
    }

    public Map<Item, Double> getEnchantability() {

        return Maps.newHashMap();
    }

    protected abstract UUID getMainhandModifierId();

    protected abstract UUID[] getArmorModifierIds();

    public final Set<UUID> getAttributeIds() {

        Set<UUID> ids = Sets.newHashSet(this.getArmorModifierIds());
        ids.add(this.getMainhandModifierId());

        return ids;
    }

}
