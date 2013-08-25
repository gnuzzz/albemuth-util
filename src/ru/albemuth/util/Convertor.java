package ru.albemuth.util;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Convertor<From, To> {

    public static final Pattern PATTERN_PAIRS_COMMA_SEPARATED = Pattern.compile("([^,=]*)(?:=([^,]*))?");
    public static final Pattern PATTERN_PAIRS_SEMICOLON_SEPARATED = Pattern.compile("([^;=]*)(?:=([^;]*))?");
    public static final Pattern PATTERN_PAIRS_AMPERSAND_SEPARATED = Pattern.compile("([^&=]*)(?:=([^&]*))?");

    public abstract To map(From from);

    public static boolean parseBooleanValue(String value) throws ConvertorException {
        return parseBooleanValue(value, false);
    }

    public static boolean parseBooleanValue(String value, boolean defaultValue) throws ConvertorException {
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public static byte parseByteValue(String value) throws ConvertorException {
        return parseByteValue(value, (byte)0);
    }

    public static byte parseByteValue(String value, byte defaultValue) throws ConvertorException {
        try {
            return value != null ? Byte.parseByte(value) : defaultValue;
        } catch (NumberFormatException e) {
            throw new ConvertorException("Wrong format of byte value " + value, e);
        }
    }

    public static char parseCharValue(String value) throws ConvertorException {
        return parseCharValue(value, (char)0);
    }

    public static char parseCharValue(String value, char defaultValue) throws ConvertorException {
        try {
            return value != null ? (char)Short.parseShort(value) : defaultValue;
        } catch (NumberFormatException e) {
            throw new ConvertorException("Wrong format of byte value " + value, e);
        }
    }

    public static short parseShortValue(String value) throws ConvertorException {
        return parseShortValue(value, (short)0);
    }

    public static short parseShortValue(String value, short defaultValue) throws ConvertorException {
        try {
            return value != null ? Short.parseShort(value) : defaultValue;
        } catch (NumberFormatException e) {
            throw new ConvertorException("Wrong format of short value " + value, e);
        }
    }

    public static int parseIntValue(String value) throws ConvertorException {
        return parseIntValue(value, 0);
    }

    public static int parseIntValue(String value, int defaultVlue) throws ConvertorException {
        try {
            return value != null ? Integer.parseInt(value) : defaultVlue;
        } catch (NumberFormatException e) {
            throw new ConvertorException("Wrong format of int value " + value, e);
        }
    }

    public static long parseLongValue(String value) throws ConvertorException {
        return parseLongValue(value, 0);
    }

    public static long parseLongValue(String value, long defaultValue) throws ConvertorException {
        try {
            return value != null ? Long.parseLong(value) : defaultValue;
        } catch (NumberFormatException e) {
            throw new ConvertorException("Wrong format of long value " + value, e);
        }
    }

    public static float parseFloatValue(String value) throws ConvertorException {
        return parseFloatValue(value, 0);
    }

    public static float parseFloatValue(String value, float defaultValue) throws ConvertorException {
        try {
            return value != null ? Float.parseFloat(value) : defaultValue;
        } catch (NumberFormatException e) {
            throw new ConvertorException("Wrong format of float value " + value, e);
        }
    }

    public static double parseDoubleValue(String value) throws ConvertorException {
        return parseDoubleValue(value, 0);
    }

    public static double parseDoubleValue(String value, double defaultValue) throws ConvertorException {
        try {
            return value != null ? Double.parseDouble(value) : defaultValue;
        } catch (NumberFormatException e) {
            throw new ConvertorException("Wrong format of double value " + value, e);
        }
    }

    public static Map<String, String> parseValues(String valuesString, Pattern valuesPattern) {
        return parseValues(valuesString, valuesPattern, new HashMap<String, String>());
    }

    public static Map<String, String> parseValues(String valuesString, Pattern valuesPattern, Map<String, String> valuesMap) {
        Matcher m = valuesPattern.matcher(valuesString);
        for (; m.find(); ) {
            String name = m.group(1);
            if (name.length() > 0) {
                String value = m.group(2);
                if (value == null) {value = "";}
                valuesMap.put(name, value);
            }
        }
        return valuesMap;
    }

    public static abstract class BooleanConvertor<From> {

        public abstract boolean intValue(From from);

    }

    public static abstract class ByteConvertor<From> {

        public abstract byte byteValue(From from);

    }

    public static abstract class ShortConvertor<From> {

        public abstract short shortValue(From from);

    }

    public static abstract class CharConvertor<From> {

        public abstract char charValue(From from);

    }

    public static abstract class IntConvertor<From> {

        public abstract int intValue(From from);

    }

    public static abstract class LongConvertor<From> {

        public abstract long longValue(From from);

    }

    public static abstract class FloatConvertor<From> {

        public abstract float floatValue(From from);

    }

    public static abstract class DoubleConvertor<From> {

        public abstract double doubleValue(From from);

    }

}
