package com.fuzs.materialmaster.core.builder;

import com.fuzs.materialmaster.MaterialMaster;
import com.fuzs.materialmaster.core.provider.ConfigPropertyProvider;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class AttributeStringListBuilder extends StringListBuilder<Item> {

    public AttributeStringListBuilder() {

        super(ForgeRegistries.ITEMS);
    }

    public void buildAttributeMap(List<String> locations, String attribute, Map<Item, Multimap<String, AttributeModifier>> origin) {

        this.buildEntryMap(locations).forEach((item, value) -> {

            UUID modifierId = item instanceof ArmorItem ? ConfigPropertyProvider.ARMOR_MODIFIERS[((ArmorItem) item).getEquipmentSlot().getIndex()] : ConfigPropertyProvider.MAINHAND_MODIFIER;
            AttributeModifier modifier = new AttributeModifier(modifierId, MaterialMaster.NAME + " modifier", value, AttributeModifier.Operation.ADDITION);

            Multimap<String, AttributeModifier> map = HashMultimap.create();
            map.put(attribute, modifier);
            origin.merge(item, map, (map1, map2) -> {

                map1.putAll(map2);
                return map1;
            });
        });
    }

}
