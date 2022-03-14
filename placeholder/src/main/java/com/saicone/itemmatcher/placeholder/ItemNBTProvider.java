package com.saicone.itemmatcher.placeholder;

import com.saicone.itemmatcher.NBTProvider;
import com.saicone.rtag.RtagItem;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ItemNBTProvider {

    public static void registerProvider(String type) {
        switch (type.trim().toLowerCase().replace("-", "")) {
            case "rtag":
                NBTProvider.of = RtagProvider::new;
            case "itemnbt":
            case "itemnbtapi":
            case "nbtapi":
                NBTProvider.of = ItemNBTAPIProvider::new;
            case "nbteditor":
                NBTProvider.of = NBTEditorProvider::new;
            default:
                registerAuto();
        }
    }

    private static void registerAuto() {
        try {
            Class.forName("com.saicone.rtag.Rtag");
            registerProvider("rtag");
            return;
        } catch (ClassNotFoundException ignored) { }

        try {
            Class.forName("de.tr7zw.nbtapi.NBTItem");
            registerProvider("itemnbt");
            return;
        } catch (ClassNotFoundException ignored) { }

        registerProvider("nbteditor");
    }

    private static final class RtagProvider extends NBTProvider {

        private final RtagItem itemTag;

        public RtagProvider(ItemStack item) {
            super(item);
            itemTag = new RtagItem(item);
        }

        @Override
        public boolean containsNBT() {
            return itemTag.getTag() != null;
        }

        @Override
        public boolean containsNBT(String... path) {
            return itemTag.get((Object[]) path) != null;
        }

        @Override
        public Object getNBTObject(int type, String... path) {
            return itemTag.get((Object[]) path);
        }

        @Override
        public List<Object> getNBTList(int type, String... path) {
            return itemTag.get((Object[]) path);
        }

        @Override
        public Map<Object, Object> getNBTMap(int keyType, int valueType, String... path) {
            return itemTag.get((Object[]) path);
        }
    }

    private static final class ItemNBTAPIProvider extends NBTProvider {

        private final NBTItem itemNBT;

        public ItemNBTAPIProvider(ItemStack item) {
            super(item);
            itemNBT = new NBTItem(item);
        }

        @Override
        public boolean containsNBT() {
            return itemNBT.hasNBTData();
        }

        @Override
        public boolean containsNBT(String... path) {
            NBTCompound compound = getCompound(itemNBT, path);
            return compound != null && compound.hasKey(path[path.length - 1]);
        }

        @Override
        public Object getNBTObject(int type, String... path) {
            if (path.length < 1) {
                return null;
            }

            return getObject(getCompound(itemNBT, path), path[path.length - 1], type);
        }

        @Override
        public List<Object> getNBTList(int type, String... path) {
            if (path.length < 1) {
                return null;
            }

            return getList(getCompound(itemNBT, path), path[path.length - 1], type);
        }

        @Override
        public Map<Object, Object> getNBTMap(int keyType, int valueType, String... path) {
            if (path.length < 1) {
                return null;
            }

            NBTCompound compound = getCompound(itemNBT, path);
            if (compound == null) {
                return null;
            }

            Map<Object, Object> map = new HashMap<>();
            compound = compound.getCompound(path[path.length - 1]);
            if (compound != null) {
                for (String key : compound.getKeys()) {
                    Object value = getObject(compound, key, valueType);
                    if (value == null) {
                        return null;
                    }
                    map.put(key, value);
                }
                return map;
            }
            return null;
        }

        private NBTCompound getCompound(NBTCompound com, String... path) {
            if (path.length <= 1) {
                return com;
            }
            NBTCompound compound = com;
            for (int i = 0; i < path.length; i++) {
                if (compound == null) {
                    return null;
                }
                String key = path[i];
                if ((i + 1) == path.length) {
                    return compound;
                } else {
                    compound = compound.getCompound(key);
                }
            }
            return null;
        }

        private Object getObject(NBTCompound compound, String key, int type) {
            if (compound == null) {
                return null;
            }
            switch (type) {
                case 1:
                    return compound.getString(key);
                case 2:
                    return compound.getByte(key);
                case 3:
                    return compound.getShort(key);
                case 4:
                    return compound.getInteger(key);
                case 5:
                    return compound.getLong(key);
                case 6:
                    return compound.getFloat(key);
                case 7:
                    return compound.getDouble(key);
                default:
                    return null;
            }
        }

        private List<Object> getList(NBTCompound compound, String key, int type) {
            NBTList<?> nbtList = getListType(compound, key, type);
            if (nbtList == null) {
                return null;
            }
            return new ArrayList<>(nbtList);
        }

        private NBTList<?> getListType(NBTCompound compound, String key, int type) {
            if (compound == null) {
                return null;
            }
            switch (type) {
                case 1:
                    return compound.getStringList(key);
                case 4:
                    return compound.getIntegerList(key);
                case 5:
                    return compound.getLongList(key);
                case 6:
                    return compound.getFloatList(key);
                case 7:
                    return compound.getDoubleList(key);
                default:
                    return null;
            }
        }

        private Class<?> getType(int type) {
            switch (type) {
                case 1:
                    return String.class;
                case 2:
                    return Byte.class;
                case 3:
                    return Short.class;
                case 4:
                    return Integer.class;
                case 5:
                    return Long.class;
                case 6:
                    return Float.class;
                case 7:
                    return Double.class;
                default:
                    return null;
            }
        }
    }

    private static final class NBTEditorProvider extends NBTProvider {

        public NBTEditorProvider(ItemStack item) {
            super(item);
        }

        @Override
        public boolean containsNBT() {
            return NBTEditor.contains(getItem());
        }

        @Override
        public boolean containsNBT(String... path) {
            return NBTEditor.contains(getItem());
        }

        @Override
        public Object getNBTObject(int type, String... path) {
            switch (type) {
                case 1:
                    return NBTEditor.getString(getItem(), (Object[]) path);
                case 2:
                    return NBTEditor.getByte(getItem(), (Object[]) path);
                case 3:
                    return NBTEditor.getShort(getItem(), (Object[]) path);
                case 4:
                    return NBTEditor.getInt(getItem(), (Object[]) path);
                case 5:
                    return NBTEditor.getLong(getItem(), (Object[]) path);
                case 6:
                    return NBTEditor.getFloat(getItem(), (Object[]) path);
                case 7:
                    return NBTEditor.getDouble(getItem(), (Object[]) path);
                default:
                    return null;
            }
        }

        @Override
        public List<Object> getNBTList(int type, String... path) {
            return null;
        }

        @Override
        public Map<Object, Object> getNBTMap(int keyType, int valueType, String... path) {
            return null;
        }
    }
}
