package com.ioc.context;

import com.ioc.beans.*;
import com.ioc.support.FileResolveException;
import com.ioc.support.IsNotFileException;
import com.ioc.support.NoSuchConstructorException;
import com.ioc.util.FileUtil;
import com.ioc.util.ObjectUtil;
import com.ioc.util.StringUtil;
import org.dom4j.DocumentException;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ClasspathXmlApplicationContext extends AbstractApplicationContext {

    private String configFilePath;
    private XmlDefinitionParser parser;


    public ClasspathXmlApplicationContext(String configFilePath) throws FileResolveException, FileNotFoundException {
        super();
        this.configFilePath = FileUtil.resolvePath(configFilePath,true);
        refresh();
    }

    public ClasspathXmlApplicationContext(String configFilePath,BeanDefinitionRegistry beanDefinitionRegistry) throws FileResolveException, FileNotFoundException {
        super(beanDefinitionRegistry);
        this.configFilePath = FileUtil.resolvePath(configFilePath,true);
        refresh();
    }




    public void init() {
        parser = new XmlDefinitionParser(registry);
        try {

            Map<String, Object> map = parser.parse(configFilePath);
            List<BeanConfiguration> beanConfigurations = (List<BeanConfiguration>) map.get(ConfigurationConst.BEAN_CONFIGURATION);
            for (BeanConfiguration beanConfiguration : beanConfigurations)
            {
                BeanDefinition beanDefinition= buildBeanDefinition(beanConfiguration);
                registBeanDefinition(beanDefinition);
            }

            List<String> basePackages = (List<String>) map.get(ConfigurationConst.COMPONENT_SCAN);
            for (String basePackage : basePackages)
            {
                scanComponent(basePackage);
            }
        } catch (IsNotFileException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private BeanDefinition buildBeanDefinition(BeanConfiguration beanConfiguration) throws ClassNotFoundException, NoSuchConstructorException, IllegalAccessException, InvocationTargetException, InstantiationException {

        String name = beanConfiguration.getId();
        Class beanClass = Class.forName(beanConfiguration.getClassName());
        Object bean;

        //构造器
        bean = buildBean(beanConfiguration);

        //注入属性property
        InjectProperty(bean,beanConfiguration);

        //获取scope
        String scope = beanConfiguration.getScope();
        BeanDefinition beanDefinition = new BeanDefinition(name,bean,beanClass,scope);
        return beanDefinition;
    }

    private Object buildBean(BeanConfiguration beanConfiguration) throws ClassNotFoundException, NoSuchConstructorException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object bean;
        Class beanClass = Class.forName(beanConfiguration.getClassName());
        List<BeanConfiguration.ConstructorArg> constructorArgs = beanConfiguration.getConstructorArgs();
        if (constructorArgs != null)
        {
            Object[] args = new Object[constructorArgs.size()];
            for (int i = 0; i < args.length; i++) {
                Object arg = parseValue(constructorArgs.get(i));
                args[i] = arg;
            }
            Constructor[] constructors = beanClass.getConstructors();
            Constructor constructor = null;
            for (Constructor constructor1 : constructors)
            {
                if (constructor1.getParameterCount() == args.length)
                {
                    constructor = constructor1;
                    break;
                }
            }
            if (constructor == null)
            {
                throw new NoSuchConstructorException("Can noy find a Constructor with " + args.length + " Arguments in class " + beanClass.getName());
            }

            Class[] parameterTypes = constructor.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++)
            {
                args[i] = ObjectUtil.castToCorrectType(args[i],parameterTypes[i]);
            }
            bean = constructor.newInstance(args);
        }
        else
        {
            bean = beanClass.newInstance();
        }
        return bean;

    }

    private void InjectProperty(Object bean, BeanConfiguration beanConfiguration) throws InvocationTargetException, IllegalAccessException {
        List<BeanConfiguration.Property> propertyList = beanConfiguration.getPropertyList();
        Class beanClass = bean.getClass();
        if (propertyList != null)
        {
            for (BeanConfiguration.Property property : propertyList)
            {
                //获取属性对应的值
                Object propertyValue = parseValue(property);
                if (propertyValue == null)
                {
                    if (property.getList() != null)
                    {
                        BeanConfiguration.ValueList list = property.getList();
                        List<BeanConfiguration.Value> values = list.getValues();
                        List<Object> valueList = new ArrayList<>(values.size());
                        for (BeanConfiguration.Value value : values)
                        {
                            valueList.add(parseValue(value));
                        }
                        propertyValue = valueList;
                    }
                    else if (property.getMap() != null)
                    {
                        Map<Object,Object> propertyMap = new HashMap<>();
                        Map<BeanConfiguration.Key, BeanConfiguration.Value> map = property.getMap();
                        Set<BeanConfiguration.Key> keySet = map.keySet();
                        for (BeanConfiguration.Key key : keySet)
                        {
                            propertyMap.put(parseValue(key),parseValue(map.get(key)));
                        }
                        propertyValue = propertyMap;
                    }

                    //获取属性名称
                    String propertyName = property.getName();

                    String setterMethodName = "set" + StringUtil.firstLetterToUpperCase(propertyName);
                    Method[] methods = beanClass.getMethods();
                    for (Method method : methods)
                    {
                        if (method.getName().equals(setterMethodName))
                        {
                            method.invoke(bean,propertyValue);
                            break;
                        }
                    }
                }

            }
        }
    }

    public Object parseValue(BeanConfiguration.Value value)
    {
        if (StringUtil.hasText(value.getValue()))
        {
            return value.getValue();
        }
        else if (StringUtil.hasText(value.getRef()))
        {
            return getBean(value.getRef());
        }
        return null;
    }

}
