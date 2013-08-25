package ru.albemuth.util;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 09.03.2007
 * Time: 17:01:14
 */
public class Accessor<T> {

    public static final String NAME_INSTANCE_DEFAULT            = "default";

    protected static final HashMap<Class, Accessor<?>> accessors = new HashMap<Class, Accessor<?>>();

    protected HashMap<Object, T> instances = new HashMap<Object, T>();

    public synchronized T getInstance(Object key) {
        return instances.get(key);
    }

    public T getDefaultInstance() {
        return getInstance(NAME_INSTANCE_DEFAULT);
    }

    public synchronized void setInstance(Object key, T instance) {
        instances.put(key, instance);
    }

    public void setDefaultInstance(T defaultInstance) {
        setInstance(NAME_INSTANCE_DEFAULT, defaultInstance);
    }

    public static <T> Accessor<T> getAccessor(Class<T> c) {
        synchronized(accessors) {
            Accessor<T> accessor = (Accessor<T>)accessors.get(c);
            if (accessor == null) {
                accessor = new Accessor<T>();
                accessors.put(c, accessor);
            }
            return accessor;
        }
    }

    public static void close() throws CloseException {
        for (Accessor accessor: accessors.values()) {
            for (Object instance: accessor.instances.values()) {
                if (instance instanceof Closed) {
                    ((Closed)instance).close();
                }
            }
        }
    }

}
