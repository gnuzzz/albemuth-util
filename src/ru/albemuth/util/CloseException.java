package ru.albemuth.util;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 13.03.2007
 * Time: 21:53:51
 */
public class CloseException extends Exception {

    public CloseException(String message) {
        super(message);
    }

    public CloseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloseException(Throwable cause) {
        super(cause);
    }

}
