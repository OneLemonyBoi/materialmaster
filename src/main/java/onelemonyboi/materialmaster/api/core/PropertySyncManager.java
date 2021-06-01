package onelemonyboi.materialmaster.api.core;

import onelemonyboi.materialmaster.api.core.storage.AttributeItemProperty;
import onelemonyboi.materialmaster.api.core.storage.ItemProperty;
import onelemonyboi.materialmaster.api.core.storage.SimpleItemProperty;
import onelemonyboi.materialmaster.api.provider.AbstractPropertyProvider;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraftforge.fml.config.ModConfig;

import java.util.*;
import java.util.stream.Collectors;

public class PropertySyncManager {

    private static final PropertySyncManager INSTANCE = new PropertySyncManager();

    private final Map<AbstractPropertyProvider, String> providers = Maps.newHashMap();
    private final Map<PropertyType, ItemProperty<?>> properties = Maps.newHashMap();
    private final Set<UUID> knownAttributeIds = Sets.newHashSet();

    private PropertySyncManager() {

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

        return this.providers.entrySet().stream()
                .filter(entry -> entry.getKey().isEnabled())
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getPriority()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        // use linked hash map so default properties are always applied last
                        (u, v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); }, LinkedHashMap::new));
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
    public void onModConfig(final ModConfig.Reloading evt) {

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
