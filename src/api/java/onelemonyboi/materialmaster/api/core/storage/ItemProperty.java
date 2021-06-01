package onelemonyboi.materialmaster.api.core.storage;

import onelemonyboi.materialmaster.api.MaterialMasterReference;
import onelemonyboi.materialmaster.api.provider.AbstractPropertyProvider;
import com.google.common.collect.Maps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class ItemProperty<T> {

    protected final Map<Item, T> properties = Maps.newHashMap();
    private final String name;
    private final Function<AbstractPropertyProvider, Map<Item, T>> provider;
    private final Predicate<Item> condition;
    private final String message;

    protected ItemProperty(String name, Function<AbstractPropertyProvider, Map<Item, T>> provider) {

        this(name, provider, flag -> true, "");
    }

    protected ItemProperty(String name, Function<AbstractPropertyProvider, Map<Item, T>> provider, Predicate<Item> condition, String message) {

        this.name = name;
        this.provider = provider;
        this.condition = condition;
        this.message = message;
    }

    public final T getValue(Item item, T defaultValue) {

        return this.properties.getOrDefault(item, defaultValue);
    }

    public final void forEach(BiConsumer<Item, T> action) {

        this.properties.forEach(action);
    }

    public final void clear() {

        this.properties.clear();
    }

    public final void addPropertiesFromProvider(AbstractPropertyProvider provider) {

        this.provider.apply(provider).forEach((item, value) -> this.add(item, value, provider.getName()));
    }

    protected abstract void add(Item item, T value, String name);

    protected final boolean meetsCondition(Item item) {

        return this.logFailure(item, this.condition.test(item), this.message);
    }

    protected final void checkOverwrite(Item item, String provider) {

        if (this.properties.containsKey(item)) {

            MaterialMasterReference.LOGGER.warn("Property \"{}\" for item \"{}\" has been overwritten by {}", this.name, item.getDisplayName(ItemStack.EMPTY).getUnformattedComponentText(), provider);
        }
    }

    protected final boolean logFailure(Item item, boolean flag, String message) {

        if (!flag) {

            MaterialMasterReference.LOGGER.error("Unable to set property \"{}\" for item \"{}\": {}", this.name, item.getDisplayName(ItemStack.EMPTY).getUnformattedComponentText(), message);
        }

        return flag;
    }

}