package com.fuzs.materialmaster.core.property;

import com.fuzs.materialmaster.core.provider.AbstractPropertyProvider;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.function.Function;

public class AttributeItemProperty extends ItemProperty<Multimap<String, AttributeModifier>> {

    public AttributeItemProperty(String name, Function<AbstractPropertyProvider, Map<Item, Multimap<String, AttributeModifier>>> provider) {

        super(name, provider);
    }

    @Override
    protected void add(Item item, Multimap<String, AttributeModifier> value, String name) {

        this.properties.merge(item, value, (map1, map2) -> {

            map1.putAll(map2);
            return map1;
        });
    }

}
