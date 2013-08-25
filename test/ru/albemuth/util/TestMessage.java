package ru.albemuth.util;

import junit.framework.TestCase;

public class TestMessage extends TestCase {

    public void test() {
        Message message = new Message();

        assertEquals(Message.Status.INFO, message.getStatus("A"));
        message.addItem("A", Message.Status.INFO, "aaa");
        assertEquals(Message.Status.INFO, message.getStatus());
        assertEquals(Message.Status.INFO, message.getStatus("A"));
        assertEquals("aaa", message.getItem("A").getMessage());
        message.addItem("B", Message.Status.WARN, "bbb");
        assertEquals(Message.Status.WARN, message.getStatus());
        assertEquals(Message.Status.WARN, message.getStatus("B"));
        assertEquals("bbb", message.getItem("B").getMessage());
        message.addItem("C", Message.Status.DATA_NEED, "ccc");
        assertEquals(Message.Status.DATA_NEED, message.getStatus());
        assertEquals(Message.Status.DATA_NEED, message.getStatus("C"));
        assertEquals("ccc", message.getItem("C").getMessage());
        message.addItem("D", Message.Status.ERROR, "ddd");
        assertEquals(Message.Status.ERROR, message.getStatus());
        assertEquals(Message.Status.ERROR, message.getStatus("D"));
        assertEquals("ddd", message.getItem("D").getMessage());

        Message.Item item = message.removeItem("B");
        assertNotNull(item);
        assertEquals(Message.Status.ERROR, message.getStatus());
        assertEquals(Message.Status.WARN, item.getStatus());
        assertEquals("bbb", item.getMessage());
        item = message.getItem("B");
        assertNull(item);
        message.removeItem("B");
        assertNull(item);

        item = message.removeItem("D");
        assertNotNull(item);
        assertEquals(Message.Status.DATA_NEED, message.getStatus());
        assertEquals(Message.Status.ERROR, item.getStatus());
        assertEquals("ddd", item.getMessage());
        item = message.getItem("D");
        assertNull(item);
        message.removeItem("D");
        assertNull(item);

        assertEquals(Message.Status.INFO, message.getStatus("E"));
        message.addItem("E", "e1", Message.Status.WARN, "eee1");
        assertEquals(Message.Status.DATA_NEED, message.getStatus());
        assertEquals(Message.Status.WARN, message.getStatus("E"));
        message.addItem("E", "e2", Message.Status.DATA_NEED, "eee2");
        assertEquals(Message.Status.DATA_NEED, message.getStatus());
        assertEquals(Message.Status.DATA_NEED, message.getStatus("E"));
        assertEquals(Message.Status.DATA_NEED, message.getStatus("E", "e2"));
        message.addItem("E", "e3", Message.Status.DATA_NEED, "eee3");
        assertEquals(Message.Status.DATA_NEED, message.getStatus());

        item = message.getItem("E");
        assertNull(item);
        item = message.getItem("E", "e2");
        assertNotNull(item);
        assertEquals("eee2", item.getMessage());

        message.addItem("F", "f1", Message.Status.ERROR, "fff1");
        assertEquals(Message.Status.ERROR, message.getStatus());

        item = message.removeItem("C");
        assertNotNull(item);
        assertEquals(Message.Status.ERROR, message.getStatus());

        item = message.removeItem("E", "e2");
        assertNotNull(item);
        assertEquals(Message.Status.ERROR, message.getStatus());
        item = message.removeItem("F", "f1");
        assertNotNull(item);
        assertEquals(Message.Status.DATA_NEED, message.getStatus());
        item = message.removeItem("E", "e3");
        assertNotNull(item);
        assertEquals(Message.Status.WARN, message.getStatus());
        assertEquals(Message.Status.WARN, message.getStatus("E"));

    }

}
