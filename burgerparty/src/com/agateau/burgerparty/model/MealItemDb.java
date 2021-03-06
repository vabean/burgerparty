package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.MealItem.Type;
import com.agateau.burgerparty.utils.FileUtils;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.XmlReader;

public class MealItemDb {
    private static class MealItemMap extends OrderedMap<String, MealItem> {
    }

    private MealItemMap mGenericMap = new MealItemMap();
    private OrderedMap<Integer, MealItemMap> mWorldMaps = new OrderedMap<Integer, MealItemMap>();

    private static MealItemDb sInstance = null;

    public BurgerItem getBurgerItem(String name) {
        BurgerItem item = (BurgerItem)get(name);
        assert(item != null);
        return item;
    }

    public BurgerItem getBurgerItem(int worldIndex, String name) {
        BurgerItem item = (BurgerItem)get(worldIndex, name);
        assert(item != null);
        return item;
    }

    public MealItem get(int worldIndex, String name) {
        MealItemMap map = mWorldMaps.get(worldIndex);
        if (map == null) {
            return mGenericMap.get(name);
        }
        MealItem item = map.get(name);
        if (item == null) {
            return mGenericMap.get(name);
        }
        return item;
    }

    public MealItem get(String name) {
        return mGenericMap.get(name);
    }

    public void load(FileHandle handle) {
        load(FileUtils.parseXml(handle));
    }

    public void load(XmlReader.Element root) {
        assert(root != null);
        mGenericMap.clear();
        mWorldMaps.clear();
        loadGenericItems(root.getChildByName("generic"));
        for (XmlReader.Element element: root.getChildrenByName("world")) {
            loadWorldItems(element);
        }
    }

    public static MealItemDb getInstance() {
        if (sInstance == null) {
            sInstance = new MealItemDb();
        }
        return sInstance;
    }

    public Array<MealItem> getItemsForLevel(int worldIndex, int levelIndex) {
        Array<MealItem> lst = new Array<MealItem>();
        for (MealItem item: mGenericMap.values()) {
            if (item.isAvailableInLevel(worldIndex, levelIndex)) {
                // This is not very efficient, but is necessary to be able to return world-specific items
                lst.add(get(worldIndex, item.getName()));
            }
        }
        return lst;
    }

    private void loadGenericItems(XmlReader.Element root) {
        for (int idx = 0; idx < root.getChildCount(); ++idx) {
            XmlReader.Element element = root.getChild(idx);
            String type = element.getAttribute("type");
            MealItem item = null;
            if (type.equals("burger")) {
                item = new BurgerItem(MealItem.WORLD_INDEX_GENERIC, element);
            } else if (type.equals("drink")) {
                item = new MealItem(MealItem.WORLD_INDEX_GENERIC, Type.DRINK, element);
            } else if (type.equals("side-order")) {
                item = new MealItem(MealItem.WORLD_INDEX_GENERIC, Type.SIDE_ORDER, element);
            }
            assert(item != null);
            mGenericMap.put(item.getName(), item);
        }
    }

    private void loadWorldItems(XmlReader.Element root) {
        int worldIndex = root.getIntAttribute("index") - 1;
        MealItemMap map = new MealItemMap();
        mWorldMaps.put(worldIndex, map);
        for (int idx = 0; idx < root.getChildCount(); ++idx) {
            XmlReader.Element element = root.getChild(idx);
            String name = element.getAttribute("name");
            MealItem original = mGenericMap.get(name);
            assert original != null;
            MealItem item;
            if (original.getType() == Type.BURGER) {
                item = new BurgerItem(worldIndex, (BurgerItem)original);
            } else {
                item = new MealItem(worldIndex, original);
            }
            item.initFromXml(element);
            map.put(item.getName(), item);
        }
    }
}