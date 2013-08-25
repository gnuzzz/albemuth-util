package ru.albemuth.util;

import java.util.*;

public abstract class Mapper<ID, CrossRef> {

    protected Map<ID, List<CrossRef>> createMap() {
        return new HashMap<ID, List<CrossRef>>();
    }

    protected List<CrossRef> createList() {
        return new ArrayList<CrossRef>();
    }

    protected abstract ID id(CrossRef ref);

    public Map<ID, List<CrossRef>> map(Collection<? extends CrossRef> refs) {
        Map<ID, List<CrossRef>> map = createMap();
        for (CrossRef ref: refs) {
            ID id = id(ref);
            List<CrossRef> list = map.get(id);
            if (list == null) {
                list = createList();
                map.put(id, list);
            }
            list.add(ref);
        }
        return map;
    }

}