package com.fuzs.materialmaster.property;

import com.fuzs.materialmaster.MaterialMaster;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class AttributeStringListBuilder extends StringListBuilder<Item> {

    public static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("C29FA54E-C2BD-4137-897D-D7B363DCA34B"), UUID.fromString("45A03570-9501-4902-ADA9-7E2EF5A2C2D8"), UUID.fromString("CE18081D-3CDB-4288-A45F-99CE6C5530AB"), UUID.fromString("DBC90783-1621-453D-A4E4-739903307E4B")};
    public static final UUID MAINHAND_MODIFIER = UUID.fromString("2202169B-0BD6-4597-B623-DA8F7161F135");
//    public static final UUID OFFHAND_MODIFIER = UUID.fromString("4BAB58F7-1BB5-47EB-9A5F-8EB41ECB2289");

    public AttributeStringListBuilder() {

        super(ForgeRegistries.ITEMS);
    }

    public void buildAttributeMap(List<String> locations, String attribute, Map<Item, Map<String, AttributeModifier>> origin) {

        this.buildEntryMap(locations).forEach((key, value) -> {

            UUID modifierId = key instanceof ArmorItem ? ARMOR_MODIFIERS[((ArmorItem) key).getEquipmentSlot().getIndex()] : MAINHAND_MODIFIER;
            AttributeModifier modifier = new AttributeModifier(modifierId, MaterialMaster.NAME + " modifier", value, AttributeModifier.Operation.ADDITION);
            Map<String, AttributeModifier> map = origin.get(key);
            if (map != null) {

                map.put(attribute, modifier);
            } else {

                origin.put(key, new HashMap<String, AttributeModifier>() {{
                    this.put(attribute, modifier);
                }});
            }
        });
    }

}
