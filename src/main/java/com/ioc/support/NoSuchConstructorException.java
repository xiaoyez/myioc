package com.ioc.support;

public class NoSuchConstructorException extends ReflectiveOperationException {

    public NoSuchConstructorException() {
    }

    public NoSuchConstructorException(String message) {
        super(message);
    }

    public NoSuchConstructorException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchConstructorException(Throwable cause) {
        super(cause);
    }
}
