package com.ioc.beans;

public interface BeanFactory {

    Object getBean(String name);

    <T>T getBean(String name, Class<T> type);

    <T>T getBean(Class<T> type);


}
