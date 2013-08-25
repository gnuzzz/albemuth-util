package ru.albemuth.util;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: -
 * Date: 29.06.2007
 * Time: 1:55:08
 */
public class TestAccsessor extends TestCase {

    public void testGetAccessor() {
        try {
            Accessor<String> a1 = Accessor.getAccessor(String.class);
            Accessor<String> a2 = Accessor.getAccessor(String.class);
            assertTrue(a1 == a2);
            Accessor<Integer> a3 = Accessor.getAccessor(Integer.class);
            assertFalse((Object)a1 == (Object)a3);
        } catch (Exception e) {
            fail();
        }
    }

    public void testGetInstance() {
        String s1 = new String("s1");
        String s2 = new String("s2");
        Accessor<String> accessor = Accessor.getAccessor(String.class);
        accessor.setInstance("s1", s1);
        accessor.setInstance("s2", s2);
        accessor.setDefaultInstance(s1);

        assertTrue(accessor.getInstance("s1") == s1);
        assertTrue(accessor.getInstance(Accessor.NAME_INSTANCE_DEFAULT) == s1);
        assertTrue(accessor.getDefaultInstance() == s1);
        assertTrue(accessor.getInstance("s2") == s2);
    }

}
