package onelemonyboi.materialmaster.api.core.storage;

import onelemonyboi.materialmaster.api.builder.EntryCollectionBuilder;
import net.minecraftforge.common.ForgeConfigSpec;

import java.lang.reflect.Field;
import java.util.List;

public class MapProviderInfo extends AbstractProviderInfo {

    private final double min;
    private final double max;

    public MapProviderInfo(Field field, ForgeConfigSpec.ConfigValue<?> configValue, EntryCollectionBuilder<?> builder, double min, double max) {

        super(field, configValue, builder);
        this.min = min;
        this.max = max;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void sync() {

        try {

            // static field doesn't have an object associated with it
            this.field.set(null, this.builder.buildEntryMapWithCondition((List<String>) this.value.get(), (entry, value) -> value >= this.min && value <= this.max, "Out of bounds"));
        } catch (IllegalAccessException | ClassCastException ignored) {

        }
    }

}