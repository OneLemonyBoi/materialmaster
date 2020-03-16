package com.fuzs.materialmaster.core.builder;

import com.fuzs.materialmaster.MaterialMaster;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Optional;

public class StringListParser<T extends IForgeRegistryEntry<T>> {

    private final IForgeRegistry<T> activeRegistry;
    private final T defaultEntry;
    
    protected StringListParser(IForgeRegistry<T> registry) {
        
        this.activeRegistry = registry;
        this.defaultEntry = registry.getValue(registry.getDefaultKey());
    }

    protected final void logStringParsingError(String entry, String message) {
        
        MaterialMaster.LOGGER.warn("Unable to parse entry \"" + entry + "\": " + message);
    }

    protected final Optional<ResourceLocation> parseResourceLocation(String source) {

        String[] s = source.split(":");
        Optional<ResourceLocation> location = Optional.empty();
        if (s.length == 1) {

            location = Optional.of(new ResourceLocation(s[0]));
        } else if (s.length == 2) {

            location = Optional.of(new ResourceLocation(s[0], s[1]));
        } else {

            this.logStringParsingError(source, "Insufficient number of arguments");
        }

        return location;
    }

    protected final Optional<T> getEntryFromRegistry(ResourceLocation location) {

        T entry = this.activeRegistry.getValue(location);
        if (entry != null && entry != this.defaultEntry) {

            return Optional.of(entry);
        } else {

            this.logStringParsingError(location.toString(), "Item not found");
        }

        return Optional.empty();
    }

    protected final Optional<T> getEntryFromRegistry(String source) {

        Optional<ResourceLocation> location = this.parseResourceLocation(source);
        return location.isPresent() ? this.getEntryFromRegistry(location.get()) : Optional.empty();
    }
    
}