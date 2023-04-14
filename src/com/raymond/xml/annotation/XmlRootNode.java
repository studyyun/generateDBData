package com.raymond.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 根节点注解
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-04 09:39
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlRootNode {
    String name() default "";
}
