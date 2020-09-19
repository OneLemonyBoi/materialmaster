package com.fuzs.materialmaster.api.core.storage;

import com.fuzs.materialmaster.api.builder.EntryCollectionBuilder;
import net.minecraftforge.common.ForgeConfigSpec;

import java.lang.reflect.Field;
import java.util.List;

public class SetProviderInfo extends AbstractProviderInfo {

    public SetProviderInfo(Field field, ForgeConfigSpec.ConfigValue<?> configValue, EntryCollectionBuilder<?> builder) {

        super(field, configValue, builder);
    }

    @SuppressWarnings("unchecked")
    public void sync() {

        try {

            // static field doesn't have an object associated with it
            this.field.set(null, this.builder.buildEntrySet((List<String>) this.value.get()));
        } catch (IllegalAccessException | ClassCastException ignored) {

        }
    }

}