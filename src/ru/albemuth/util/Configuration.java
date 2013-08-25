package ru.albemuth.util;

import java.util.Properties;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 15.12.2006
 * Time: 17:56:58
 */
public class Configuration {

    public  static final String PROPERTY_CFG_GROUP                  = "cfg-group";
    private static final Map<Object, String> NAMES                  = Collections.synchronizedMap(new HashMap<Object, String>());

    private Properties properties;

    public Configuration(Properties properties) {
        this.properties = new Properties(properties);
    }

    public Configuration(String name) throws IOException {
        properties = new Properties();
        properties.load(Configuration.class.getResourceAsStream(name));
    }

    public Properties getProperties() {
        return new Properties(properties);
    }

    public String getPropertyValue(String name) throws ConfigurationException {
        return properties.getProperty(name);
    }

    public String getPropertyValue(Class clazz, String name) throws ConfigurationException {
        String value = getPropertyValue(clazz.getName() + "." + name);
        if (value == null) {
            String cfgSelector = getPropertyValue(clazz.getName() + "." + PROPERTY_CFG_GROUP);
            if (cfgSelector != null) {
                value = getPropertyValue(cfgSelector + "." + name);
            }
        }
        return value;
    }

    public String getPropertyValue(Object object, String name) throws ConfigurationException {
        String value = null;
        if (NAMES.get(object) != null) {
            value = getPropertyValue(NAMES.get(object) + "." + name);
        }
        if (value == null) {
            if (object != null) {
                for (Class clazz = object.getClass(); value == null && clazz != null; clazz = clazz.getSuperclass()) {
                    value = getPropertyValue(clazz, name);
                }
            }
            if (value == null) {
                value = getPropertyValue(name);
            }
        }
        return value;
    }

    public String getStringValue(String name) throws ConfigurationException {
        String value = getPropertyValue(name);
        if (value == null) {
            throw new ConfigurationException("Property " + name + " not found");
        }
        return value;
    }

    public String getStringValue(Object object, String name) throws ConfigurationException {
        String value = getPropertyValue(object, name);
        if (value == null) {
            throw new ConfigurationException("Property " + name + " for object of class " + object.getClass().getName() + " not found");
        }
        return value;
    }

    public String getStringValue(Object object, String name, String defaultValue) throws ConfigurationException {
        String value = getPropertyValue(object, name);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    public boolean getBooleanValue(String name) throws ConfigurationException {
        return Boolean.parseBoolean(getStringValue(name));
    }

    public boolean getBooleanValue(Object object, String name, boolean defaultValue) throws ConfigurationException {
        String value = getPropertyValue(object, name);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public boolean getBooleanValue(Object object, String name) throws ConfigurationException {
        return Boolean.parseBoolean(getStringValue(object, name));
    }

    public byte getByteValue(String name) throws ConfigurationException {
        return parseByteValue(getStringValue(name));
    }

    public byte getByteValue(Object object, String name) throws ConfigurationException {
        return parseByteValue(getStringValue(object, name));
    }

    public byte getByteValue(Object object, String name, byte defaultValue) throws ConfigurationException {
        String value = getPropertyValue(object, name);
        if (value == null) {
            return defaultValue;
        } else {
            return parseByteValue(value);
        }
    }

    public short getShortValue(String name) throws ConfigurationException {
        return parseShortValue(getStringValue(name));
    }

    public short getShortValue(Object object, String name) throws ConfigurationException {
        return parseShortValue(getStringValue(object, name));
    }

    public short getShortValue(Object object, String name, short defaultValue) throws ConfigurationException {
        String value = getPropertyValue(object, name);
        if (value == null) {
            return defaultValue;
        } else {
            return parseShortValue(value);
        }
    }

    public int getIntValue(String name) throws ConfigurationException {
        return parseIntValue(getStringValue(name));
    }

    public int getIntValue(Object object, String name) throws ConfigurationException {
        return parseIntValue(getStringValue(object, name));
    }

    public int getIntValue(Object object, String name, int defaultValue) throws ConfigurationException {
        String value = getPropertyValue(object, name);
        if (value == null) {
            return defaultValue;
        } else {
            return parseIntValue(value);
        }
    }

    public long getLongValue(String name) throws ConfigurationException {
        return parseLongValue(getStringValue(name));
    }

    public long getLongValue(Object object, String name) throws ConfigurationException {
        return parseLongValue(getStringValue(object, name));
    }

    public long getLongValue(Object object, String name, long defaultValue) throws ConfigurationException {
        String value = getPropertyValue(object, name);
        if (value == null) {
            return defaultValue;
        } else {
            return parseLongValue(value);
        }
    }

    public float getFloatValue(String name) throws ConfigurationException {
        return parseFloatValue(getStringValue(name));
    }

    public float getFloatValue(Object object, String name) throws ConfigurationException {
        return parseFloatValue(getStringValue(object, name));
    }

    public float getFloatValue(Object object, String name, float defaultValue) throws ConfigurationException {
        String value = getPropertyValue(object, name);
        if (value == null) {
            return defaultValue;
        } else {
            return parseFloatValue(value);
        }
    }

    public double getDoubleValue(String name) throws ConfigurationException {
        return parseDoubleValue(getStringValue(name));
    }

    public double getDoubleValue(Object object, String name) throws ConfigurationException {
        return parseDoubleValue(getStringValue(object, name));
    }

    public double getDoubleValue(Object object, String name, double defaultValue) throws ConfigurationException {
        String value = getPropertyValue(object, name);
        if (value == null) {
            return defaultValue;
        } else {
            return parseDoubleValue(value);
        }
    }

    public static byte parseByteValue(String value) throws ConfigurationException {
        try {
            return value != null ? Byte.parseByte(value) : 0;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Wrong format of byte value " + value, e);
        }
    }

    public static short parseShortValue(String value) throws ConfigurationException {
        try {
            return value != null ? Short.parseShort(value) : 0;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Wrong format of short value " + value, e);
        }
    }

    public static int parseIntValue(String value) throws ConfigurationException {
        try {
            return value != null ? Integer.parseInt(value) : 0;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Wrong format of int value " + value, e);
        }
    }

    public static long parseLongValue(String value) throws ConfigurationException {
        try {
            return value != null ? Long.parseLong(value) : 0;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Wrong format of long value " + value, e);
        }
    }

    public static float parseFloatValue(String value) throws ConfigurationException {
        try {
            return value != null ? Float.parseFloat(value) : 0;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Wrong format of float value " + value, e);
        }
    }

    public static double parseDoubleValue(String value) throws ConfigurationException {
        try {
            return value != null ? Double.parseDouble(value) : 0;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Wrong format of double value " + value, e);
        }
    }

    public static <T> T createInstance(Class<T> baseClass, String className) throws ConfigurationException {
        try {
            Object ret = Class.forName(className).newInstance();
            if (baseClass.isAssignableFrom(ret.getClass())) {
                return (T)ret;
            } else {
                throw new ConfigurationException("Can't create instance of class " + className + ": class " + className + " isn't encode child of " + baseClass.getName());
            }
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("Class " + className + " not found", e);
        } catch (InstantiationException e) {
            throw new ConfigurationException("Can't create instance of class " + className, e);
        } catch (IllegalAccessException e) {
            throw new ConfigurationException("Can't create instance of class " + className, e);
        }
    }

    public static <T> T createInstance(Class<T> baseClass, String className, Object owner, String objectName) throws ConfigurationException {
        T ret = createInstance(baseClass, className);
        String name = NAMES.get(owner);
        name = (name == null ? owner.getClass().getName() : name) + "." + objectName;
        NAMES.put(ret, name);
        return ret;
    }

    public static String getNameFor(Object object) {
        return NAMES.get(object);
    }

}
