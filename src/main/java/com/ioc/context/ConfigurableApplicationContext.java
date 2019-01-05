package com.ioc.context;

public interface ConfigurableApplicationContext extends ApplicationContext {

    void close();

    void refresh();
}
