package com.pingpong.gc_free;

import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;

import java.util.*;

public class CustomHashSet extends HashSet {

    private THashSet<Object> set;

    public CustomHashSet() {
        set = new THashSet<>();
    }

    public CustomHashSet(Collection<Object> c) {
        set = new THashSet<Object>(c);
    }

    public CustomHashSet(int initialCapacity, float loadFactor) {
        set = new THashSet<>(initialCapacity, loadFactor);
    }

    public CustomHashSet(int initialCapacity) {
        set = new THashSet<>(initialCapacity);
    }


    @Override
    public Iterator iterator() {
        return set.iterator();
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public boolean add(Object o) {
        return set.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return set.remove(o);
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    public Spliterator spliterator() {
        return set.spliterator();
    }


    public THashSet<Object> getSet() {
        return set;
    }


    static <E> Set<E> ungrowableSet(THashSet<E> var0) {
        return new THashSet<E>() {
            public int size() {
                return var0.size();
            }

            public boolean isEmpty() {
                return var0.isEmpty();
            }

            public boolean contains(Object var1) {
                return var0.contains(var1);
            }

            public Object[] toArray() {
                return var0.toArray();
            }

            public <T> T[] toArray(T[] var1) {
                return var0.toArray(var1);
            }

            public String toString() {
                return var0.toString();
            }

            public TObjectHashIterator<E> iterator() {
                return var0.iterator();
            }

            public boolean equals(Object var1) {
                return var0.equals(var1);
            }

            public int hashCode() {
                return var0.hashCode();
            }

            public void clear() {
                var0.clear();
            }

            public boolean remove(Object var1) {
                return var0.remove(var1);
            }

            public boolean containsAll(Collection<?> var1) {
                return var0.containsAll(var1);
            }

            public boolean removeAll(Collection<?> var1) {
                return var0.removeAll(var1);
            }

            public boolean retainAll(Collection<?> var1) {
                return var0.retainAll(var1);
            }

            public boolean add(E var1) {
                throw new UnsupportedOperationException();
            }

            public boolean addAll(Collection<? extends E> var1) {
                throw new UnsupportedOperationException();
            }

            public boolean forEach(TObjectProcedure procedure) {
                return var0.forEach(procedure);
            }
        };

    }
}
