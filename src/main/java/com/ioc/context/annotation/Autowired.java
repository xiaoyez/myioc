package com.ioc.context.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.FIELD,ElementType.CONSTRUCTOR,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    public String value() default "";

    public String name() default  "";
}
