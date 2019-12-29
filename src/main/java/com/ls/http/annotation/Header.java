package com.ls.http.annotation;

public @interface Header {
    String key() default "";
    String value() default "";
}
