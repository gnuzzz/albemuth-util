package ru.albemuth.util;

import junit.framework.TestCase;
import ru.albemuth.util.deprecated.CacheOld;

/**
 * Created by IntelliJ IDEA.
 * User: -
 * Date: 06.07.2007
 * Time: 20:26:22
 */
public class TestCache extends TestCase {

    public void testGrowWhenExhausted() {
        CacheOld<String> cacheOld = new CacheOld<String>("test cacheOld", 2, CacheOld.NEVER_EXPIRED, CacheOld.GROW_WHEN_EXHAUSTED);
        cacheOld.put("1", "1");
        cacheOld.put("2", "2");
        assertEquals(2, cacheOld.getCapacity());
        cacheOld.put("3", "3");
        assertEquals(4, cacheOld.getCapacity());
        assertNotNull(cacheOld.get("1"));
        assertNotNull(cacheOld.get("2"));
        assertNotNull(cacheOld.get("3"));
    }

    public void testReplaceWhenExhausted() {
        CacheOld<String> cacheOld = new CacheOld<String>("test cacheOld", 2, CacheOld.NEVER_EXPIRED, CacheOld.REPLACE_WHEN_EXHAUSTED);
        cacheOld.put("1", "1");
        cacheOld.put("2", "2");
        assertEquals(2, cacheOld.getCapacity());
        cacheOld.put("3", "3");
        assertEquals(2, cacheOld.getCapacity());
        assertNull(cacheOld.get("1"));
        assertNotNull(cacheOld.get("2"));
        assertNotNull(cacheOld.get("3"));
    }

    public void testExpired() {
        try {
            CacheOld<String> cacheOld = new CacheOld<String>("test cacheOld", 2, 200, CacheOld.GROW_WHEN_EXHAUSTED);
            cacheOld.put("1", "1");
            Thread.sleep(100);
            cacheOld.put("2", "2");
            assertNotNull(cacheOld.get("1"));
            assertNotNull(cacheOld.get("2"));
            Thread.sleep(150);
            assertNull(cacheOld.get("1"));
            assertNotNull(cacheOld.get("2"));
            Thread.sleep(100);
            assertNull(cacheOld.get("1"));
            assertNull(cacheOld.get("2"));
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

    }

}
