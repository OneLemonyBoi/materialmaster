package com.fuzs.materialmaster.api.core.storage;

import com.fuzs.materialmaster.api.builder.EntryCollectionBuilder;
import net.minecraftforge.common.ForgeConfigSpec;

import java.lang.reflect.Field;

public abstract class AbstractProviderInfo {

    protected final Field field;
    protected final ForgeConfigSpec.ConfigValue<?> value;
    protected final EntryCollectionBuilder<?> builder;

    protected AbstractProviderInfo(Field field, ForgeConfigSpec.ConfigValue<?> configValue, EntryCollectionBuilder<?> builder) {

        this.field = field;
        this.value = configValue;
        this.builder = builder;
    }

    public abstract void sync();

}
