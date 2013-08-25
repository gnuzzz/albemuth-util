package ru.albemuth.util;

/**
 * Created by IntelliJ IDEA.
 * User: -
 * Date: 30.11.2007
 * Time: 1:22:09
 */
public class ConvertorException extends Exception {

    public ConvertorException(String s) {
        super(s);
    }

    public ConvertorException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ConvertorException(Throwable throwable) {
        super(throwable);
    }

}
