package com.saicone.itemmatcher;

import com.saicone.itemmatcher.function.ItemPredicate;
import com.saicone.itemmatcher.function.MetaPredicate;
import com.saicone.itemmatcher.function.NBTPredicate;
import com.saicone.itemmatcher.util.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.*;
import java.util.stream.Collectors;

public class IMatcherBuilder {

    private final List<ItemPredicate<Player, ItemStack>> itemPredicate = new ArrayList<>();
    private final List<MetaPredicate<Player, ItemMeta>> metaPredicate = new ArrayList<>();
    private final List<NBTPredicate<Player, NBTProvider>> nbtPredicate = new ArrayList<>();

    public boolean addItemPredicate(ItemPredicate<Player, ItemStack> predicate) {
        if (predicate != null) {
            return itemPredicate.add(predicate);
        }
        return false;
    }

    public boolean addMetaPredicate(MetaPredicate<Player, ItemMeta> predicate) {
        if (predicate != null) {
            return metaPredicate.add(predicate);
        }
        return false;
    }

    public boolean addNBTPredicate(NBTPredicate<Player, NBTProvider> predicate) {
        if (predicate != null) {
            return nbtPredicate.add(predicate);
        }
        return false;
    }

    public MetaPredicate<Player, ItemMeta> getMetaPredicate(String matcher) {
        switch (matcher.trim().toLowerCase()) {
            case "hascustommodeldata":
            case "hascustommodel":
            case "hasmodeldata":
            case "hasmodel":
                return CustomModelData.getPredicate(true);
            case "nothascustommodeldata":
            case "nothascustommodel":
            case "nothasmodeldata":
            case "nothasmodel":
                return CustomModelData.getPredicate(false);
            case "hasdisplayname":
            case "hascustomname":
            case "hasname":
                return DisplayName.getPredicate(true);
            case "nothasdisplayname":
            case "nothascustomname":
            case "nothasname":
                return DisplayName.getPredicate(false);
            case "hasenchantments":
            case "hasenchants":
            case "isenchanted":
            case "enchanted":
                return Enchantments.getPredicate(true);
            case "nothasenchantments":
            case "nothasenchants":
            case "isnotenchanted":
            case "notenchanted":
                return Enchantments.getPredicate(false);
            case "hasflags":
                return Flags.getPredicate(true);
            case "nothasflags":
                return Flags.getPredicate(false);
            case "hasdisplaylore":
            case "hascustomlore":
            case "haslore":
                return Lore.getPredicate(true);
            case "nothasdisplaylore":
            case "nothascustomlore":
            case "nothaslore":
                return Lore.getPredicate(false);
            case "isupgraded":
                return Potion.getPredicate(true, true);
            case "isnotupgraded":
                return Potion.getPredicate(false, true);
            case "isextended":
                return Potion.getPredicate(true, false);
            case "isnotextended":
                return Potion.getPredicate(false, false);
            default:
                return null;
        }
    }

    public NBTPredicate<Player, NBTProvider> getNBTPredicate(String matcher, String... path) {
        switch (matcher.trim().toLowerCase()) {
            case "hasnbt":
            case "hastag":
            case "containsnbt":
            case "containstag":
                return NbtTag.getPredicate(true, path);
            case "nothasnbt":
            case "nothastag":
            case "notcontainsnbt":
            case "notcontainstag":
                return NbtTag.getPredicate(true, path);
            default:
                return null;
        }
    }

    public ItemPredicate<Player, ItemStack> getItemPredicate(String matcher, String value, Comparator comparator, String... args) {
        boolean papi = parsePlaceholders(args);
        switch (matcher.trim().toLowerCase()) {
            case "amount":
            case "amt":
                return Amount.getPredicate(value, comparator, papi);
            case "damage":
            case "data":
                return Damage.getPredicate(value, comparator, papi);
            case "material":
            case "mat":
            case "type":
            case "id":
                return Material.getPredicate(value, comparator, papi);
            default:
                return null;
        }
    }

    public MetaPredicate<Player, ItemMeta> getMetaPredicate(String matcher, String value, Comparator comparator, String... args) {
        boolean papi = parsePlaceholders(args);
        switch (matcher.trim().toLowerCase()) {
            case "custommodeldata":
            case "custommodel":
            case "modeldata":
            case "model":
                return CustomModelData.getPredicate(value, comparator, papi);
            case "displayname":
            case "customname":
            case "name":
                return DisplayName.getPredicate(value, comparator, papi);
            case "potiontype":
            case "potion":
            case "potioneffect":
            case "effect":
                return Potion.getPredicate(value, comparator, papi);
            default:
                return null;
        }
    }

    public NBTPredicate<Player, NBTProvider> getNBTPredicate(String[] path, String value, Comparator comparator, String... args) {
        boolean papi = parsePlaceholders(args);
        int type = NbtTag.getTypeID(Utils.getArgument(args, "Type", "string"));
        return NbtTag.getPredicate(path, value, comparator, papi, type);
    }

    public <T> MetaPredicate<Player, ItemMeta> getMetaPredicate(String matcher, List<T> value, Comparator comparator, String... args) {
        boolean papi = parsePlaceholders(args);
        int type = comparableType(args);
        List<String> list = new ArrayList<>();
        value.forEach(s -> list.add(String.valueOf(s)));
        switch (matcher.trim().toLowerCase()) {
            case "itemflags":
            case "flags":
                return Flags.getPredicate(Utils.trim(list), comparator, papi, type);
            case "displaylore":
            case "customlore":
            case "lore":
                return Lore.getPredicate(list, comparator, papi, type);
            default:
                return null;
        }
    }

    public <T> NBTPredicate<Player, NBTProvider> getNBTPredicate(String[] path, List<T> value, Comparator comparator, String... args) {
        boolean papi = parsePlaceholders(args);
        int type = comparableType(args);
        int listType = NbtTag.getTypeID(Utils.getArgument(args, "Type", "string"));
        List<String> list = new ArrayList<>();
        value.forEach(s -> list.add(String.valueOf(s)));
        return NbtTag.getPredicate(path, list, comparator, papi, type, listType);
    }

    public <K, V> MetaPredicate<Player, ItemMeta> getMetaPredicate(String matcher, Map<K, V> value, BiComparator comparator, String... args) {
        boolean papi = parsePlaceholders(args);
        int type = comparableType(args);
        Map<String, Object> map = new HashMap<>();

        switch (matcher.trim().toLowerCase()) {
            case "enchantments":
            case "enchantment":
            case "enchants":
            case "enchant":
                if (papi) {
                    value.forEach((key, val) -> map.put(String.valueOf(key), String.valueOf(val)));
                } else {
                    value.forEach((key, val) -> map.put(String.valueOf(key), Utils.parseInt(String.valueOf(val), 0)));
                }
                return Enchantments.getPredicate(map, comparator, papi, type);
            default:
                return null;
        }
    }

    public <K, V> NBTPredicate<Player, NBTProvider> getNBTPredicate(String[] path, Map<K, V> value, BiComparator comparator, String... args) {
        boolean papi = parsePlaceholders(args);
        int type = comparableType(args);
        int keyType = NbtTag.getTypeID(Utils.getArgument(args, "KeyType", "string"));
        int valueType = NbtTag.getTypeID(Utils.getArgument(args, "ValueType", "string"));
        Map<String, String> map = new HashMap<>();
        value.forEach((key, val) -> map.put(String.valueOf(key), String.valueOf(val)));
        return NbtTag.getPredicate(path, map, comparator, papi, type, keyType, valueType);
    }

    public IMatcherBuilder append(String matcher, String... path) {
        addMetaPredicate(getMetaPredicate(matcher));
        addNBTPredicate(getNBTPredicate(matcher, path));
        return this;
    }

    public IMatcherBuilder append(String matcher, int value, String comparator, String... args) {
        return append(matcher, String.valueOf(value), comparator, args);
    }

    public IMatcherBuilder append(String matcher, String value, String comparator, String... args) {
        addItemPredicate(getItemPredicate(matcher, value, Comparator.of(comparator, Comparator.Equal.INSTANCE), args));
        addMetaPredicate(getMetaPredicate(matcher, value, Comparator.of(comparator, Comparator.Equal.INSTANCE), args));
        return this;
    }

    public <T> IMatcherBuilder append(String matcher, List<T> value, String comparator, String... args) {
        addMetaPredicate(getMetaPredicate(matcher, value, Comparator.of(comparator, Comparator.Equal.INSTANCE), args));
        return this;
    }

    public <K, V> IMatcherBuilder append(String matcher, Map<K, V> value, String comparator, String... args) {
        String[] split = comparator.split(":", 2);
        return append(matcher, value, split.length > 0 ? split[0] : comparator, split.length > 1 ? split[1] : comparator, args);
    }

    public <K, V> IMatcherBuilder append(String matcher, Map<K, V> value, String keyComparator, String valueComparator, String... args) {
        addMetaPredicate(getMetaPredicate(matcher, value, BiComparator.of(keyComparator, valueComparator, Comparator.Equal.INSTANCE, Comparator.Equal.INSTANCE), args));
        return this;
    }

    public IMatcherBuilder append(String[] path, String value, String comparator, String... args) {
        addNBTPredicate(getNBTPredicate(path, value, Comparator.of(comparator, Comparator.Equal.INSTANCE), args));
        return this;
    }

    public <T> IMatcherBuilder append(String[] path, List<T> value, String comparator, String... args) {
        addNBTPredicate(getNBTPredicate(path, value, Comparator.of(comparator, Comparator.Equal.INSTANCE), args));
        return this;
    }

    public <K, V> IMatcherBuilder append(String[] path, Map<K, V> value, String comparator, String... args) {
        String[] split = comparator.split(":", 2);
        return append(path, value, split.length > 0 ? split[0] : comparator, split.length > 1 ? split[1] : comparator, args);
    }

    public <K, V> IMatcherBuilder append(String[] path, Map<K, V> value, String keyComparator, String valueComparator, String... args) {
        addNBTPredicate(getNBTPredicate(path, value, BiComparator.of(keyComparator, valueComparator, Comparator.Equal.INSTANCE, Comparator.Equal.INSTANCE), args));
        return this;
    }

    public IMatcherBuilder appendConfig(ConfigurationSection section) {
        String rulesKey = Utils.getKeyMatches(section, "(?i)(argument|arg|rule)s?");
        if (rulesKey != null) {
            List<String> rules = section.getStringList(rulesKey);
            for (String rule : rules) {
                String[] split = rule.split("=", 2);
                if (split.length > 1) {
                    append(split[0], Utils.safeSplit(split[1].trim(), ".", (s) -> s.split("\\.")));
                } else {
                    append(rule);
                }
            }
        }
        
        for (String key : section.getKeys(false)) {
            if (key.equals(rulesKey)) {
                continue;
            }
            String[] split = Utils.safeSplit(key, ":");
            if (split.length < 1) continue;

            if (Utils.regexMatches("(?i)(nbt(tag)?|tag)-?(map|compound|list)?=.*", split[0])) {
                String[] keySplit = split[0].split("=", 2);
                if (keySplit.length == 2) {
                    String[] newSplit = new String[split.length + 1];
                    newSplit[0] = keySplit[0];
                    newSplit[1] = keySplit[1];
                    if (split.length - 2 >= 0) System.arraycopy(split, 2, newSplit, 2, split.length - 2);
                    split = newSplit;
                }
            }

            String[] args = Utils.safeSplit(split[split.length - 1], "?", (s) -> s.split("[?]", 2));
            if (args.length > 1) {
                split[split.length - 1] = args[0];
                args = args[1].split(",");
            } else {
                args = new String[0];
            }

            Object value = section.get(key);
            if (value instanceof ConfigurationSection) {
                appendConfig(split, (ConfigurationSection) value, args);
            } else if (value instanceof List) {
                appendConfig(split, (List<?>) value, args);
            } else {
                appendConfig(split, String.valueOf(value), args);
            }
        }
        return this;
    }

    public IMatcherBuilder appendConfig(String[] key, ConfigurationSection section, String... args) {
        if (Utils.regexMatches("(?i)(nbt(tag)?|tag)-?(map|compound)", key[0])) {
            if (key.length > 1) {
                return append(Utils.safeSplit(key[1], ".", (s) -> s.split("\\.")), Utils.getSectionAsMap(section), key.length > 2 ? key[2] : "==", key.length > 3 ? key[3] : "==", args);
            } else {
                return this;
            }
        } else {
            List<String> rules = new ArrayList<>(Arrays.asList(args));

            String rulesKey = Utils.getKeyMatches(section, "(?i)(argument|arg|rule)s?");
            if (rulesKey != null) {
                Object ruleList = section.get(rulesKey);
                if (ruleList instanceof List) {
                    ((List<?>) ruleList).forEach(rule -> rules.add(String.valueOf(rule)));
                } else if (ruleList instanceof String) {
                    rules.add((String) ruleList);
                }
            }

            String valueKey = Utils.getKeyMatches(section, "(?i)(value)s?");
            if (valueKey == null || Utils.regexMatches("(?i)enchant(ment)?s?", key[0])) {
                Map<String, Object> value = new HashMap<>();
                section.getKeys(false).forEach((key1) -> {
                    if (!key1.equals(rulesKey)) {
                        value.put(key1, section.get(key1));
                    }
                });
                return append(key[0], value, key.length > 1 ? key[1] : "==", key.length > 2 ? key[2] : "==", rules.toArray(new String[0]));
            } else {
                Object value = section.get(valueKey);
                if (value instanceof List) {
                    return appendConfig(key, (List<?>) value, args);
                } else {
                    return appendConfig(key, String.valueOf(value), args);
                }
            }
        }
    }

    public <T> IMatcherBuilder appendConfig(String[] key, List<T> list, String... args) {
        if (Utils.regexMatches("(?i)(nbt(tag)?|tag)-?list", key[0])) {
            if (key.length > 1) {
                return append(Utils.safeSplit(key[1], ".", (s) -> s.split("\\.")), list, key.length > 2 ? key[2] : "==", args);
            } else {
                return this;
            }
        } else if (Utils.regexMatches("(?i)enchant(ment)?s?", key[0])) {
            return append(key[0], Enchantments.listToMap(list), key.length > 1 ? key[1] : "==", key.length > 2 ? key[2] : "==", args);
        } else {
            return append(key[0], list, key.length > 1 ? key[1] : "==", args);
        }
    }

    public IMatcherBuilder appendConfig(String[] key, String value, String... args) {
        if (Utils.regexMatches("(?i)nbt(tag)?|tag", key[0])) {
            if (key.length > 1) {
                return append(Utils.safeSplit(key[1], ".", (s) -> s.split("\\.")), value, key.length > 2 ? key[2] : "==", args);
            } else {
                return this;
            }
        } else if (Utils.regexMatches("(?i)enchant(ment)?s?", key[0])) {
            return append(key[0], Enchantments.stringToMap(value), key.length > 1 ? key[1] : "==", key.length > 2 ? key[2] : "==", args);
        } else if (Utils.regexMatches("(?i)(flags?|(custom|display)?lore)", key[0])) {
            List<String> list = new ArrayList<>();
            Collections.addAll(list, key[0].toLowerCase().startsWith("flag") ? value.split(";") : Utils.safeSplit(value, "\\n"));
            return append(key[0], list, key.length > 1 ? key[1] : "==", args);
        } else {
            return append(key[0], value, key.length > 1 ? key[1] : "==", args);
        }
    }

    public IMatcher build() {
        return new IMatcher(itemPredicate.isEmpty() ? null : itemPredicate, metaPredicate.isEmpty() ? null : metaPredicate, nbtPredicate.isEmpty() ? null : nbtPredicate);
    }

    private int comparableType(String[] args) {
        switch (Utils.getArgument(args, "Compare").toLowerCase()) {
            case "all":
            case "total":
                return 2;
            case "inverseall":
            case "iall":
            case "inversetotal":
            case "itotal":
                return 3;
            case "exact":
            case "same":
            case "==":
                return 4;
            default:
                return 1;
        }
    }

    private boolean parsePlaceholders(String[] args) {
        switch (Utils.getArgument(args, "PlaceholderAPI").toLowerCase()) {
            case "true":
            case "yes":
            case "y":
                return true;
            default:
                return false;
        }
    }

    public static final class Amount {

        public static ItemPredicate<Player, ItemStack> getPredicate(String value, Comparator comparator, boolean papi) {
            int amount = Utils.parseInt(value, 0);
            return (player, item) -> comparator.matchNumber(item.getAmount(),
                    papi ? Utils.parseInt(PlaceholderAPI.setPlaceholders(player, value), 0) : amount);
        }
    }

    public static final class CustomModelData {

        public static MetaPredicate<Player, ItemMeta> getPredicate(boolean contains) {
            if (contains) {
                return (__, meta) -> meta.hasCustomModelData();
            } else {
                return (__, meta) -> !meta.hasCustomModelData();
            }
        }

        public static MetaPredicate<Player, ItemMeta> getPredicate(String value, Comparator comparator, boolean papi) {
            if (value == null || value.equalsIgnoreCase("null")) {
                return getPredicate(false);
            }
            int model = Utils.parseInt(value, -1);
            return (player, meta) -> meta.hasCustomModelData() && comparator.matchNumber(meta.getCustomModelData(),
                    papi ? Utils.parseInt(PlaceholderAPI.setPlaceholders(player, value), -1) : model);
        }
    }

    public static final class Damage {

        @SuppressWarnings("deprecation")
        public static ItemPredicate<Player, ItemStack> getPredicate(String value, Comparator comparator, boolean papi) {
            short damage = (short) Utils.parseInt(value, 0);
            return (player, item) -> comparator.matchNumber(item.getDurability(),
                    papi ? (short) Utils.parseInt(PlaceholderAPI.setPlaceholders(player, value), 0) : damage);
        }
    }

    public static final class DisplayName {

        public static MetaPredicate<Player, ItemMeta> getPredicate(boolean contains) {
            if (contains) {
                return (__, meta) -> meta.hasDisplayName();
            } else {
                return (__, meta) -> !meta.hasDisplayName();
            }
        }

        public static MetaPredicate<Player, ItemMeta> getPredicate(String value, Comparator comparator, boolean papi) {
            if (value == null) {
                return getPredicate(false);
            }
            String name = Utils.color(value);
            return (player, meta) -> meta.hasDisplayName() && comparator.matchString(meta.getDisplayName(),
                    papi ? Utils.color(PlaceholderAPI.setPlaceholders(player, value)) : name);
        }
    }

    public static final class Enchantments {

        public static MetaPredicate<Player, ItemMeta> getPredicate(boolean contains) {
            if (contains) {
                return (__, meta) -> meta.hasEnchants();
            } else {
                return (__, meta) -> !meta.hasEnchants();
            }
        }

        public static MetaPredicate<Player, ItemMeta> getPredicate(Map<String, Object> value, BiComparator comparator, boolean papi, int type) {
            if (value == null || value.isEmpty()) {
                return getPredicate(false);
            }
            if (value.size() == 1) {
                String enchant = value.keySet().toArray(new String[0])[0];
                String level = String.valueOf(value.get(enchant));
                int levelInt = Utils.parseInt(level, 1);
                switch (type) {
                    case 1:
                    case 2:
                        return (player, meta) -> meta.hasEnchants() && comparator.matchEntry(keyToString(meta.getEnchants()),
                                papi ? PlaceholderAPI.setPlaceholders(player, enchant) : enchant,
                                papi ? Utils.parseInt(PlaceholderAPI.setPlaceholders(player, level), 0) : levelInt);
                    case 3:
                        return (player, meta) -> meta.hasEnchants() && comparator.matchEntryAll(keyToString(meta.getEnchants()),
                                papi ? PlaceholderAPI.setPlaceholders(player, enchant) : enchant,
                                papi ? Utils.parseInt(PlaceholderAPI.setPlaceholders(player, level), 0) : levelInt);
                    case 4:
                        return (player, meta) -> meta.hasEnchants() && comparator.matchEntryExact(keyToString(meta.getEnchants()),
                                papi ? PlaceholderAPI.setPlaceholders(player, enchant) : enchant,
                                papi ? Utils.parseInt(PlaceholderAPI.setPlaceholders(player, level), 0) : levelInt);
                    default:
                        return (__, ___) -> false;
                }
            }
            switch (type) {
                case 1:
                    return (player, meta) -> meta.hasEnchants() && comparator.matchMap(keyToString(meta.getEnchants()),
                            papi ? valueToInt(Utils.setPlaceholders(player, cast(value))) : cast(value));
                case 2:
                    return (player, meta) -> meta.hasEnchants() && comparator.matchMapAll(keyToString(meta.getEnchants()),
                            papi ? valueToInt(Utils.setPlaceholders(player, cast(value))) : cast(value));
                case 3:
                    return (player, meta) -> meta.hasEnchants() && comparator.matchMapInverseAll(keyToString(meta.getEnchants()),
                            papi ? valueToInt(Utils.setPlaceholders(player, cast(value))) : cast(value));
                case 4:
                    return (player, meta) -> meta.hasEnchants() && comparator.matchMapExact(keyToString(meta.getEnchants()),
                            papi ? valueToInt(Utils.setPlaceholders(player, cast(value))) : cast(value));
                default:
                    return (__, ___) -> false;
            }
        }

        @SuppressWarnings("deprecation")
        public static Map<String, Integer> keyToString(Map<Enchantment, Integer> enchants) {
            Map<String, Integer> map = new HashMap<>();
            enchants.forEach((enchant, level) -> map.put(enchant.getName(), level));
            return map;
        }

        public static Map<String, Integer> valueToInt(Map<String, String> enchants) {
            Map<String, Integer> map = new HashMap<>();
            enchants.forEach((enchant, level) -> map.put(enchant, Utils.parseInt(level, 0)));
            return map;
        }

        public static <T> Map<String, String> listToMap(List<T> list) {
            Map<String, String> map = new HashMap<>();
            for (T t : list) {
                String[] split = String.valueOf(t).split("=", 2);
                if (split.length < 1) continue;

                map.put(split[0].trim(), split.length > 1 ? split[1].trim() : "1");
            }
            return map;
        }

        public static Map<String, String> stringToMap(String value) {
            Map<String, String> map = new HashMap<>();
            for (String s : value.split(";")) {
                String[] split = s.split("=", 2);
                if (split.length < 1) continue;
                map.put(split[0].trim(), split.length > 1 ? split[1].trim() : "1");
            }
            return map;
        }

        @SuppressWarnings("unchecked")
        private static <A, B> Map<String, B> cast(Map<String, A> map) {
            return (Map<String, B>) map;
        }
    }

    public static final class Flags {

        public static MetaPredicate<Player, ItemMeta> getPredicate(boolean contains) {
            if (contains) {
                return (__, meta) -> !meta.getItemFlags().isEmpty();
            } else {
                return (__, meta) -> meta.getItemFlags().isEmpty();
            }
        }

        public static MetaPredicate<Player, ItemMeta> getPredicate(List<String> value, Comparator comparator, boolean papi, int type) {
            if (value == null || value.isEmpty()) {
                return getPredicate(false);
            }
            if (value.size() == 1) {
                String flag = value.get(0);
                switch (type) {
                    case 1:
                    case 2:
                        return (player, meta) -> !meta.getItemFlags().isEmpty() && comparator.matchValue(toString(meta.getItemFlags()),
                                papi ? PlaceholderAPI.setPlaceholders(player, flag) : flag);
                    case 3:
                        return (player, meta) -> !meta.getItemFlags().isEmpty() && comparator.matchValueAll(toString(meta.getItemFlags()),
                                papi ? PlaceholderAPI.setPlaceholders(player, flag) : flag);
                    case 4:
                        return (player, meta) -> !meta.getItemFlags().isEmpty() && comparator.matchValueExact(toString(meta.getItemFlags()),
                                papi ? PlaceholderAPI.setPlaceholders(player, flag) : flag);
                    default:
                        return (__, ___) -> false;
                }
            }
            switch (type) {
                case 1:
                    return (player, meta) -> !meta.getItemFlags().isEmpty() && comparator.matchList(toString(meta.getItemFlags()),
                            papi ? PlaceholderAPI.setPlaceholders(player, value) : value);
                case 2:
                    return (player, meta) -> !meta.getItemFlags().isEmpty() && comparator.matchListAll(toString(meta.getItemFlags()),
                            papi ? PlaceholderAPI.setPlaceholders(player, value) : value);
                case 3:
                    return (player, meta) -> !meta.getItemFlags().isEmpty() && comparator.matchListInverseAll(toString(meta.getItemFlags()),
                            papi ? PlaceholderAPI.setPlaceholders(player, value) : value);
                case 4:
                    return (player, meta) -> !meta.getItemFlags().isEmpty() && comparator.matchListExact(toString(meta.getItemFlags()),
                            papi ? PlaceholderAPI.setPlaceholders(player, value) : value);
                default:
                    return (__, ___) -> false;
            }
        }

        public static List<String> toString(Set<ItemFlag> flags) {
            return toString(flags.stream().collect(Collectors.toUnmodifiableList()));
        }

        public static List<String> toString(List<ItemFlag> flags) {
            List<String> list = new ArrayList<>();
            flags.forEach(flag -> list.add(flag.name()));
            return list;
        }
    }

    public static final class Lore {

        public static MetaPredicate<Player, ItemMeta> getPredicate(boolean contains) {
            if (contains) {
                return (__, meta) -> meta.hasLore();
            } else {
                return (__, meta) -> !meta.hasLore();
            }
        }

        @SuppressWarnings("all")
        public static MetaPredicate<Player, ItemMeta> getPredicate(List<String> value, Comparator comparator, boolean papi, int type) {
            if (value == null || value.isEmpty()) {
                return getPredicate(false);
            }
            if (value.size() == 1) {
                String line = papi ? value.get(0) : Utils.color(value.get(0));
                switch (type) {
                    case 1:
                    case 2:
                        return (player, meta) -> !meta.getItemFlags().isEmpty() && comparator.matchValue(meta.getLore(),
                                papi ? Utils.color(PlaceholderAPI.setPlaceholders(player, line)) : line);
                    case 3:
                        return (player, meta) -> !meta.getItemFlags().isEmpty() && comparator.matchValueAll(meta.getLore(),
                                papi ? Utils.color(PlaceholderAPI.setPlaceholders(player, line)) : line);
                    case 4:
                        return (player, meta) -> !meta.getItemFlags().isEmpty() && comparator.matchValueExact(meta.getLore(),
                                papi ? Utils.color(PlaceholderAPI.setPlaceholders(player, line)) : line);
                    default:
                        return (__, ___) -> false;
                }
            }
            List<String> lore = papi ? null : Utils.color(value);
            switch (type) {
                case 1:
                    return (player, meta) -> meta.hasLore() && comparator.matchList(meta.getLore(),
                            papi ? Utils.color(PlaceholderAPI.setPlaceholders(player, value)) : lore);
                case 2:
                    return (player, meta) -> meta.hasLore() && comparator.matchListAll(meta.getLore(),
                            papi ? Utils.color(PlaceholderAPI.setPlaceholders(player, value)) : lore);
                case 3:
                    return (player, meta) -> meta.hasLore() && comparator.matchListInverseAll(meta.getLore(),
                            papi ? Utils.color(PlaceholderAPI.setPlaceholders(player, value)) : lore);
                case 4:
                    return (player, meta) -> meta.hasLore() && comparator.matchListExact(meta.getLore(),
                            papi ? Utils.color(PlaceholderAPI.setPlaceholders(player, value)) : lore);
                default:
                    return (__, ___) -> false;
            }
        }
    }

    public static final class Material {

        public static ItemPredicate<Player, ItemStack> getPredicate(String value, Comparator comparator, boolean papi) {
            String material = value.toUpperCase();
            return (player, item) -> comparator.matchString(item.getType().name(),
                    papi ? PlaceholderAPI.setPlaceholders(player, value).toUpperCase() : material);
        }
    }

    public static final class NbtTag {

        public static NBTPredicate<Player, NBTProvider> getPredicate(boolean contains, String... path) {
            if (path.length < 1) {
                if (contains) {
                    return (__, nbt) -> nbt.containsNBT();
                } else {
                    return (__, nbt) -> !nbt.containsNBT();
                }
            } else {
                if (contains) {
                    return (__, nbt) -> nbt.containsNBT(path);
                } else {
                    return (__, nbt) -> !nbt.containsNBT(path);
                }
            }
        }

        public static NBTPredicate<Player, NBTProvider> getPredicate(String[] path, String value, Comparator comparator, boolean papi, int type) {
            Object object = getHasType(value, type);
            return (player, nbt) -> comparator.match(nbt.getNBTObject(type, path),
                        papi ? getHasType(PlaceholderAPI.setPlaceholders(player, value), type) : object);
        }

        public static NBTPredicate<Player, NBTProvider> getPredicate(String[] path, List<String> value, Comparator comparator, boolean papi, int type, int listType) {
            if (value == null || value.isEmpty()) {
                return getPredicate(false);
            }
            if (value.size() == 1) {
                String val = value.get(0);
                Object object = papi ? null : getHasType(val, listType);
                switch (type) {
                    case 1:
                    case 2:
                        return (player, nbt) -> {
                            List<Object> nbtList = nbt.getNBTList(listType, path);
                            return nbtList != null && comparator.matchValue(nbtList,
                                    papi ? getHasType(PlaceholderAPI.setPlaceholders(player, val), listType) : object);
                        };
                    case 3:
                        return (player, nbt) -> {
                            List<Object> nbtList = nbt.getNBTList(listType, path);
                            return nbtList != null && comparator.matchValueAll(nbtList,
                                    papi ? getHasType(PlaceholderAPI.setPlaceholders(player, val), listType) : object);
                        };
                    case 4:
                        return (player, nbt) -> {
                            List<Object> nbtList = nbt.getNBTList(listType, path);
                            return nbtList != null && comparator.matchValueExact(nbtList,
                                    papi ? getHasType(PlaceholderAPI.setPlaceholders(player, val), listType) : object);
                        };
                    default:
                        return (__, ___) -> false;
                }
            }

            List<Object> list = papi ? null : getHasType(value, listType);
            switch (type) {
                case 1:
                    return (player, nbt) -> {
                        List<Object> nbtList = nbt.getNBTList(listType, path);
                        if (nbtList != null) {
                            List<Object> valueList = papi ? getHasType(PlaceholderAPI.setPlaceholders(player, value), listType) : list;
                            return valueList != null && comparator.matchList(nbtList, valueList);
                        }
                        return false;
                    };
                case 2:
                    return (player, nbt) -> {
                        List<Object> nbtList = nbt.getNBTList(listType, path);
                        if (nbtList != null) {
                            List<Object> valueList = papi ? getHasType(PlaceholderAPI.setPlaceholders(player, value), listType) : list;
                            return valueList != null && comparator.matchListAll(nbtList, valueList);
                        }
                        return false;
                    };
                case 3:
                    return (player, nbt) -> {
                        List<Object> nbtList = nbt.getNBTList(listType, path);
                        if (nbtList != null) {
                            List<Object> valueList = papi ? getHasType(PlaceholderAPI.setPlaceholders(player, value), listType) : list;
                            return valueList != null && comparator.matchListInverseAll(nbtList, valueList);
                        }
                        return false;
                    };
                case 4:
                    return (player, nbt) -> {
                        List<Object> nbtList = nbt.getNBTList(listType, path);
                        if (nbtList != null) {
                            List<Object> valueList = papi ? getHasType(PlaceholderAPI.setPlaceholders(player, value), listType) : list;
                            return valueList != null && comparator.matchListExact(nbtList, valueList);
                        }
                        return false;
                    };
                default:
                    return (__, ___) -> false;
            }
        }

        public static NBTPredicate<Player, NBTProvider> getPredicate(String[] path, Map<String, String> value, BiComparator comparator, boolean papi, int type, int keyType, int valueType) {
            if (value == null || value.isEmpty()) {
                return getPredicate(false);
            }

            if (value.size() == 1) {
                Map.Entry<String, String> mapEntry = null;
                for (Map.Entry<String, String> entry : value.entrySet()) {
                    mapEntry = entry;
                    break;
                }
                if (mapEntry == null) return (__, ___) -> false;

                String mapKey = mapEntry.getKey();
                String mapValue = mapEntry.getValue();
                Object key = getHasType(mapKey, keyType);
                Object val = getHasType(mapValue, valueType);
                switch (type) {
                    case 1:
                    case 2:
                        return (player, nbt) -> {
                            Map<Object, Object> nbtMap = nbt.getNBTMap(keyType, valueType, path);
                            return nbtMap != null && comparator.matchEntry(nbtMap,
                                    papi ? getHasType(PlaceholderAPI.setPlaceholders(player, mapKey), keyType) : key,
                                    papi ? getHasType(PlaceholderAPI.setPlaceholders(player, mapValue), valueType) : val);

                        };
                    case 3:
                        return (player, nbt) -> {
                            Map<Object, Object> nbtMap = nbt.getNBTMap(keyType, valueType, path);
                            return nbtMap != null && comparator.matchEntryAll(nbtMap,
                                    papi ? getHasType(PlaceholderAPI.setPlaceholders(player, mapKey), keyType) : key,
                                    papi ? getHasType(PlaceholderAPI.setPlaceholders(player, mapValue), valueType) : val);

                        };
                    case 4:
                        return (player, nbt) -> {
                            Map<Object, Object> nbtMap = nbt.getNBTMap(keyType, valueType, path);
                            return nbtMap != null && comparator.matchEntryExact(nbtMap,
                                    papi ? getHasType(PlaceholderAPI.setPlaceholders(player, mapKey), keyType) : key,
                                    papi ? getHasType(PlaceholderAPI.setPlaceholders(player, mapValue), valueType) : val);

                        };
                    default:
                        return (__, ___) -> false;
                }
            }

            Map<Object, Object> map = papi ? null : getHasType(value, keyType, valueType);
            switch (type) {
                case 1:
                    return (player, nbt) -> {
                        Map<Object, Object> nbtMap = nbt.getNBTMap(keyType, valueType, path);
                        if (nbtMap != null) {
                            Map<Object, Object> valueMap = papi ? getHasType(Utils.setPlaceholders(player, value), keyType, valueType) : map;
                            return valueMap != null && comparator.matchMap(nbtMap, valueMap);
                        }
                        return false;
                    };
                case 2:
                    return (player, nbt) -> {
                        Map<Object, Object> nbtMap = nbt.getNBTMap(keyType, valueType, path);
                        if (nbtMap != null) {
                            Map<Object, Object> valueMap = papi ? getHasType(Utils.setPlaceholders(player, value), keyType, valueType) : map;
                            return valueMap != null && comparator.matchMapAll(nbtMap, valueMap);
                        }
                        return false;
                    };
                case 3:
                    return (player, nbt) -> {
                        Map<Object, Object> nbtMap = nbt.getNBTMap(keyType, valueType, path);
                        if (nbtMap != null) {
                            Map<Object, Object> valueMap = papi ? getHasType(Utils.setPlaceholders(player, value), keyType, valueType) : map;
                            return valueMap != null && comparator.matchMapInverseAll(nbtMap, valueMap);
                        }
                        return false;
                    };
                case 4:
                    return (player, nbt) -> {
                        Map<Object, Object> nbtMap = nbt.getNBTMap(keyType, valueType, path);
                        if (nbtMap != null) {
                            Map<Object, Object> valueMap = papi ? getHasType(Utils.setPlaceholders(player, value), keyType, valueType) : map;
                            return valueMap != null && comparator.matchMapExact(nbtMap, valueMap);
                        }
                        return false;
                    };
                default:
                    return (__, ___) -> false;
            }
        }

        public static int getTypeID(String s) {
            switch (s.toLowerCase()) {
                case "string":
                case "str":
                case "text":
                    return 1;
                case "byte":
                    return 2;
                case "short":
                    return 3;
                case "integer":
                case "int":
                case "number":
                    return 4;
                case "long":
                    return 5;
                case "float":
                    return 6;
                case "double":
                    return 7;
                default:
                    return 0;
            }
        }

        public static Object getHasType(String s, int type) {
            try {
                return parseType(s, type);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        public static List<Object> getHasType(List<String> list, int type) {
            List<Object> finalList = new ArrayList<>();
            for (String s : list) {
                Object object = getHasType(s, type);
                if (object == null) {
                    return null;
                }
                finalList.add(object);
            }
            return finalList;
        }

        public static Map<Object, Object> getHasType(Map<String, String> map, int keyType, int valueType) {
            Map<Object, Object> finalMap = new HashMap<>();
            map.forEach((key, value) -> {
                Object keyObject = getHasType(key, keyType);
                if (keyObject != null) {
                    finalMap.put(keyObject, getHasType(value, valueType));
                }
            });
            return map.size() == finalMap.size() ? finalMap : null;
        }

        private static Object parseType(String s, int type) throws NumberFormatException {
            switch (type) {
                case 1:
                    return s;
                case 2:
                    return Byte.parseByte(s);
                case 3:
                    return Short.parseShort(s);
                case 4:
                    return Integer.parseInt(s);
                case 5:
                    return Long.parseLong(s);
                case 6:
                    return Float.parseFloat(s);
                case 7:
                    return Double.parseDouble(s);
                default:
                    return null;
            }
        }
    }

    public static final class Potion {

        public static MetaPredicate<Player, ItemMeta> getPredicate(boolean is, boolean upgraded) {
            if (upgraded) {
                if (is) {
                    return (__, meta) -> meta instanceof PotionMeta && ((PotionMeta) meta).getBasePotionData().isUpgraded();
                } else {
                    return (__, meta) -> !(meta instanceof PotionMeta) || !((PotionMeta) meta).getBasePotionData().isUpgraded();
                }
            } else {
                if (is) {
                    return (__, meta) -> meta instanceof PotionMeta && ((PotionMeta) meta).getBasePotionData().isExtended();
                } else {
                    return (__, meta) -> !(meta instanceof PotionMeta) || !((PotionMeta) meta).getBasePotionData().isExtended();
                }
            }
        }

        public static MetaPredicate<Player, ItemMeta> getPredicate(String value, Comparator comparator, boolean papi) {
            String type = value.toUpperCase();
            return (player, meta) -> meta instanceof PotionMeta && comparator.matchString(((PotionMeta) meta).getBasePotionData().getType().name(),
                    papi ? PlaceholderAPI.setPlaceholders(player, value).toUpperCase() : type);
        }
    }
}
