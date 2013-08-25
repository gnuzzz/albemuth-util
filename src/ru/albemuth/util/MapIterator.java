package ru.albemuth.util;

import java.util.Iterator;

public abstract class MapIterator<From, To> extends Convertor<From, To> implements Iterator<To> {

    private Iterator<From> iterator;

    protected MapIterator(Iterator<From> iterator) {
        this.iterator = iterator;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public To next() {
        return map(iterator.next());
    }

    public void remove() {
        iterator.remove();
    }

}
