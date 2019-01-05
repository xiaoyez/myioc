package com.ioc.beans;

import com.ioc.util.StringUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface BeanDefinitionRegistry {


    void registry(String name,BeanDefinition beanDefinition);

    Object get(String name);

    Object remove(String name);

    void removeAll();

    void destroy();
}
