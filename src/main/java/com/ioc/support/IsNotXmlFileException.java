package com.ioc.support;

import com.ioc.support.IsNotFileException;

public class IsNotXmlFileException extends IsNotFileException {
    public IsNotXmlFileException(String message) {
        super(message);
    }
}
