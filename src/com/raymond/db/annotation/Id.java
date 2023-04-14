package com.raymond.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否是主键ID标识
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-14 17:54
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
}
