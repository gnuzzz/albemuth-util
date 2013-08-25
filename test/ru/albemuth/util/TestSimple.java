package ru.albemuth.util;

import junit.framework.TestCase;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class TestSimple extends TestCase /*implements Serializable*/ {

    public void test() {
        try {
            for (int i = 0; i < 100; i++) {
                System.out.println(RandomGenerator.randomInt((int)(750-0.25*750), (int)(750+0.25*750)));
            }
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public static double f(double x) {
        return (-Math.tanh((x/30*24*3600*1000)*18.7 - 18.7) + 1)/2;
    }

    public static double f(double x, double a, double b, double c) {
        return a * Math.tanh(b*x + c);
    }

}


/*
0.0 - 0
0.25 - 0.25
0.5- 0.46
0.75 - 0.63
1.0 - 0.76
2.0 - 0.96
20 - 1.0
*/