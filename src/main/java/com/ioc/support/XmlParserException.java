package com.ioc.support;

public class XmlParserException extends RuntimeException {

    public XmlParserException() {
    }

    public XmlParserException(String message) {
        super(message);
    }

    public XmlParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlParserException(Throwable cause) {
        super(cause);
    }

    public XmlParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
