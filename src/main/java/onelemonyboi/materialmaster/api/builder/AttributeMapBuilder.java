package onelemonyboi.materialmaster.api.builder;

import onelemonyboi.materialmaster.api.MaterialMasterReference;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.UUID;

/**
 * builder class mainly for easily defining an attribute map
 */
@SuppressWarnings("UnusedReturnValue")
public class AttributeMapBuilder {

    private final Map<Item, Multimap<String, AttributeModifier>> attributes = Maps.newHashMap();

    private final UUID mainhandModifierId;
    private final UUID[] armorModifierIds;

    /**
     * use {@link #create} instead
     */
    private AttributeMapBuilder(UUID mainhand, UUID[] armor) {

        this.mainhandModifierId = mainhand;
        this.armorModifierIds = armor;
    }

    /**
     * @param mainhand modifier id to be used for mainhand
     * @param armor modifier id to be used for armor slots
     * @return new attribute builder for given uuids
     */
    public static AttributeMapBuilder create(UUID mainhand, UUID[] armor) {

        return new AttributeMapBuilder(mainhand, armor);
    }

    /**
     * @return attribute map from this builder
     */
    public Map<Item, Multimap<String, AttributeModifier>> build() {
        
        return this.attributes;
    }

    private void putMultimapMap(Item item, IAttribute attribute, AttributeModifier modifier) {

        // build
        HashMultimap<String, AttributeModifier> multimap = HashMultimap.create();
        multimap.put(attribute.getName(), modifier);

        // combine
        this.attributes.merge(item, multimap, (map1, map2) -> {

            map1.putAll(map2);
            return map1;
        });
    }

    private void putMultimapMap(Item item, IAttribute attribute, Double value) {

        UUID id = item instanceof ArmorItem ? this.armorModifierIds[((ArmorItem) item).getEquipmentSlot().getIndex()] : this.mainhandModifierId;
        AttributeModifier modifier = new AttributeModifier(id, MaterialMasterReference.NAME + " modifier", value, AttributeModifier.Operation.ADDITION);
        this.putMultimapMap(item, attribute, modifier);
    }

    public AttributeMapBuilder putMaxHealth(Item item, Double value) {

        this.putMultimapMap(item, SharedMonsterAttributes.MAX_HEALTH, value);
        return this;
    }

    public AttributeMapBuilder putKnockbackResistance(Item item, Double value) {

        this.putMultimapMap(item, SharedMonsterAttributes.KNOCKBACK_RESISTANCE, value);
        return this;
    }

    public AttributeMapBuilder putMovementSpeed(Item item, Double value) {

        this.putMultimapMap(item, SharedMonsterAttributes.MOVEMENT_SPEED, value);
        return this;
    }

    public AttributeMapBuilder putAttackDamage(Item item, Double value) {

        this.putMultimapMap(item, SharedMonsterAttributes.ATTACK_DAMAGE, value);
        return this;
    }

    public AttributeMapBuilder putAttackKnockback(Item item, Double value) {

        this.putMultimapMap(item, SharedMonsterAttributes.ATTACK_KNOCKBACK, value);
        return this;
    }

    public AttributeMapBuilder putAttackSpeed(Item item, Double value) {

        this.putMultimapMap(item, SharedMonsterAttributes.ATTACK_SPEED, value);
        return this;
    }

    public AttributeMapBuilder putArmor(Item item, Double value) {

        this.putMultimapMap(item, SharedMonsterAttributes.ARMOR, value);
        return this;
    }

    public AttributeMapBuilder putArmorToughness(Item item, Double value) {

        this.putMultimapMap(item, SharedMonsterAttributes.ARMOR_TOUGHNESS, value);
        return this;
    }

    public AttributeMapBuilder putLuck(Item item, Double value) {

        this.putMultimapMap(item, SharedMonsterAttributes.LUCK, value);
        return this;
    }

    public AttributeMapBuilder putSwimSpeed(Item item, Double value) {

        this.putMultimapMap(item, LivingEntity.SWIM_SPEED, value);
        return this;
    }

    public AttributeMapBuilder putGravity(Item item, Double value) {

        this.putMultimapMap(item, LivingEntity.ENTITY_GRAVITY, value);
        return this;
    }

    public AttributeMapBuilder putReachDistance(Item item, Double value) {

        this.putMultimapMap(item, PlayerEntity.REACH_DISTANCE, value);
        return this;
    }

    public AttributeMapBuilder putAttackReach(Item item, Double value) {

        this.putMultimapMap(item, MaterialMasterReference.ATTACK_REACH, value);
        return this;
    }

}
