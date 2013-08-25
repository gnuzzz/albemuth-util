package ru.albemuth.util;

import junit.framework.TestCase;

import java.lang.reflect.Proxy;

import ru.albemuth.util.deprecated.GenericInvocationHandler;

public class TestGenericInvocationHandler extends TestCase {

    public void test() {
        Value a1 = new ValueImpl("a1", "a1");
        Value a = (Value)Proxy.newProxyInstance(Value.class.getClassLoader(), new Class[]{Value.class}, new GenericInvocationHandler<Value>(a1));
        assertEquals("a1", a.getValue());
        Value a2 = new ValueImpl("a2", "a2");
        ((GenericInvocationHandler<Value>)Proxy.getInvocationHandler(a)).setObject(a2);
        assertEquals("a2", a.getValue());
    }

}
