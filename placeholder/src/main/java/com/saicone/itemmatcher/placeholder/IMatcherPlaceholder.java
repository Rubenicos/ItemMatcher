package com.saicone.itemmatcher.placeholder;

import com.saicone.itemmatcher.IMatcher;
import com.saicone.itemmatcher.IMatcherBuilder;
import com.saicone.itemmatcher.util.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class IMatcherPlaceholder extends PlaceholderExpansion implements Cacheable, Configurable {

    private static final Class<?> itemArray = ItemStack[].class;

    private final Map<String, IMatcher> cache = new HashMap<>();

    @Override
    public boolean register() {
        ItemNBTProvider.registerProvider(getString("NBTProvider", "auto").trim().toLowerCase());
        return super.register();
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public Map<String, Object> getDefaults() {
        return Map.of("NBTProvider", "auto");
    }

    @Override
    public @NotNull String getIdentifier() {
        return "itemmatcher";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Rubenicos";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String s) {
        if (player == null) {
            return "false";
        }
        final String[] params = PlaceholderAPI.setBracketPlaceholders(player, s).split("_", 2);
        if (params.length < 2) {
            return "false";
        }

        int amount = 1;
        final String[] split = params[0].split(":");
        if (split.length > 1) {
            amount = Utils.parseInt(split[1], 1);
            params[0] = split[0];
        }

        final Object item = getItem(player.getInventory(), params[0]);
        if (item == null) {
            return "false";
        }

        IMatcher matcher = getMatcher(params[1]);
        int count = 0;
        if (itemArray.isInstance(item)) {
            for (ItemStack it : ((ItemStack[]) item)) {
                if (it != null && matcher.match(player, it)) {
                    count = count + it.getAmount();
                    if (count >= amount) {
                        return "true";
                    }
                }
            }
        } else {
            if (matcher.match(player, (ItemStack) item)) {
                count = count + ((ItemStack) item).getAmount();
            }
        }
        return count >= amount ? "true" : "false";
    }

    @SuppressWarnings("deprecation")
    private Object getItem(PlayerInventory inventory, String type) {
        if (inventory == null) {
            return null;
        }
        switch (type.trim().toLowerCase()) {
            case "all":
                return inventory.getContents();
            case "armor":
                return inventory.getArmorContents();
            case "mainhand":
            case "hand":
                return inventory.getItemInHand();
            case "offhand":
                return inventory.getItemInOffHand();
            case "helmet":
                return inventory.getHelmet();
            case "chestplate":
                return inventory.getChestplate();
            case "leggings":
                return inventory.getLeggings();
            case "boots":
                return inventory.getBoots();
            case "extra":
                return inventory.getExtraContents();
            default:
                return null;
        }
    }

    private IMatcher getMatcher(String params) {
        return cache.getOrDefault(params, cache(params));
    }

    private IMatcher cache(String id) {
        IMatcherBuilder builder = new IMatcherBuilder();
        String[] params = Utils.safeSplit(id, ";");
        for (String param : params) {
            String[] split = Utils.safeSplit(param, "==");
            if (split.length > 1) {
                String[] key = Utils.safeSplit(split[0], ":");
                if (key.length < 1) continue;

                Object value = split[1];
                if (Utils.regexMatches("(?i)(nbt(tag)?|tag)-?(map|compound|list)?=.*", key[0])) {
                    String[] keySplit = key[0].split("=", 2);
                    if (keySplit.length == 2) {
                        String[] newSplit = new String[key.length + 1];
                        newSplit[0] = keySplit[0];
                        newSplit[1] = keySplit[1];
                        if (key.length - 2 >= 0) System.arraycopy(key, 2, newSplit, 2, key.length - 2);
                        key = newSplit;
                    }
                    String s = key[0].toLowerCase();
                    if (s.contains("map") || s.contains("compound")) {
                        Map<String, String> map = new HashMap<>();
                        for (String mapSplit : Utils.safeSplit((String) value, "|", (str) -> str.split("\\|"))) {
                            String[] entry = Utils.safeSplit(mapSplit, ":");
                            if (entry.length > 1) {
                                map.put(entry[0], entry[1]);
                            }
                        }
                        value = map;
                    } else if (s.contains("list")) {
                        value = new ArrayList<>(Arrays.asList(Utils.safeSplit((String) value, "|", (str) -> str.split("\\|"))));
                    }
                }

                String[] args = Utils.safeSplit(key[key.length - 1], "?", (s) -> s.split("[?]", 2));
                if (args.length > 1) {
                    key[key.length - 1] = args[0];
                    args = args[1].split(",");
                } else {
                    args = new String[0];
                }

                if (value instanceof Map) {
                    if (key.length > 1) {
                        builder.append(Utils.safeSplit(key[1], ".", (s) -> s.split("\\.")), (Map<?, ?>) value, key.length > 2 ? key[2] : "==", key.length > 3 ? key[3] : "==", args);
                    }
                } else if (value instanceof List) {
                    if (key.length > 1) {
                        builder.append(Utils.safeSplit(key[1], ".", (s) -> s.split("\\.")), (List<?>) value, key.length > 2 ? key[2] : "==", args);
                    }
                } else {
                    builder.appendConfig(key, String.valueOf(value), args);
                }
            } else {
                String[] rule = split[0].split("=", 2);
                if (rule.length > 1) {
                    builder.append(rule[0], Utils.safeSplit(rule[1].trim(), ".", (s) -> s.split("\\.")));
                } else {
                    builder.append(split[0]);
                }
            }
        }
        return builder.build();
    }
}
