package com.fuzs.materialmaster.core.builder;

import com.fuzs.materialmaster.MaterialMaster;
import com.fuzs.materialmaster.common.RegisterAttributeHandler;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("UnusedReturnValue")
public class AttributeMapBuilder {

    private final Map<Item, Multimap<String, AttributeModifier>> attributes = Maps.newHashMap();

    private final UUID mainhandModifierId;
    private final UUID[] armorModifierIds;

    private AttributeMapBuilder(UUID mainhand, UUID[] armor) {

        this.mainhandModifierId = mainhand;
        this.armorModifierIds = armor;
    }

    public static AttributeMapBuilder create(UUID mainhand, UUID[] armor) {

        return new AttributeMapBuilder(mainhand, armor);
    }
    
    public Map<Item, Multimap<String, AttributeModifier>> build() {
        
        return this.attributes;
    }

    private void putMultimapMap(Item item, String attribute, AttributeModifier modifier) {

        // build
        HashMultimap<String, AttributeModifier> multimap = HashMultimap.create();
        multimap.put(attribute, modifier);

        // combine
        this.attributes.merge(item, multimap, (map1, map2) -> {

            map1.putAll(map2);
            return map1;
        });
    }

    private void putMultimapMap(Item item, String attribute, Double value) {

        UUID id = item instanceof ArmorItem ? this.armorModifierIds[((ArmorItem) item).getEquipmentSlot().getIndex()] : this.mainhandModifierId;
        AttributeModifier modifier = new AttributeModifier(id, MaterialMaster.NAME + " modifier", value, AttributeModifier.Operation.ADDITION);
        this.putMultimapMap(item, attribute, modifier);
    }

    public AttributeMapBuilder putKnockbackResistance(Item item, Double value) {

        this.putMultimapMap(item, SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), value);
        return this;
    }

    public AttributeMapBuilder putAttackDamage(Item item, Double value) {

        this.putMultimapMap(item, SharedMonsterAttributes.ATTACK_DAMAGE.getName(), value);
        return this;
    }

    public AttributeMapBuilder putAttackKnockback(Item item, Double value) {

        this.putMultimapMap(item, SharedMonsterAttributes.ATTACK_KNOCKBACK.getName(), value);
        return this;
    }

    public AttributeMapBuilder putAttackSpeed(Item item, Double value) {

        this.putMultimapMap(item, SharedMonsterAttributes.ATTACK_SPEED.getName(), value);
        return this;
    }

    public AttributeMapBuilder putReachDistance(Item item, Double value) {

        this.putMultimapMap(item, PlayerEntity.REACH_DISTANCE.getName(), value);
        return this;
    }

    public AttributeMapBuilder putAttackReach(Item item, Double value) {

        this.putMultimapMap(item, RegisterAttributeHandler.ATTACK_REACH.getName(), value);
        return this;
    }

    public AttributeMapBuilder putArmor(Item item, Double value) {

        this.putMultimapMap(item, SharedMonsterAttributes.ARMOR.getName(), value);
        return this;
    }

    public AttributeMapBuilder putArmorToughness(Item item, Double value) {

        this.putMultimapMap(item, SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), value);
        return this;
    }

}
