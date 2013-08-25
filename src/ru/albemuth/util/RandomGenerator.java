package ru.albemuth.util;

import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 02.08.2004
 * Time: 14:46:36
 * To change this template use Options | File Templates.
 */
public class RandomGenerator {

    public static String randomString(int aLength) {
        String ret = "";
        for (int i = 0; i < aLength; i++) {
            ret += (char)randomInt(64, 123);
        }
        return ret;
    }

    public static int randomInt(int aMin, int aMax) {
        return (int)randomLong(aMin, aMax);
    }

    public static byte randomByte(byte aMin, byte aMax) {
        return (byte)randomLong(aMin, aMax);
    }

    public static Date randomDate(Date aMin, Date aMax) {
        long t1 = aMin.getTime();
        long t2 = aMax.getTime();
        return new Date(randomLong(t1, t2));
    }

    public static Time randomTime(Time aMin, Time aMax) {
        long t1 = aMin.getTime();
        long t2 = aMax.getTime();
        return new Time(randomLong(t1, t2));
    }

    public static Timestamp randomTimestamp(Timestamp aMin, Timestamp aMax) {
        long t1 = aMin.getTime();
        long t2 = aMax.getTime();
        return new Timestamp(randomLong(t1, t2));
    }

    public static boolean randomBoolean() {
        return randomLong(0, 10) % 2 == 0;
    }

    public static long randomLong(long aMin, long aMax) {
        return (long)(Math.random() * (aMax - aMin) + aMin);
    }

    public static float randomFloat(float aMin, float aMax) {
        return (float)randomDouble(aMin, aMax);
    }

    public static double randomDouble(double aMin, double aMax) {
        return Math.random() *  (aMax - aMin) + aMin;
    }

}
