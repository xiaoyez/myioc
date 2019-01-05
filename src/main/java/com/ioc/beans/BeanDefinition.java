package com.ioc.beans;

import com.ioc.util.ObjectUtil;

import java.lang.reflect.InvocationTargetException;

public class BeanDefinition {
    private String name;

    private Object bean;

    private Class beanType;

    private String scope;

    public BeanDefinition() {
    }

    public BeanDefinition(String name, Object bean, Class beanType,String scope) {
        this.name = name;
        this.bean = bean;
        this.beanType = beanType;
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getBean()
    {
        if (scope.equals("singleton"))
            return bean;
        if (scope.equals("prototype")) {
            try {
                return ObjectUtil.copy(bean);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Class getBeanType() {
        return beanType;
    }

    public void setBeanType(Class beanType) {
        this.beanType = beanType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "name='" + name + '\'' +
                ", bean=" + bean +
                ", beanType=" + beanType +
                ", scope='" + scope + '\'' +
                '}';
    }
}
