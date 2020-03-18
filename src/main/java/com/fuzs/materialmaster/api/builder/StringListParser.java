package com.fuzs.materialmaster.api.builder;

import com.fuzs.materialmaster.MaterialMaster;
import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StringListParser<T extends IForgeRegistryEntry<T>> {

    private final IForgeRegistry<T> activeRegistry;
    private final T defaultEntry;
    
    protected StringListParser(IForgeRegistry<T> registry) {
        
        this.activeRegistry = registry;
        this.defaultEntry = registry.getValue(registry.getDefaultKey());
    }

    protected final void logStringParsingError(String entry, String message) {
        
        MaterialMaster.LOGGER.error("Unable to parse entry \"" + entry + "\": " + message);
    }

    protected final boolean checkOverwrite(boolean flag, String entry) {

        if (flag) {

            this.logStringParsingError(entry, "Already present");
        }

        return !flag;
    }

    protected final List<T> getEntryFromRegistry(String source) {

        List<T> entries = Lists.newArrayList();
        Optional<ResourceLocation> location = Optional.ofNullable(ResourceLocation.tryCreate(source));
        if (location.isPresent()) {

            Optional<T> entry = this.getEntryFromRegistry(location.get());
            entry.ifPresent(entries::add);
        } else {

            this.getWildcardEntries(source, entries);
        }

        return entries;
    }

    private void getWildcardEntries(String source, List<T> entries) {

        String[] s = source.split(":");
        switch (s.length) {

            case 1:

                entries.addAll(this.getListFromRegistry("minecraft", s[0]));
                break;
            case 2:

                entries.addAll(this.getListFromRegistry(s[0], s[1]));
                break;
            default:

                this.logStringParsingError(source, "Invalid resource location format");
        }
    }

    private Optional<T> getEntryFromRegistry(ResourceLocation location) {

        T entry = this.activeRegistry.getValue(location);
        if (entry != null && (entry != this.defaultEntry || this.activeRegistry.containsValue(entry))) {

            return Optional.of(entry);
        } else {

            this.logStringParsingError(location.toString(), "Entry not found");
        }

        return Optional.empty();
    }

    private List<T> getListFromRegistry(String namespace, String path) {

        List<T> entries = this.activeRegistry.getEntries().stream()
                .filter(entry -> entry.getKey().getNamespace().equals(namespace))
                .filter(entry -> entry.getKey().getPath().matches(path.replace("*", "[a-z0-9/._-]*")))
                .map(Map.Entry::getValue).collect(Collectors.toList());

        if (entries.isEmpty()) {

            this.logStringParsingError(namespace + ':' + path, "Entry not found");
        }

        return entries;
    }
    
}