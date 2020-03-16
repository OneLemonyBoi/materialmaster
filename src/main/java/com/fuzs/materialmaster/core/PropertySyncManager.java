package com.fuzs.materialmaster.core;

import com.fuzs.materialmaster.config.ConfigBuildHandler;
import com.fuzs.materialmaster.core.property.AttributeItemProperty;
import com.fuzs.materialmaster.core.property.ItemProperty;
import com.fuzs.materialmaster.core.property.SimpleItemProperty;
import com.fuzs.materialmaster.core.provider.AbstractPropertyProvider;
import com.fuzs.materialmaster.core.provider.ConfigPropertyProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.item.Item;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PropertySyncManager {

    private static final PropertySyncManager INSTANCE = new PropertySyncManager();

    private final List<AbstractPropertyProvider> providers = Lists.newArrayList();
    private final Map<String, ItemProperty<?>> properties = Maps.newHashMap();
    private final ConfigPropertyProvider configProperties = new ConfigPropertyProvider();
    private final Set<UUID> knownAttributeIds = Sets.newHashSet();

    private PropertySyncManager() {

        this.properties.put("attributes", new AttributeItemProperty("Attributes", AbstractPropertyProvider::getAttributes));
        this.properties.put("stack_size", new SimpleItemProperty("Stack Size", AbstractPropertyProvider::getStackSize, 0.0, 64.0));
        this.properties.put("durability", new SimpleItemProperty("Durability", AbstractPropertyProvider::getDurability, Item::isDamageable, "Not damageable"));
        this.properties.put("dig_speed", new SimpleItemProperty("Dig Speed", AbstractPropertyProvider::getDigSpeed));
        this.properties.put("harvest_level", new SimpleItemProperty("Harvest Level", AbstractPropertyProvider::getHarvestLevel, item -> !item.getToolTypes(null).isEmpty(), "No tool"));
        this.properties.put("enchantability", new SimpleItemProperty("Enchantability", AbstractPropertyProvider::getEnchantability, item -> item.getItemEnchantability() > 0, "Not enchantable"));
    }

    public void registerProvider(AbstractPropertyProvider provider) {

        this.providers.add(provider);
    }

    @Nonnull
    public ItemProperty<?> getProperty(String id) {

        return this.properties.get(id);
    }

    public boolean isKnownAttributeId(UUID attributeId) {

        return this.knownAttributeIds.contains(attributeId);
    }

    private List<AbstractPropertyProvider> getProviders() {

        List<AbstractPropertyProvider> providers = this.providers.stream().filter(AbstractPropertyProvider::isEnabled).collect(Collectors.toList());
        providers.add(this.configProperties);

        return providers;
    }

    @SuppressWarnings("RedundantCast")
    private void sync() {

        this.properties.values().forEach(ItemProperty::clear);
        List<AbstractPropertyProvider> providers = this.getProviders();
        this.properties.values().forEach(property -> providers.forEach(property::addPropertiesFromProvider));
        this.syncKnownAttributeIds(providers);

        ((SimpleItemProperty) this.getProperty("stack_size")).forEach((item, value) -> item.maxStackSize = value.intValue());
        ((SimpleItemProperty) this.getProperty("durability")).forEach((item, value) -> item.maxDamage = value.intValue());
    }

    private void syncKnownAttributeIds(List<AbstractPropertyProvider> providers) {

        this.knownAttributeIds.clear();
        providers.forEach(provider -> this.knownAttributeIds.addAll(provider.getAttributeIds()));
        // vanilla item attack damage modifier
        this.knownAttributeIds.add(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"));
        // vanilla item attack speed modifier
        this.knownAttributeIds.add(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"));
    }

    // config reload event handler
    public void onModConfig(final ModConfig.ModConfigEvent evt) {

        if (evt.getConfig().getSpec() == ConfigBuildHandler.SPEC) {

            this.sync();
        }
    }

    public static PropertySyncManager getInstance() {

        return INSTANCE;
    }

}
