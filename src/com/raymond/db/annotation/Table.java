package com.raymond.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表名
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-09 14:25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name() default "";
}
