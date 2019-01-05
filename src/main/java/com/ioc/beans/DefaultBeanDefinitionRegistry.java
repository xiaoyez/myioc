package com.ioc.beans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanDefinitionRegistry implements BeanDefinitionRegistry {

    public DefaultBeanDefinitionRegistry() {
        beanDefinitionContainer = new ConcurrentHashMap<String, BeanDefinition>();
    }

    Map<String,BeanDefinition> beanDefinitionContainer;

    public void registry(String name, BeanDefinition beanDefinition) {
        beanDefinitionContainer.put(name,beanDefinition);
    }

    public Object get(String name) {
        return beanDefinitionContainer.get(name).getBean();
    }

    public Object remove(String name) {
        return beanDefinitionContainer.remove(name);
    }

    public void removeAll() {
        beanDefinitionContainer.clear();
    }

    public void destroy() {
        beanDefinitionContainer.clear();
    }

    public Map<String,BeanDefinition> getContainer()
    {
        return beanDefinitionContainer;
    }
}
