package com.pingpong.gc_free;


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
        substitudeToNewMap(selector, "keys", keys);
        substitudeToNewMap(selector, "publicKeys", publicKeys);
        substitudeToNewMap(selector, "selectedKeys", selectedKeys);
        substitudeToNewMap(selector, "publicSelectedKeys", publicSelectedKeys);
    }


    private void substitudeToNewMap(Selector selector, String fieldName, Set customSet) {
        Field field = null;
        try {
            field = selector.getClass().getSuperclass().getDeclaredField(fieldName);
            field.setAccessible(true);

            if (!"publicSelectedKeys".equals(fieldName)) {
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
