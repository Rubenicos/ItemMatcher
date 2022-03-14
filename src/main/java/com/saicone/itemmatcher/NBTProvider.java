package com.saicone.itemmatcher;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class NBTProvider {

    public static Function<ItemStack, NBTProvider> of = (item) -> null;

    private final ItemStack item;

    public NBTProvider(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public abstract boolean containsNBT();

    public abstract boolean containsNBT(String... path);

    public abstract Object getNBTObject(int type, String... path);

    public abstract List<Object> getNBTList(int type, String... path);

    public abstract Map<Object, Object> getNBTMap(int keyType, int valueType, String... path);
}
