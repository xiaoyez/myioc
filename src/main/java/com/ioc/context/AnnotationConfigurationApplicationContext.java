package com.ioc.context;


import com.ioc.beans.BeanDefinition;
import com.ioc.beans.BeanDefinitionRegistry;
import com.ioc.context.annotation.*;
import com.ioc.support.IsNotConfigurationClass;
import com.ioc.util.AnnotationUtil;
import com.ioc.util.StringUtil ;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class AnnotationConfigurationApplicationContext extends AbstractApplicationContext {
    Class configurationClass;

    public AnnotationConfigurationApplicationContext(Class configurationClass) {
        super();
        this.configurationClass = configurationClass;
        refresh();
    }

    public AnnotationConfigurationApplicationContext(BeanDefinitionRegistry beanDefinitionRegistry, Class configurationClass) {
        super(beanDefinitionRegistry);
        this.configurationClass = configurationClass;
        refresh();
    }

    public void init() {
        Annotation annotation = configurationClass.getAnnotation(Configuration.class);
        if (annotation == null)
        {
            throw new IsNotConfigurationClass("the class " + configurationClass.getName() + "is not a configuration class");
        }
        else
        {
            try {
                loadBeanDefinitions();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadBeanDefinitions() throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException, ClassNotFoundException {

        //将配置类对象注册到ioc容器
        Object config = configurationClass.newInstance();
        BeanDefinition configBean = new BeanDefinition(configurationClass.getName(),config,configurationClass,"singleton");
        registry.registry(configurationClass.getName(),configBean);

        //扫描Bean
        Method[] methods = configurationClass.getMethods();
        for (Method method : methods) {
            if (!AnnotationUtil.existAnnotation(method,Bean.class))
            {
                continue;
            }
            else
            {
                Bean beanAnnotation = method.getAnnotation(Bean.class);

                //获取bean的名称
                String name = beanAnnotation.value();
                if (!StringUtil.hasText(name))
                {
                    name = method.getName();
                }

                loadBean(method,name);
            }
        }

        //扫描组件
        if (AnnotationUtil.existAnnotation(configurationClass,ComponentScan.class))
        {
            ComponentScan componentScanAnnoation = (ComponentScan) configurationClass.getAnnotation(ComponentScan.class);
            String basePackage = componentScanAnnoation.basePackage();
            scanComponent(basePackage);
        }
    }



    protected void loadBean(AnnotatedElement element, String name) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        String scope = RequiredScope.SINGLETON;
        if (AnnotationUtil.existAnnotation(element,Scope.class))
        {
            scope = AnnotationUtil.getAnnotation(element,Scope.class).value();
        }

        Object bean = null;
        if (element instanceof  Class)
        {
            bean = ((Class)element).newInstance();
            injectionField(bean);
        }
        if (element instanceof Method)
        {
            Method method = (Method)element;
            Object config = getBean(configurationClass);

            //标注在方法上的@Autowired
            if (AnnotationUtil.existAnnotation(method,Autowired.class))
            {
                Class<?>[] parameterTypes = method.getParameterTypes();
                Object[] args = new Object[parameterTypes.length];
                Autowired autowiredAnnotation = method.getAnnotation(Autowired.class);

                for (int i = 0; i < parameterTypes.length; i++)
                {
                    Object arg = autowire(autowiredAnnotation,parameterTypes[i]);
                    args[i] = arg;
                }
                bean = method.invoke(config,args);
            }
            else
            {
                Parameter[] parameters = method.getParameters();
                if (parameters == null || parameters.length == 0)
                {
                    bean = method.invoke(config);
                }
                else
                {
                    //方法参数里的@Autowired
                    Object[] args = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++)
                    {
                        if (AnnotationUtil.existAnnotation(parameters[i],Autowired.class))
                        {
                            Autowired autowiredAnnotation = parameters[i].getAnnotation(Autowired.class);
                            Class<?> type = parameters[i].getType();
                            Object arg = autowire(autowiredAnnotation,type);
                            args[i] = arg;
                        }
                        bean = method.invoke(config,args);
                    }
                }
            }
        }
        BeanDefinition beanDefinition = new BeanDefinition(name,bean,bean.getClass(),scope);
        registBeanDefinition(beanDefinition);
    }



}
