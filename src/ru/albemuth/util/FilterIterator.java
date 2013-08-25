package ru.albemuth.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class FilterIterator<T> implements Iterator<T> {

    private Iterator<T> iterator;
    private T next;

    public FilterIterator(Iterator<T> iterator) {
        this.iterator = iterator;
        toNext();
    }

    public boolean hasNext() {
        return next != null;
    }

    public T next() {
        if (next == null) throw new NoSuchElementException();
        T returnValue = next;
        toNext();
        return returnValue;
    }

    public void remove() {
        iterator.remove();
    }

    private void toNext() {
        next = null;
        for (; iterator.hasNext(); ) {
            T t = iterator.next();
            if (t != null && accept(t)) {
                next = t;
                break;
            }
        }
    }

    protected abstract boolean accept(T t);

}