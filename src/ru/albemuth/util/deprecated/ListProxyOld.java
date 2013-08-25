package ru.albemuth.util.deprecated;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 13.04.2006
 * Time: 19:02:54
 * To change this template use Options | File Templates.
 */
public abstract class ListProxyOld<T> extends CachingProxyOld<List<T>> implements List<T> {

    public int size() {
        return getData().size();
    }

    public boolean isEmpty() {
        return getData().isEmpty();
    }

    public boolean contains(Object o) {
        return getData().contains(o);
    }

    public Iterator<T> iterator() {
        return getData().iterator();
    }

    public Object[] toArray() {
        return getData().toArray();
    }

    public <T>T[] toArray(T[] a) {
        return getData().toArray(a);
    }

    public boolean add(T o) {
        return getData().add(o);
    }

    public boolean remove(Object o) {
        return getData().remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return getData().containsAll(c);
    }

    public boolean addAll(Collection<? extends T> c) {
        return getData().addAll(c);
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        return getData().addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return getData().removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return getData().retainAll(c);
    }

    public void clear() {
        getData().clear();
    }

    public T get(int index) {
        return getData().get(index);
    }

    public T set(int index, T element) {
        return getData().set(index, element);
    }

    public void add(int index, T element) {
        getData().add(index, element);
    }

    public T remove(int index) {
        return getData().remove(index);
    }

    public int indexOf(Object o) {
        return getData().indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return getData().lastIndexOf(o);
    }

    public ListIterator<T> listIterator() {
        return getData().listIterator();
    }

    public ListIterator<T> listIterator(int index) {
        return getData().listIterator(index);
    }

    public List<T> subList(int fromIndex, int toIndex) {
        return getData().subList(fromIndex, toIndex);
    }

}
