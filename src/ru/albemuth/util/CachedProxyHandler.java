package ru.albemuth.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.InvocationHandler;


public abstract class CachedProxyHandler<T> implements InvocationHandler {

    protected long ttl;
    protected long expires;
    protected T value;

    public CachedProxyHandler(long ttl) {
        this.ttl = ttl;
    }

    protected boolean isExpired() {
        return ttl != Cache.NEVER_EXPIRED && System.currentTimeMillis() > expires;
    }

    protected T value() {
        return value;
    }

    protected abstract T obtainValue();

    public T getValue() {
        if (value() == null || isExpired()) {
            synchronized(this) {
                if (value() == null || isExpired()) {
                    setValue(obtainValue());
                }
            }
        }
        return value();
    }

    protected void setValue(T value) {
        this.value = value;
        this.expires = System.currentTimeMillis() + ttl;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(getValue(), args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    public static <T> T createProxy(CachedProxyHandler<T> handler, Class... classes) {
        return (T)java.lang.reflect.Proxy.newProxyInstance(handler.getClass().getClassLoader(), classes, handler);
    }

    public static class NeverExpired<T> extends CachedProxyHandler<T> implements Externalizable {

        public NeverExpired() {
            super(Cache.NEVER_EXPIRED);
        }

        public NeverExpired(T value) {
            super(Cache.NEVER_EXPIRED);
            this.value = value;
        }

        public T getValue() {
            if (value == null) {
                synchronized (this) {
                    if (value == null) {
                        value = obtainValue();
                    }
                }
            }
            return value;
        }

        protected T obtainValue() {
            return value;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(value);
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            value = (T)in.readObject();
        }

    }

}
