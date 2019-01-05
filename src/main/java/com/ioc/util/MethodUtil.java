package com.ioc.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class MethodUtil {

    public static Type[] getActualTypeArguments(Method method, int index)
    {
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        ParameterizedType parameterizedType = (ParameterizedType)genericParameterTypes[index];
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return actualTypeArguments;
    }

    public static Method getMethodByName(String methodName,Class clazz) {
        Method[] methods = clazz.getMethods();
        Method method = null;
        for (Method m : methods)
        {
            if (m.getName().equals(methodName))
                method = m;
        }
        return method;
    }
}
