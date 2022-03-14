package com.saicone.itemmatcher.function;

import com.saicone.itemmatcher.NBTProvider;
import org.bukkit.entity.Player;

/**
 * Represents a function that accepts an player with item nbt and returns a boolean value.
 *
 * @param <P> object type that implements {@link Player} interface.
 * @param <T> object type that extends {@link NBTProvider} class.
 *
 * @author Rubenicos
 */
@FunctionalInterface
public interface NBTPredicate<P extends Player, T extends NBTProvider> {

    boolean test(P player, T nbt);
}
