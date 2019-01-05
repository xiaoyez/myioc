package com.ioc.context;

import com.ioc.beans.BeanDefinition;
import com.ioc.beans.BeanDefinitionRegistry;
import com.ioc.beans.DefaultBeanDefinitionRegistry;
import com.ioc.context.annotation.*;
import com.ioc.util.AnnotationUtil;
import com.ioc.util.ClassUtil;
import com.ioc.util.ObjectUtil;
import com.ioc.util.StringUtil;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractApplicationContext implements ConfigurableApplicationContext{

    protected BeanDefinitionRegistry registry;

    public AbstractApplicationContext()
    {
        registry = new DefaultBeanDefinitionRegistry();
    }

    public AbstractApplicationContext(BeanDefinitionRegistry beanDefinitionRegistry)
    {
        this.registry = beanDefinitionRegistry;
    }

    public void refresh() {
        init();
    }

    public <T> T getBean(String name, Class<T> type) {
        Object bean = registry.get(name);
        return (T)bean;
    }

    public <T> T getBean(Class<T> type) {
        DefaultBeanDefinitionRegistry defaultBeanDefinitionRegistry = (DefaultBeanDefinitionRegistry)registry;
        Map<String, BeanDefinition> container = defaultBeanDefinitionRegistry.getContainer();
        Collection<BeanDefinition> beanDefinitions = container.values();
        int count = 0;
        BeanDefinition beanDefinition = null;
        for (BeanDefinition beanDefinition1 : beanDefinitions)
        {
            if (beanDefinition1.getBeanType() == type)
            {
                count++;
                beanDefinition = beanDefinition1;
            }
        }
        if (count > 1)
        {
            try {
                throw new Exception("can not found single bean of type " + type.getName() + " but found " + count);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            return (T)beanDefinition.getBean();
        return null;
    }

    public Object getBean(String name) {
        return registry.get(name);
    }

    public String[] getBeanDefinitionNames()
    {
        DefaultBeanDefinitionRegistry defaultBeanDefinitionRegistry = (DefaultBeanDefinitionRegistry)registry;
        Map<String, BeanDefinition> container = defaultBeanDefinitionRegistry.getContainer();
        Collection<BeanDefinition> beanDefinitions = container.values();
        String[] names = new String[beanDefinitions.size()];
        int i = 0;
        for (BeanDefinition beanDefinition : beanDefinitions)
        {
            names[i] = beanDefinition.getName();
        }
        return names;
    }

    public void close() {
        this.registry.destroy();
    }

    protected <T> T autowire(Autowired autowired, Class<T> clazz)
    {
        T bean;
        String name = null;
        name = autowired.value();
        if (!StringUtil.hasText(name))
        {
            name = autowired.name();
            if (!StringUtil.hasText(name))
                name = null;
        }
        if (!StringUtil.hasText(name))
        {
            bean = getBean(clazz);
        }
        else
        {
            bean = getBean(name,clazz);
        }
        return bean;
    }

    protected void registBeanDefinition(BeanDefinition beanDefinition)
    {
        registry.registry(beanDefinition.getName(),beanDefinition);
    }

    protected void scanComponent(String basePackage) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {

        List<Class> classList = ClassUtil.searchClass(basePackage);
        for (Class clazz : classList)
        {
            if (clazz != null && AnnotationUtil.existAnnotation(clazz,Component.class))
            {
                String name = AnnotationUtil.getAnnotation(clazz,Component.class).value();
                if (!StringUtil.hasText(name))
                {
                    name = clazz.getName();
                }

                loadBean(clazz,name);
            }
        }
    }

    protected void loadBean(Class clazz, String name) throws IllegalAccessException, InstantiationException {
        String scope = RequiredScope.SINGLETON;
        if (AnnotationUtil.existAnnotation(clazz,Scope.class))
        {
            scope = AnnotationUtil.getAnnotation(clazz,Scope.class).value();
        }

        Object bean = null;
        bean = clazz.newInstance();
        injectionField(bean);
        registBeanDefinition(new BeanDefinition(name,bean,bean.getClass(),scope));
    }


    protected void injectionField(Object bean) throws IllegalAccessException {
        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            if (AnnotationUtil.existAnnotation(field,Value.class))
            {
                String value = AnnotationUtil.getAnnotation(field, Value.class).value();
                Class<?> type = field.getType();
                Object v = ObjectUtil.castToCorrectType(value, type);
                field.set(bean,v);
            }
            if (AnnotationUtil.existAnnotation(field,Autowired.class))
            {
                Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
                Class<?> type = field.getType();
                Object property = autowire(autowiredAnnotation,type);
                field.setAccessible(true);
                field.set(bean,property);
            }
        }
    }
}
