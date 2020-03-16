package com.fuzs.materialmaster.core.provider;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractPropertyProvider {

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

    public Set<UUID> getAttributeIds() {

        return Sets.newHashSet();
    }

}
