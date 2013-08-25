package ru.albemuth.util;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class EntityStorage<V> implements Configured, Closed, Cache<V> {

    public static final int FLAG_LAZY                   = 1;
    public static final int FLAG_LOADED_ONLY            = 2;

    private ConcurrentHashMap<Object, EntityHandler> cache = new ConcurrentHashMap<Object, EntityHandler>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock r = lock.readLock();
    private final Lock w = lock.writeLock();
    private String name;
    private long ttl;


    public void configure(Configuration cfg) throws ConfigurationException {
        this.cache = new ConcurrentHashMap<Object, EntityHandler>();
        this.name = Configuration.getNameFor(this);
        this.ttl = cfg.getLongValue(this, "ttl", 60*60000);
    }

    public void close() throws CloseException {
        clear();
    }

    public String getName() {
        return name;
    }

    public int size() {
        return cache.size();
    }

    public long ttl() {
        return ttl;
    }


    public V get(Object key) {
        return get(key, 0);
    }

    protected EntityHandler getEntityHandler(Object key, boolean createIfNotExists) {
        EntityHandler entityHandler = null;
        r.lock();
        try {
            entityHandler = cache.get(key);
            if (entityHandler == null && createIfNotExists) {
                r.unlock();
                w.lock();
                try {
                    entityHandler = cache.get(key);
                    if (entityHandler == null) {
                        entityHandler = createEntityHandler(key, getEntityClass());
                        cache.put(key, entityHandler);
                    }
                } finally {
                    r.lock();
                    w.unlock();
                }
            }
        } finally {
            r.unlock();
        }
        return entityHandler;
    }

    public V get(Object key, int params) {
        EntityHandler valueHandler = getEntityHandler(key, (params & FLAG_LOADED_ONLY) == 0);
        V value = null;
        if (valueHandler != null) {
            if (!valueHandler.isNullObject() && (params & (FLAG_LAZY | FLAG_LOADED_ONLY)) == 0) {
                synchronized (valueHandler) {
                    if (!valueHandler.isNullObject()) {
                        valueHandler.getValue();
                    }
                }
            }
            if (!valueHandler.isNullObject() && ((params & FLAG_LOADED_ONLY) == 0 || !valueHandler.isLazy())) {
                value = valueHandler.getProxy();
            }
        }
        return value;
    }

    public Collection<V> values() {
        r.lock();
        try {
            Collection<V> values = new ArrayList<V>();
            for (EntityHandler handler: cache.values()) {
                if (!handler.isNullObject() && !handler.isUnloaded() && !handler.isLazy()) {
                    values.add(handler.getProxy());
                }
            }
            return values;
        } finally {
            r.unlock();
        }
    }

    public Set<Object> keySet() {
        return cache.keySet();
    }

    public V put(Object key, V entity, long ttl) {
        EntityHandler valueHandler = getEntityHandler(key, true);
        V oldValue;
        synchronized(valueHandler) {
            if (valueHandler.isRemoved()) {
                valueHandler = getEntityHandler(key, true);
            }
            oldValue = valueHandler.value();
            valueHandler.setTtl(ttl);
            valueHandler.setValue(entity);
        }
        return oldValue;
    }

    public V put(Object key, V value) {
        return put(key, value, ttl());
    }

    public V remove(Object key) {
        EntityHandler valueHandler;
        w.lock();
        try {
            valueHandler = cache.remove(key);
        } finally {
            w.unlock();
        }
        V oldValue = null;
        if (valueHandler != null) {
            synchronized(valueHandler) {
                if (!valueHandler.isLazy() && !valueHandler.isUnloaded() && !valueHandler.isNullObject()) {
                    oldValue = valueHandler.value();
                }
                valueHandler.removedFromCache();
            }
        }
        return oldValue;
    }

    public boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    public void clear() {
        for (Object key: cache.keySet()) {
            remove(key);
        }
    }

    public void refresh() {
        EntityHandler valueHandler;
        for (Object key: cache.keySet()) {
            r.lock();
            try {
                valueHandler = cache.get(key);
            } finally {
                r.unlock();
            }
            if (valueHandler != null && (valueHandler.isUnloaded() || (valueHandler.isExpired() && (valueHandler.isNullObject() || !keepEntityInCache(valueHandler.value()))))) {
                synchronized(valueHandler) {
                    if (valueHandler != null && (valueHandler.isUnloaded() || (valueHandler.isExpired() && (valueHandler.isNullObject() || !keepEntityInCache(valueHandler.value()))))) {
                        remove(key);
                    }
                }
            }
        }
    }

    protected EntityHandler createEntityHandler(Object key, Class entityClass) {
        EntityHandler entityHandler = new EntityHandler(key, ttl());
        V entityProxy = CachedProxyHandler.createProxy(entityHandler, entityClass.getInterfaces());
        entityHandler.setProxy(entityProxy);
        return entityHandler;
    }

    public abstract Class<? extends V> getEntityClass();

    public abstract V loadEntity(Object key);

    protected abstract boolean keepEntityInCache(V entity);

    final class EntityHandler extends CachedProxyHandler<V> {

        protected Object key;
        protected V proxy;
        protected SoftReference<V> softValue;
        protected boolean removed;

        public EntityHandler(Object key, long ttl) {
            super(ttl);
            this.key = key;
        }

        public EntityHandler(Object key, long ttl, V value) {
            super(ttl);
            this.key = key;
            setValue(value);
        }

        protected void setTtl(long ttl) {
            this.ttl = ttl;
        }

        public V getProxy() {
            return proxy;
        }

        public void setProxy(V proxy) {
            this.proxy = proxy;
        }
        
        protected V value() {
            V value;
            if (this.softValue != null) {
                value = this.softValue.get();
            } else {
                value = this.value;
            }
            return value;
        }

        protected void setValue(V value) {
            if (value != null) {
                if (keepEntityInCache(value)) {
                    this.softValue = null;
                    this.value = value;
                } else {
                    this.softValue = new SoftReference<V>(value);
                    this.value = null;
                }
                this.expires = System.currentTimeMillis() + ttl;
            } else {
                key = null;
            }
        }

        protected V obtainValue() {
            return loadEntity(key);
        }

        public boolean isNullObject() {
            return key == null;
        }

        public boolean isLazy() {
            return value == null && softValue == null;
        }

        public boolean isUnloaded() {
            return softValue != null && softValue.get() == null;
        }

        /*public boolean isUnknown() {
            return key != null && value() == null;
        }*/

        public boolean isExpired() {
            return super.isExpired();
        }

        public boolean isRemoved() {
            return removed;
        }

        protected void removedFromCache() {
            this.key = null;
            this.value = null;
            this.softValue = null;
            this.removed = true;
        }

    }

}
