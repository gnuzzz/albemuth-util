package ru.albemuth.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class FilteredCollection<Item> implements Collection<Item> {

    private Collection<Item> collection;
    private Collection<Item> filteredCollection;

    public FilteredCollection(Collection<Item> collection) {
        this.collection = collection;
    }

    protected Collection<Item> getFilteredCollection() {
        if (filteredCollection == null) {
            Collection<Item> items = new ArrayList<Item>();
            for (Item item : this) {
                items.add(item);
            }
            filteredCollection = items;
        }
        return filteredCollection;
    }

    @Override
    public int size() {
        return getFilteredCollection().size();
    }

    @Override
    public boolean isEmpty() {
        return getFilteredCollection().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getFilteredCollection().contains(o);
    }

    @Override
    public Iterator<Item> iterator() {
        Iterator<Item> iterator;
        if (filteredCollection != null) {
            iterator = filteredCollection.iterator();
        } else {
            iterator = new FilterIterator<Item>(collection.iterator()) {
                @Override
                protected boolean accept(Item item) {
                    return FilteredCollection.this.accept(item);
                }
            };
        }
        return iterator;
    }

    @Override
    public Object[] toArray() {
        return getFilteredCollection().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getFilteredCollection().toArray(a);
    }

    @Override
    public boolean add(Item item) {
        boolean ret = false;
        if (accept(item)) {
            ret = collection.add(item);
            if (filteredCollection != null) {
                ret = filteredCollection.add(item);
            }
        }
        return ret;
    }

    @Override
    public boolean remove(Object o) {
        boolean ret = getFilteredCollection().remove(o);
        if (ret) {
            collection.remove(o);
        }
        return ret;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getFilteredCollection().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Item> c) {
        boolean ret = true;
        for (Item item: c) {
            ret &= add(item);
        }
        return ret;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean ret = true;
        for (Object o: c) {
            ret &= remove(o);
        }
        return ret;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Collection<Item> toRemove = new ArrayList<Item>();
        for (Item item: getFilteredCollection()) {
            if (!c.contains(item)) {
                toRemove.add(item);
            }
        }
        return removeAll(toRemove);
    }

    @Override
    public void clear() {
        collection.removeAll(getFilteredCollection());
        getFilteredCollection().clear();
    }

    public abstract boolean accept(Item item);

}
