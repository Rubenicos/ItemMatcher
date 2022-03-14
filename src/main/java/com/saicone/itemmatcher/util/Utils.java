package com.saicone.itemmatcher.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Utils {

    private static final boolean useRGB = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].split("_")[1]) >= 16;
    private static final Cache<String, Pattern> patterCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();

    public static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static String[] safeSplit(String s, String replace) {
        return safeSplit(s, replace, (str) -> str.split(replace));
    }

    public static String[] safeSplit(String s, String replace, Function<String, String[]> function) {
        String[] split = function.apply(s.replace("[" + replace + "]", "<?-rplc_?>"));
        String[] finalSplit = new String[split.length];
        for (int i = 0; i < split.length; i++) {
            finalSplit[i] = split[i].replace("<?-rplc_?>", replace);
        }
        return finalSplit;
    }

    public static boolean regexMatches(String pattern, String text) {
        return getPattern(pattern).matcher(text).matches();
    }

    public static Pattern getPattern(String pattern) {
        Pattern p = patterCache.getIfPresent(pattern);
        if (p == null) {
            patterCache.put(pattern, Pattern.compile(pattern));
            p = patterCache.getIfPresent(pattern);
        }
        return p;
    }

    public static String getArgument(String[] args, String key) {
        return getArgument(args, key, "");
    }

    public static String getArgument(String[] args, String key, String def) {
        for (String arg : args) {
            if (arg.contains("=")) {
                String[] split = arg.split("=", 2);
                if (split.length > 1) {
                    if (split[0].equalsIgnoreCase(key)) {
                        return split[1].trim();
                    }
                }
            }
        }
        return def;
    }

    public static Map<String, Object> getSectionAsMap(ConfigurationSection section) {
        Map<String, Object> map = new HashMap<>();
        section.getKeys(false).forEach((key) -> {
            Object value = map.get(key);
            if (value instanceof ConfigurationSection) {
                map.put(key, getSectionAsMap((ConfigurationSection) value));
            } else if (value instanceof Map) {
                map.put(key, new HashMap<>((Map<?, ?>) value));
            } else if (value instanceof List) {
                List<Object> list = new ArrayList<>();
                for (int i = 0; i < ((List<?>) value).size(); i++) {
                    Object iValue = ((List<?>) value).get(i);
                    if (iValue instanceof ConfigurationSection) {
                        list.add(getSectionAsMap((ConfigurationSection) iValue));
                    } else {
                        list.add(iValue);
                    }
                }
                map.put(key, list);
            } else {
                map.put(key, value);
            }
        });
        return map;
    }

    public static String getKeyMatches(ConfigurationSection section, String regex) {
        for (String key : section.getKeys(false)) {
            if (regexMatches(regex, key)) {
                return key;
            }
        }
        return null;
    }

    public static <K, V> K getKeyMatches(Map<K, V> map, String regex) {
        for (K key : map.keySet()) {
            String s = String.valueOf(key);
            if (regexMatches(regex, s)) {
                return key;
            }
        }
        return null;
    }

    public static Map<String, String> setPlaceholders(Player player, Map<String, String> map) {
        if (player == null) {
            return map;
        } else {
            Map<String, String> newMap = new HashMap<>();
            map.forEach((key, value) -> newMap.put(PlaceholderAPI.setPlaceholders(player, key), PlaceholderAPI.setPlaceholders(player, value)));
            return newMap;
        }
    }

    public static List<String> trim(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, list.get(i).trim());
        }
        return list;
    }

    public static List<String> color(List<String> list) {
        List<String> list1 = new ArrayList<>();
        list.forEach(s -> list1.add(color(s)));
        return list1;
    }

    public static String color(String s) {
        if (useRGB && s.contains("&#")) {
            StringBuilder builder = new StringBuilder();
            char[] chars = s.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (i + 7 < chars.length && chars[i] == '&' && chars[i + 1] == '#') {
                    StringBuilder color = new StringBuilder();
                    for (int c = i + 2; c < chars.length && c <= 7; c++) {
                        color.append(chars[c]);
                    }
                    if (color.length() == 6) {
                        builder.append(rgb(color.toString()));
                        i += color.length() + 2;
                    } else {
                        builder.append(chars[i]);
                    }
                } else {
                    builder.append(chars[i]);
                }
            }
            return ChatColor.translateAlternateColorCodes('&', builder.toString());
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private static String rgb(String color) {
        try {
            Integer.parseInt(color, 16);
        } catch (NumberFormatException ex) {
            return "<Invalid HEX>";
        }

        StringBuilder hex = new StringBuilder("§x");
        for (char c : color.toCharArray()) {
            hex.append("§").append(c);
        }

        return hex.toString();
    }
}
