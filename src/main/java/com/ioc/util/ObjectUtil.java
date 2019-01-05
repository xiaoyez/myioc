package com.ioc.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

public class ObjectUtil {

    public static Object castToCorrectType(Object value, Class parameterType) {
        if (parameterType == String.class)
        {
            value = value;
        }
        if (parameterType == Integer.class)
        {
            value = Integer.valueOf((String) value);
        }
        if (parameterType == Float.class)
        {
            value = Float.parseFloat((String)value);
        }
        if (parameterType == Double.class)
        {
            value = Double.parseDouble((String) value);
        }
        if (parameterType == Boolean.class)
        {
            if ("true".equals(value))
                value = true;
            else if("false".equals(value))
                value = false;
            else
                throw new IllegalArgumentException("the value can not cast to Boolean");
        }
        return value;
    }

    public static Object copy(Object bean) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Class clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Object copyBean = null;
        Constructor[] constructors = clazz.getConstructors();
        copyBean = constructors[0].newInstance(null);
        Method setMethod = null, getMethod = null;
        for (Field field : fields )
        {
            String setMethodName = "set" + StringUtil.firstLetterToUpperCase(field.getName());
            String getMethodName = "get" + StringUtil.firstLetterToUpperCase(field.getName());
            setMethod = clazz.getMethod(setMethodName,field.getType());
            getMethod = clazz.getMethod(getMethodName);
            setMethod.invoke(copyBean,getMethod.invoke(bean));
        }
        return copyBean;
    }

    public static <T extends Number> T castToSuitType(String value, Class<T> clazz)
    {
        Object v = value;
        if (clazz == Integer.class)
            v = Integer.parseInt(value);
        else if (clazz == Float.class)
            v = Float.parseFloat(value);
        else if (clazz == Double.class)
            v = Double.parseDouble(value);
        else if (clazz == Short.class)
            v = Short.parseShort(value);
        else if (clazz == BigDecimal.class)
            v = BigDecimal.valueOf(Long.parseLong(value));
        else if (clazz == Long.class)
            v = Long.parseLong(value);
        return (T)v;
    }
}
