package ru.albemuth.util;

import junit.framework.TestCase;

public class TestCorrelation extends TestCase {

    public void test() {
        try {
            Correlation c = new Correlation("test");
            c.addValues(1, 10);
            c.addValues(2, 20);
            c.addValues(3, 29);
            assertEquals(0.9995, c.correlation(), 0.00005);

            c.clear();
            for (double i = 0; i < 1000 * Math.PI; i += Math.PI/6) {
                c.addValues(i, Math.sin(i));
            }
            System.out.println(c.correlation());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
