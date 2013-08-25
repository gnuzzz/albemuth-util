package ru.albemuth.util;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.fail;

public class TestLoader {

    @Test
    public void testLoadAsResource() {
        try {
            Loader.SimpleLoader loader = new Loader.SimpleLoader();
            loader.load("/log4j.properties", true);
            TestCase.assertTrue(loader.getData().size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
