package ru.albemuth.util.deprecated;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: -
 * Date: 03.08.2007
 * Time: 2:39:20
 */
public class GenericInvocationHandler<T> implements InvocationHandler {

    protected T object;

    public GenericInvocationHandler(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public Object invoke(Object proxy, Method method, Object[] objects) throws Throwable {
        return method.invoke(this.object, objects);
    }

}
