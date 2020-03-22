package com.fuzs.materialmaster.core;

import com.fuzs.materialmaster.MaterialMaster;
import com.fuzs.materialmaster.api.provider.AbstractPropertyProvider;
import com.fuzs.materialmaster.core.provider.ConfigPropertyProvider;
import com.fuzs.materialmaster.core.storage.AttributeItemProperty;
import com.fuzs.materialmaster.core.storage.ItemProperty;
import com.fuzs.materialmaster.core.storage.SimpleItemProperty;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraftforge.fml.config.ModConfig;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PropertySyncManager {

    private static final PropertySyncManager INSTANCE = new PropertySyncManager(new ConfigPropertyProvider());

    private final Map<AbstractPropertyProvider, String> providers = Maps.newHashMap();
    private final Map<PropertyType, ItemProperty<?>> properties = Maps.newHashMap();
    private final AbstractPropertyProvider defaultProperties;
    private final Set<UUID> knownAttributeIds = Sets.newHashSet();

    private PropertySyncManager(AbstractPropertyProvider defaultProperties) {

        this.defaultProperties = defaultProperties;
        // removed precondition tests since new values might be set to no longer match them causing confusing errors to be logged
        this.properties.put(PropertyType.ATTRIBUTES, new AttributeItemProperty("Attributes", AbstractPropertyProvider::getAttributes));
        this.properties.put(PropertyType.STACK_SIZE, new SimpleItemProperty("Stack Size", AbstractPropertyProvider::getStackSize, 0.0, 64.0));
        this.properties.put(PropertyType.DURABILTY, new SimpleItemProperty("Durability", AbstractPropertyProvider::getDurability));
        this.properties.put(PropertyType.DIG_SPEED, new SimpleItemProperty("Dig Speed", AbstractPropertyProvider::getDigSpeed));
        this.properties.put(PropertyType.HARVEST_LEVEL, new SimpleItemProperty("Harvest Level", AbstractPropertyProvider::getHarvestLevel, -1.0, Integer.MAX_VALUE, item -> !item.getToolTypes(null).isEmpty(), "No tool"));
        this.properties.put(PropertyType.ENCHANTABILITY, new SimpleItemProperty("Enchantability", AbstractPropertyProvider::getEnchantability));
    }

    public void registerPropertyProvider(String modid, AbstractPropertyProvider provider) {

        this.providers.put(provider, modid);
    }

    public ItemProperty<?> getProperty(PropertyType id) {

        return this.properties.get(id);
    }

    public boolean isKnownAttributeId(UUID attributeId) {

        return this.knownAttributeIds.contains(attributeId);
    }

    private Map<AbstractPropertyProvider, String> getProviders() {

        Map<AbstractPropertyProvider, String> providers = this.providers.entrySet().stream()
                .filter(entry -> entry.getKey().isEnabled())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        // use linked hash map so default properties are always applied last
                        (u, v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); }, LinkedHashMap::new));
        providers.put(this.defaultProperties, MaterialMaster.MODID);

        return providers;
    }

    @SuppressWarnings("RedundantCast")
    public void sync() {

        this.properties.values().forEach(ItemProperty::clear);
        Set<AbstractPropertyProvider> providers = this.getProviders().keySet();
        this.properties.values().forEach(property -> providers.forEach(property::addPropertiesFromProvider));
        this.syncKnownAttributeIds(providers);

        ((SimpleItemProperty) this.getProperty(PropertyType.STACK_SIZE)).forEach((item, value) -> item.maxStackSize = value.intValue());
        ((SimpleItemProperty) this.getProperty(PropertyType.DURABILTY)).forEach((item, value) -> item.maxDamage = value.intValue());
    }

    private void syncKnownAttributeIds(Set<AbstractPropertyProvider> providers) {

        this.knownAttributeIds.clear();
        providers.forEach(provider -> this.knownAttributeIds.addAll(provider.getAttributeIds()));
        // vanilla item attack damage modifier
        this.knownAttributeIds.add(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"));
        // vanilla item attack speed modifier
        this.knownAttributeIds.add(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"));
    }

    // config event handler
    public void onModConfig(final ModConfig.ModConfigEvent evt) {

        if (this.getProviders().containsValue(evt.getConfig().getModId())) {

            this.sync();
        }
    }

    public static PropertySyncManager getInstance() {

        return INSTANCE;
    }

    public enum PropertyType {

        ATTRIBUTES, STACK_SIZE, DURABILTY, DIG_SPEED, HARVEST_LEVEL, ENCHANTABILITY
    }

}
