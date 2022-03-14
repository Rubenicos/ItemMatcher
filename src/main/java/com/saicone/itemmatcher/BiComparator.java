package com.saicone.itemmatcher;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BiComparator {

    public static BiComparator of(String keyName, String valueName) {
        return of(keyName, valueName, Comparator.DEFAULT, Comparator.DEFAULT);
    }

    public static BiComparator of(String keyName, String valueName, Comparator keyDefault, Comparator valueDefault) {
        return new BiComparator(Comparator.of(keyName, keyDefault), Comparator.of(valueName, valueDefault));
    }

    private final Comparator keyComparator;
    private final Comparator valueComparator;

    public BiComparator(Comparator keyComparator, Comparator valueComparator) {
        this.keyComparator = keyComparator;
        this.valueComparator = valueComparator;
    }

    public Comparator getKeyComparator() {
        return keyComparator;
    }

    public Comparator getValueComparator() {
        return valueComparator;
    }

    public <K, V> boolean matchEntry(Map<K, V> map1, K key, V value) {
        for (Map.Entry<K, V> entry : map1.entrySet()) {
            if (keyComparator.match(entry.getKey(), key) && valueComparator.match(entry.getValue(), value)) {
                return true;
            }
        }
        return false;
    }

    public <K, V> boolean matchEntryAll(Map<K, V> map1, K key, V value) {
        for (Map.Entry<K, V> entry : map1.entrySet()) {
            if (!keyComparator.match(entry.getKey(), key) || !valueComparator.match(entry.getValue(), value)) {
                return false;
            }
        }
        return true;
    }

    public <K, V> boolean matchEntryExact(Map<K, V> map1, K key, V value) {
        if (map1.size() == 1) {
            for (Map.Entry<K, V> entry : map1.entrySet()) {
                if (keyComparator.match(entry.getKey(), key) && valueComparator.match(entry.getValue(), value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public <K, V> boolean matchMap(Map<K, V> map1, Map<K, V> map2) {
        for (Map.Entry<K, V> entry1 : map1.entrySet()) {
            for (Map.Entry<K, V> entry2 : map2.entrySet()) {
                if (keyComparator.match(entry1.getKey(), entry2.getKey()) && valueComparator.match(entry1.getValue(), entry2.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public <K, V> boolean matchMapAll(Map<K, V> map1, Map<K, V> map2) {
        for (Map.Entry<K, V> entry2 : map2.entrySet()) {
            boolean match = false;
            for (Map.Entry<K, V> entry1 : map1.entrySet()) {
                if ((match = keyComparator.match(entry1.getKey(), entry2.getKey()) && valueComparator.match(entry1.getValue(), entry2.getValue()))) {
                    break;
                }
            }
            if (!match) {
                return false;
            }
        }
        return true;
    }

    public <K, V> boolean matchMapInverseAll(Map<K, V> map1, Map<K, V> map2) {
        for (Map.Entry<K, V> entry1 : map1.entrySet()) {
            boolean match = false;
            for (Map.Entry<K, V> entry2 : map2.entrySet()) {
                if ((match = keyComparator.match(entry1.getKey(), entry2.getKey()) && valueComparator.match(entry1.getValue(), entry2.getValue()))) {
                    break;
                }
            }
            if (!match) {
                return false;
            }
        }
        return true;
    }

    public <K, V> boolean matchMapExact(Map<K, V> map1, Map<K, V> map2) {
        if (map1.size() == map2.size()) {
            List<Map.Entry<K, V>> list1 = map1.entrySet().stream().collect(Collectors.toUnmodifiableList());
            List<Map.Entry<K, V>> list2 = map2.entrySet().stream().collect(Collectors.toUnmodifiableList());
            for (int i = 0; i < list1.size(); i++) {
                Map.Entry<K, V> entry1 = list1.get(i);
                Map.Entry<K, V> entry2 = list2.get(i);
                if (!keyComparator.match(entry1.getKey(), entry2.getKey()) || !valueComparator.match(entry1.getValue(), entry2.getValue())) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
