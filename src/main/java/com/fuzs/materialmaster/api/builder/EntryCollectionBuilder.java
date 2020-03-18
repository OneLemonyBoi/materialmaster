package com.fuzs.materialmaster.api.builder;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class EntryCollectionBuilder<T extends IForgeRegistryEntry<T>> extends StringListParser<T> {

    public EntryCollectionBuilder(IForgeRegistry<T> registry) {

        super(registry);
    }

    public Set<T> buildEntrySet(List<String> locations) {

        return this.buildEntrySetWithCondition(locations, flag -> true, "");
    }

    public Map<T, Double> buildEntryMap(List<String> locations) {

        return this.buildEntryMapWithCondition(locations, (entry, value) -> true, "");
    }

    public Set<T> buildEntrySetWithCondition(List<String> locations, Predicate<T> condition, String message) {

        Set<T> set = Sets.newHashSet();
        for (String source : locations) {

            this.getEntryFromRegistry(source.trim()).forEach(entry -> {

                if (condition.test(entry)) {

                    if (this.checkOverwrite(set.contains(entry), source)) {

                        set.add(entry);
                    }
                } else {

                    this.logStringParsingError(source, message);
                }
            });
        }

        return set;
    }

    public Map<T, Double> buildEntryMapWithCondition(List<String> locations, BiPredicate<T, Double> condition, String message) {

        Map<T, Double> map = Maps.newHashMap();
        for (String source : locations) {

            String[] s = Arrays.stream(source.split(",")).map(String::trim).toArray(String[]::new);
            if (s.length == 2) {

                List<T> entries = this.getEntryFromRegistry(s[0]);
                if (entries.isEmpty()) {

                    continue;
                }

                Optional<Double> size = Optional.empty();
                try {

                    size = Optional.of(Double.parseDouble(s[1]));
                } catch (NumberFormatException ignored) {

                    this.logStringParsingError(source, "Invalid number format");
                }

                size.ifPresent(value -> entries.forEach(entry -> {

                    if (condition.test(entry, value)) {

                        if (this.checkOverwrite(map.containsKey(entry), entry.toString())) {

                            map.put(entry, value);
                        }
                    } else {

                        this.logStringParsingError(source, message);
                    }
                }));
            } else {

                this.logStringParsingError(source, "Insufficient number of arguments");
            }
        }

        return map;
    }

}
