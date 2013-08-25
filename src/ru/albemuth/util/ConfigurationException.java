package ru.albemuth.util;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 15.12.2006
 * Time: 19:24:46
 */
public class ConfigurationException extends Exception {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

}
