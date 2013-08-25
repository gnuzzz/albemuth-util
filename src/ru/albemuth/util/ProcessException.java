package ru.albemuth.util;

public class ProcessException extends Exception {

    public ProcessException(String s) {
        super(s);
    }

    public ProcessException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ProcessException(Throwable throwable) {
        super(throwable);
    }

}
