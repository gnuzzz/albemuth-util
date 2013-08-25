package ru.albemuth.util;

import java.util.*;

public abstract class FilteredList<Item> implements List<Item> {

    private List<Item> list;
    private List<Item> filteredList;

    protected FilteredList(List<Item> list) {
        this.list = list;
    }

    protected List<Item> getFilteredList() {
        if (filteredList == null) {
            List<Item> items = new ArrayList<Item>();
            for (Item item : this) {
                items.add(item);
            }
            filteredList = items;
        }
        return filteredList;
    }

    @Override
    public int size() {
        return getFilteredList().size();
    }

    @Override
    public boolean isEmpty() {
        return getFilteredList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getFilteredList().contains(o);
    }

    @Override
    public Iterator<Item> iterator() {
        Iterator<Item> iterator;
        if (filteredList != null) {
            iterator = filteredList.iterator();
        } else {
            iterator = new FilterIterator<Item>(list.iterator()) {
                @Override
                protected boolean accept(Item item) {
                    return FilteredList.this.accept(item);
                }
            };
        }
        return iterator;
    }

    @Override
    public Object[] toArray() {
        return getFilteredList().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getFilteredList().toArray(a);
    }

    @Override
    public boolean add(Item item) {
        boolean ret = false;
        if (accept(item)) {
            ret = list.add(item);
            if (filteredList != null) {
                ret = filteredList.add(item);
            }
        }
        return ret;
    }

    @Override
    public boolean remove(Object o) {
        boolean ret = getFilteredList().remove(o);
        if (ret) {
            list.remove(o);
        }
        return ret;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getFilteredList().containsAll(c);
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
    public boolean addAll(int index, Collection<? extends Item> c) {
        throw new AbstractMethodError("Not implement yet");  //todo
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
        for (Item item: getFilteredList()) {
            if (!c.contains(item)) {
                toRemove.add(item);
            }
        }
        return removeAll(toRemove);
    }

    @Override
    public void clear() {
        list.removeAll(getFilteredList());
        getFilteredList().clear();
    }

    @Override
    public Item get(int index) {
        throw new AbstractMethodError("Not implement yet");  //todo
    }

    @Override
    public Item set(int index, Item element) {
        throw new AbstractMethodError("Not implement yet");  //todo
    }

    @Override
    public void add(int index, Item element) {
        throw new AbstractMethodError("Not implement yet");  //todo
    }

    @Override
    public Item remove(int index) {
        throw new AbstractMethodError("Not implement yet");  //todo
    }

    @Override
    public int indexOf(Object o) {
        throw new AbstractMethodError("Not implement yet");  //todo
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new AbstractMethodError("Not implement yet");  //todo
    }

    @Override
    public ListIterator<Item> listIterator() {
        throw new AbstractMethodError("Not implement yet");  //todo
    }

    @Override
    public ListIterator<Item> listIterator(int index) {
        throw new AbstractMethodError("Not implement yet");  //todo
    }

    @Override
    public List<Item> subList(int fromIndex, int toIndex) {
        throw new AbstractMethodError("Not implement yet");  //todo
    }

    public abstract boolean accept(Item item);

}
