package ru.albemuth.util;

import java.util.concurrent.ConcurrentHashMap;
import java.lang.ref.SoftReference;

public class ConcurrentCache<T> implements Cache<T> {

    protected String name;
    protected long ttl;
    protected ConcurrentHashMap<Object, Item<T>> storage;

    public void configure(Configuration cfg) throws ConfigurationException {
        this.name = Configuration.getNameFor(this);
        this.ttl = cfg.getLongValue(this, "ttl", 40*60000);
        this.storage = new ConcurrentHashMap<Object, Item<T>>();
    }

    public String getName() {
        return name;
    }

    public int size() {
        return storage.size();
    }

    public long ttl() {
        return ttl;
    }

    public T get(Object key) {
        Item<T> item = storage.get(key);
        if (item != null) {
            T value = item.getValue();
            if (value == null) {
                storage.remove(key, item);
            }
            return value;
        } else {
            return null;
        }
    }

    public T get(Object key, int params) {
        return get(key);
    }

    public T put(Object key, T value) {
        return put(key, value, ttl);
    }

    public T put(Object key, T value, long ttl) {
        Item<T> item = new Item<T>(key, value, ttl != NEVER_EXPIRED ? System.currentTimeMillis() + ttl : NEVER_EXPIRED);
        item = storage.put(key, item);
        return item != null ? item.getValue() : null;
    }

    public T remove(Object key) {
        Item<T> item = storage.remove(key);
        if (item != null) {
            return item.getValue();
        } else {
            return null;
        }
    }

    public boolean containsKey(Object key) {
        return storage.containsKey(key);
    }

    public void clear() {
        storage.clear();
    }

    public void refresh() {
        for (Object key: storage.keySet()) {
            get(key);
        }
    }

    public void close() {}

    protected class Item<T> {

        private Object key;
        private SoftReference<T> valueReference;
        private long expires;

        protected Item(Object key, T value, long expires) {
            this.key = key;
            this.valueReference = new SoftReference<T>(value);
            this.expires = expires;
        }

        protected Object getKey() {
            return key;
        }

        protected T getValue() {
            return isExpired() ? null : valueReference.get();
        }

        protected boolean isExpired() {
            return System.currentTimeMillis() >= expires && expires != NEVER_EXPIRED;
        }

    }

}
