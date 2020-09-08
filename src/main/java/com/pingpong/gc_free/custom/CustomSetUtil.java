package com.pingpong.gc_free.custom;


import gnu.trove.set.hash.THashSet;

import java.lang.reflect.Field;
import java.nio.channels.Selector;
import java.util.Set;

public class CustomSetUtil {

    private Set keys;
    private Set publicKeys;
    private THashSet selectedKeys;
    private Set publicSelectedKeys;


    public CustomSetUtil() {
        this.keys = new CustomHashSet();
        this.publicKeys = new CustomHashSet();
        this.selectedKeys = new THashSet();
        this.publicSelectedKeys = CustomHashSet.ungrowableSet(selectedKeys);
    }

    public void substitudeSelectedKeysSet(Selector selector) {
        substitudeToSet(selector, "keys", keys);
        substitudeToSet(selector, "publicKeys", publicKeys);
        substitudeToSet(selector, "selectedKeys", selectedKeys);
        substitudeToSet(selector, "publicSelectedKeys", publicSelectedKeys);
    }


    private void substitudeToSet(Selector selector, String mapName, Set customSet) {
        Field field = null;
        try {
            field = selector.getClass().getSuperclass().getDeclaredField(mapName);
            field.setAccessible(true);

            if (!"publicSelectedKeys".equals(mapName)) {
                Set values = (Set) field.get(selector);
                customSet.addAll(values);
            }

            field.set(selector, customSet);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            field.setAccessible(false);
        }
    }

}
