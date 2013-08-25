package ru.albemuth.util;

public interface Cache<T> extends Configured, Closed {

    public static final long NEVER_EXPIRED                             = -1;

    public String getName();

    public int size();

    public long ttl();

    public T get(Object key);

    public T get(Object key, int params);

    public T put(Object key, T value);

    public T put(Object key, T value, long ttl);

    public T remove(Object key);

    public boolean containsKey(Object key);

    public void clear();

    public void refresh();

}
