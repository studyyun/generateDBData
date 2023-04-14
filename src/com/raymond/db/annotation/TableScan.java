package com.raymond.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * xml包扫描路径
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-09 09:32
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableScan {
    /**
     * xml包路径
     */
    String[] basePackages() default {};
}
