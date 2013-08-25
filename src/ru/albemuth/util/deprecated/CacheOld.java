package ru.albemuth.util.deprecated;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 28.11.2005
 * Time: 15:48:04
 * To change this template use Options | File Templates.
 */
public class CacheOld<T> {

    public static final int DEFAULT_CACHE_CAPACITY                     = 1000;
    public static final long NEVER_EXPIRED                             = -1;
    public static final int GROW_WHEN_EXHAUSTED                        = 0;
    public static final int REPLACE_WHEN_EXHAUSTED                     = 1;

    protected String name;
    protected HashMap<Object, CacheItem<T>> storage;
    protected CacheItem<T>[] queue;
    protected int currentIndex;
    protected long ttl;
    protected int whenExhaustedAction;

    public CacheOld(String name, int capacity, long ttl, int whenExhaustedAction) {
        this.name = name;
        this.storage = new HashMap<Object, CacheItem<T>>();
        this.queue = new CacheItem[capacity];
        this.currentIndex = 0;
        this.ttl = ttl;
        this.whenExhaustedAction = whenExhaustedAction;
    }

    public CacheOld(String name, int capacity) {
        this(name, capacity, NEVER_EXPIRED, REPLACE_WHEN_EXHAUSTED);
    }

    public CacheOld(String name, long ttl) {
        this(name, DEFAULT_CACHE_CAPACITY, ttl, REPLACE_WHEN_EXHAUSTED);
    }

    public CacheOld(String name) {
        this(name, DEFAULT_CACHE_CAPACITY, NEVER_EXPIRED, REPLACE_WHEN_EXHAUSTED);
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return queue.length;
    }

    public long getTtl() {
        return ttl;
    }

    public int getSize() {
        return storage.size();
    }

    public int getWhenExhaustedAction() {
        return whenExhaustedAction;
    }

    public synchronized void put(Object key, T value) {
        if (queue[currentIndex] != null) {
            CacheItem old = queue[currentIndex];
            if (old.isExpired() || whenExhaustedAction == REPLACE_WHEN_EXHAUSTED) {
                remove(old.getKey());
            } else {
                CacheItem[] newQueue = new CacheItem[queue.length * 2];
                if (currentIndex < queue.length - 1 && queue[currentIndex + 1] != null) {
                    System.arraycopy(queue, currentIndex + 1, newQueue, 0, queue.length - currentIndex - 1);
                }
                System.arraycopy(queue, 0, newQueue, queue.length - currentIndex - 1, currentIndex + 1);
                currentIndex = queue.length;
                queue = newQueue;
            }
        }
        long expires = ttl != NEVER_EXPIRED ? System.currentTimeMillis() + ttl : NEVER_EXPIRED;
        CacheItem<T> item = new CacheItem<T>(key, value, expires, currentIndex);
        CacheItem<T> oldItem = storage.put(key, item);
        if (oldItem != null) {
            oldItem.setValue(null);
        }
        queue[currentIndex] = item;
        currentIndex++;
        if (currentIndex >= queue.length) {
            currentIndex = 0;
        }
    }

    public synchronized T get(Object key) {
        T ret = null;
        CacheItem<T> item = storage.get(key);

        if (item != null) {
            if (!item.isExpired()) {
                ret = item.getValue();
            } else {
                storage.remove(key);
                queue[item.getIndex()] = null;
            }
        }
        return ret;
    }

    public synchronized T remove(Object key) {
        T ret = null;
        CacheItem<T> item = storage.remove(key);

        if (item != null) {
            ret = item.getValue();
            queue[item.getIndex()] = null;
            item.setValue(null);
        }
        return ret;
    }

    public synchronized void clear() {
        storage.clear();
    }

    protected final class CacheItem<T> {

        private Object key;
        private T value;
        private long expires;
        private int index;

        public CacheItem(Object key, T value, long expires, int index) {
            this.key = key;
            this.value = value;
            this.expires = expires;
            this.index = index;
        }

        public Object getKey() {
            return key;
        }

        public T getValue() {
            return value;
        }

        protected void setValue(T value) {
            this.value = value;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expires && expires != NEVER_EXPIRED;
        }

        public int getIndex() {
            return index;
        }

    }

}
