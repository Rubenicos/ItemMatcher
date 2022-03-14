package com.saicone.itemmatcher.function;

import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Represents a function that accepts an player with item meta and returns a boolean value.
 *
 * @param <P> object type that implements {@link Player} interface.
 * @param <M> object type that implements {@link ItemMeta} interface.
 *
 * @author Rubenicos
 */
@FunctionalInterface
public interface MetaPredicate<P extends Player, M extends ItemMeta> {

    boolean test(P player, M meta);
}
