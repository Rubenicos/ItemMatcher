package com.saicone.itemmatcher;

import com.saicone.itemmatcher.function.ItemPredicate;
import com.saicone.itemmatcher.function.MetaPredicate;
import com.saicone.itemmatcher.function.NBTPredicate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class IMatcher {

    private final List<ItemPredicate<Player, ItemStack>> itemPredicate;
    private final List<MetaPredicate<Player, ItemMeta>> metaPredicate;
    private final List<NBTPredicate<Player, NBTProvider>> nbtPredicate;

    public IMatcher(List<ItemPredicate<Player, ItemStack>> itemPredicate, List<MetaPredicate<Player, ItemMeta>> metaPredicate, List<NBTPredicate<Player, NBTProvider>> nbtPredicate) {
        this.itemPredicate = itemPredicate;
        this.metaPredicate = metaPredicate;
        this.nbtPredicate = nbtPredicate;
    }

    public boolean match(ItemStack item) {
        return match(null, item);
    }

    public boolean match(Player player, ItemStack item) {
        return matchItem(player, item) && matchMeta(player, item.getItemMeta()) && matchNBT(player, item);
    }

    public boolean matchItem(Player player, ItemStack item) {
        if (item == null) {
            return false;
        } else if (itemPredicate != null) {
            for (ItemPredicate<Player, ItemStack> predicate : itemPredicate) {
                if (!predicate.test(player, item)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean matchMeta(Player player, ItemMeta meta) {
        if (metaPredicate != null && !metaPredicate.isEmpty()) {
            if (meta == null) {
                return false;
            }
            for (MetaPredicate<Player, ItemMeta> predicate : metaPredicate) {
                if (!predicate.test(player, meta)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean matchNBT(Player player, ItemStack item) {
        if (nbtPredicate != null && !nbtPredicate.isEmpty()) {
            return matchNBT(player, NBTProvider.of.apply(item));
        }
        return true;
    }

    public boolean matchNBT(Player player, NBTProvider nbt) {
        if (nbt == null) {
            return false;
        }
        for (NBTPredicate<Player, NBTProvider> predicate : nbtPredicate) {
            if (!predicate.test(player, nbt)) {
                return false;
            }
        }
        return true;
    }
}
