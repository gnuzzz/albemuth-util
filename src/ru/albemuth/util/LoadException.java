package ru.albemuth.util;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 29.05.2006
 * Time: 22:07:12
 * To change this template use Options | File Templates.
 */
public class LoadException extends Exception {

    public LoadException(String aMessage) {
        super(aMessage);
    }

    public LoadException(Throwable aCause) {
        super(aCause);
    }

    public LoadException(String aMessage, Throwable aCause) {
        super(aMessage, aCause);
    }

}
