package com.raymond.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 节点名称注解
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-04 09:54
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlNode {
    String name() default "";

    boolean isChildNodes() default false;
}
