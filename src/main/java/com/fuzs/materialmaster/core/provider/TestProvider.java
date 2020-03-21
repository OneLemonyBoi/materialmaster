package com.fuzs.materialmaster.core.provider;

import com.fuzs.materialmaster.api.SyncProvider;
import net.minecraft.item.Item;

import java.util.Map;

@SyncProvider
public class TestProvider {

    @SyncProvider.EntryMap(path = {"attribute_item_stats", "Attack Damage List"}, type = SyncProvider.RegistryType.ITEMS)
    public static Map<Item, Double> itemList;

}
