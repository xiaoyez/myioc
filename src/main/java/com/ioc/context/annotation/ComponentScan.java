package com.ioc.context.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ComponentScan {

    public String value() default "";

    public String basePackage();
}
