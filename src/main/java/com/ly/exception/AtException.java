package com.ly.exception;

/**
 * Created by luoyoujun on 2019/5/29.
 */
public class AtException extends RuntimeException {

    public AtException() {
        super();
    }

    public AtException(String message) {
        super(message);
    }

    public AtException(String message, Throwable cause) {
        super(message, cause);
    }

    public AtException(Throwable cause) {
        super(cause);
    }

    protected AtException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
