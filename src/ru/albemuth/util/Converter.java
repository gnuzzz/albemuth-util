package ru.albemuth.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class Converter<From, To> {

    public abstract To convert(From from);

    public Collection<To> convert(Collection<From> froms) {
        List<To> ret = new ArrayList<To>(froms.size());
        for (From f: froms) {
            ret.add(convert(f));
        }
        return ret;
    }

    public Iterator<To> convert(final Iterator<From> fromIterator) {
        return new Iterator<To>() {

            public boolean hasNext() {
                return fromIterator.hasNext();
            }

            public To next() {
                return convert(fromIterator.next());
            }

            public void remove() {
                fromIterator.remove();
            }
        };
    }

}
