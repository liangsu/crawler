package com.ls.http.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Feign {

    String url() default "";

    Header[] headers() default {};
}
