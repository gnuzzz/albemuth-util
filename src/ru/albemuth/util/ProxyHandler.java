package ru.albemuth.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

@Deprecated
public abstract class ProxyHandler<T> implements InvocationHandler {

    protected long ttl;
    protected long expires;

    public ProxyHandler(long ttl) {
        this.ttl = ttl;
    }

    protected abstract T value();

    protected abstract void setValue(T value);

    protected boolean isExpired() {
        return ttl != Cache.NEVER_EXPIRED && System.currentTimeMillis() > expires;
    }

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

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(getValue(), args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    protected abstract T obtainValue();

    public static <T> T createProxy(ProxyHandler<T> handler, Class... classes) {
        return (T)java.lang.reflect.Proxy.newProxyInstance(handler.getClass().getClassLoader(), classes, handler);
    }

}
