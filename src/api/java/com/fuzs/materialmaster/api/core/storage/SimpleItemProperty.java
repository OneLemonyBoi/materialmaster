package com.fuzs.materialmaster.api.core.storage;

import com.fuzs.materialmaster.api.provider.AbstractPropertyProvider;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class SimpleItemProperty extends ItemProperty<Double> {

    private final Predicate<Double> range;

    public SimpleItemProperty(String name, Function<AbstractPropertyProvider, Map<Item, Double>> provider) {

        this(name, provider, 0.0, Integer.MAX_VALUE);
    }

    public SimpleItemProperty(String name, Function<AbstractPropertyProvider, Map<Item, Double>> provider, Predicate<Item> condition, String message) {

        this(name, provider, 0.0, Integer.MAX_VALUE, condition, message);
    }

    public SimpleItemProperty(String name, Function<AbstractPropertyProvider, Map<Item, Double>> provider, double min, double max) {

        this(name, provider, min, max, flag -> true, "");
    }

    public SimpleItemProperty(String name, Function<AbstractPropertyProvider, Map<Item, Double>> provider, double min, double max, Predicate<Item> condition, String message) {

        super(name, provider, condition, message);
        this.range = value -> value >= min && value <= max;
    }

    @Override
    protected void add(Item item, Double value, String name) {

        this.checkOverwrite(item, name);
        if (this.inRange(item, value) && this.meetsCondition(item)) {

            this.properties.put(item, value);
        }
    }

    private boolean inRange(Item item, Double value) {

        return this.logFailure(item, this.range.test(value), "Out of bounds");
    }

}
