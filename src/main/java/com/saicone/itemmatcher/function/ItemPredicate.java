package com.saicone.itemmatcher.function;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a function that accepts an player with item and returns a boolean value.
 *
 * @param <P> object type that implements {@link Player} interface.
 * @param <I> object type that implements {@link ItemStack} interface.
 *
 * @author Rubenicos
 */
@FunctionalInterface
public interface ItemPredicate<P extends Player, I extends ItemStack> {

    boolean test(P player, I item);
}
