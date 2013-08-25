package ru.albemuth.util;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 09.08.2004
 * Time: 14:02:52
 * To change this template use Options | File Templates.
 */
public class PoolException extends Exception {

    public PoolException(String aMessage) {
        super(aMessage);
    }

    public PoolException(Throwable aCause) {
        super(aCause);
    }

    public PoolException(String aMessage, Throwable aCause) {
        super(aMessage, aCause);
    }

}
