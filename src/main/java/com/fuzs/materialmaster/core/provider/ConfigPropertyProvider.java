package com.fuzs.materialmaster.core.provider;

import com.fuzs.materialmaster.MaterialMaster;
import com.fuzs.materialmaster.common.RegisterAttributeHandler;
import com.fuzs.materialmaster.config.ConfigBuildHandler;
import com.fuzs.materialmaster.core.builder.EntryCollectionBuilder;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConfigPropertyProvider extends AbstractPropertyProvider {

    private final EntryCollectionBuilder<Item> entryBuilder = new EntryCollectionBuilder<>(ForgeRegistries.ITEMS);

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

        Map<Item, Multimap<String, AttributeModifier>> attributes = Maps.newHashMap();
        this.buildAttributeMap(attributes, ConfigBuildHandler.KNOCKBACK_RESISTANCE.get(), SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName());
        this.buildAttributeMap(attributes, ConfigBuildHandler.ATTACK_DAMAGE.get(), SharedMonsterAttributes.ATTACK_DAMAGE.getName());
        this.buildAttributeMap(attributes, ConfigBuildHandler.ATTACK_KNOCKBACK.get(), SharedMonsterAttributes.ATTACK_KNOCKBACK.getName());
        this.buildAttributeMap(attributes, ConfigBuildHandler.ATTACK_SPEED.get(), SharedMonsterAttributes.ATTACK_SPEED.getName());
        this.buildAttributeMap(attributes, ConfigBuildHandler.REACH_DISTANCE.get(), PlayerEntity.REACH_DISTANCE.getName());
        this.buildAttributeMap(attributes, ConfigBuildHandler.ATTACK_REACH.get(), RegisterAttributeHandler.ATTACK_REACH.getName());
        this.buildAttributeMap(attributes, ConfigBuildHandler.ARMOR.get(), SharedMonsterAttributes.ARMOR.getName());
        this.buildAttributeMap(attributes, ConfigBuildHandler.ARMOR_TOUGHNESS.get(), SharedMonsterAttributes.ARMOR_TOUGHNESS.getName());

        return attributes;
    }

    @Override
    public Map<Item, Double> getStackSize() {

        return this.entryBuilder.buildEntryMap(ConfigBuildHandler.STACK_SIZE.get());
    }

    @Override
    public Map<Item, Double> getDurability() {

        return this.entryBuilder.buildEntryMap(ConfigBuildHandler.DURABILITY.get());
    }

    @Override
    public Map<Item, Double> getDigSpeed() {

        return this.entryBuilder.buildEntryMap(ConfigBuildHandler.DIG_SPEED.get());
    }

    @Override
    public Map<Item, Double> getHarvestLevel() {

        return this.entryBuilder.buildEntryMap(ConfigBuildHandler.HARVEST_LEVEL.get());
    }

    @Override
    public Map<Item, Double> getEnchantability() {

        return this.entryBuilder.buildEntryMap(ConfigBuildHandler.ENCHANTABILITY.get());
    }

    @Override
    protected UUID getMainhandModifierId() {

        return UUID.fromString("2202169B-0BD6-4597-B623-DA8F7161F135");
    }

    @Override
    protected UUID[] getArmorModifierIds() {

        return new UUID[]{UUID.fromString("C29FA54E-C2BD-4137-897D-D7B363DCA34B"), UUID.fromString("45A03570-9501-4902-ADA9-7E2EF5A2C2D8"), UUID.fromString("CE18081D-3CDB-4288-A45F-99CE6C5530AB"), UUID.fromString("DBC90783-1621-453D-A4E4-739903307E4B")};
    }

    private Map<Item, Map.Entry<String, AttributeModifier>> buildAttributeMap(List<String> locations, String attribute) {

        return this.entryBuilder.buildEntryMap(locations).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {

            UUID id = entry.getKey() instanceof ArmorItem ? this.getArmorModifierIds()[((ArmorItem) entry.getKey()).getEquipmentSlot().getIndex()] : this.getMainhandModifierId();
            AttributeModifier modifier = new AttributeModifier(id, MaterialMaster.NAME + " modifier", entry.getValue(), AttributeModifier.Operation.ADDITION);

            return Maps.immutableEntry(attribute, modifier);
        }));
    }

    private void buildAttributeMap(Map<Item, Multimap<String, AttributeModifier>> origin, List<String> locations, String attribute) {

        this.getAttributeBuilder().putMultimapMap(origin, this.buildAttributeMap(locations, attribute));
    }

}
