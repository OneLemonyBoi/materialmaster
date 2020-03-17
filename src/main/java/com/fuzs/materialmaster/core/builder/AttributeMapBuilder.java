package com.fuzs.materialmaster.core.builder;

import com.fuzs.materialmaster.MaterialMaster;
import com.fuzs.materialmaster.common.RegisterAttributeHandler;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.UUID;

public class AttributeMapBuilder {

    private final UUID mainhandModifierId;
    private final UUID[] armorModifierIds;

    public AttributeMapBuilder(UUID mainhand, UUID[] armor) {

        this.mainhandModifierId = mainhand;
        this.armorModifierIds = armor;
    }

    public static AttributeMapBuilder create(UUID mainhand, UUID[] armor) {

        return new AttributeMapBuilder(mainhand, armor);
    }

    public void putMultimapMap(Map<Item, Multimap<String, AttributeModifier>> origin, Map<Item, Map.Entry<String, AttributeModifier>> merge) {

        merge.forEach((item, entry) -> this.putMultimapMap(origin, item, entry.getKey(), entry.getValue()));
    }

    private void putMultimapMap(Map<Item, Multimap<String, AttributeModifier>> origin, Item item, String attribute, AttributeModifier modifier) {

        HashMultimap<String, AttributeModifier> multimap = HashMultimap.create();
        multimap.put(attribute, modifier);
        origin.merge(item, multimap, (map1, map2) -> {

            map1.putAll(map2);
            return map1;
        });
    }

    public void putMultimapMap(Map<Item, Multimap<String, AttributeModifier>> origin, Item item, String attribute, Double value) {

        UUID id = item instanceof ArmorItem ? this.armorModifierIds[((ArmorItem) item).getEquipmentSlot().getIndex()] : this.mainhandModifierId;
        AttributeModifier modifier = new AttributeModifier(id, MaterialMaster.NAME + " modifier", value, AttributeModifier.Operation.ADDITION);
        this.putMultimapMap(origin, item, attribute, modifier);
    }

    public void putKnockbackResistance(Map<Item, Multimap<String, AttributeModifier>> origin, Item item, Double value) {

        this.putMultimapMap(origin, item, SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), value);
    }

    public void putAttackDamage(Map<Item, Multimap<String, AttributeModifier>> origin, Item item, Double value) {

        this.putMultimapMap(origin, item, SharedMonsterAttributes.ATTACK_DAMAGE.getName(), value);
    }

    public void putAttackKnockback(Map<Item, Multimap<String, AttributeModifier>> origin, Item item, Double value) {

        this.putMultimapMap(origin, item, SharedMonsterAttributes.ATTACK_KNOCKBACK.getName(), value);
    }

    public void putAttackSpeed(Map<Item, Multimap<String, AttributeModifier>> origin, Item item, Double value) {

        this.putMultimapMap(origin, item, SharedMonsterAttributes.ATTACK_SPEED.getName(), value);
    }

    public void putReachDistance(Map<Item, Multimap<String, AttributeModifier>> origin, Item item, Double value) {

        this.putMultimapMap(origin, item, PlayerEntity.REACH_DISTANCE.getName(), value);
    }

    public void putAttackReach(Map<Item, Multimap<String, AttributeModifier>> origin, Item item, Double value) {

        this.putMultimapMap(origin, item, RegisterAttributeHandler.ATTACK_REACH.getName(), value);
    }

    public void putArmor(Map<Item, Multimap<String, AttributeModifier>> origin, Item item, Double value) {

        this.putMultimapMap(origin, item, SharedMonsterAttributes.ARMOR.getName(), value);
    }

    public void putArmorToughness(Map<Item, Multimap<String, AttributeModifier>> origin, Item item, Double value) {

        this.putMultimapMap(origin, item, SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), value);
    }

}
