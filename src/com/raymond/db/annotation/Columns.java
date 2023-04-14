package com.raymond.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-09 14:26
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Columns {
    String name() default "";
}
