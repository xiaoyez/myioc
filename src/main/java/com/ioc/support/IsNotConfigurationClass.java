package com.ioc.support;

public class IsNotConfigurationClass extends RuntimeException {
    public IsNotConfigurationClass() {
    }

    public IsNotConfigurationClass(String message) {
        super(message);
    }
}
