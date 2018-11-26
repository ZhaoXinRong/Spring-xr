package com.snowruin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * reqeustParam
 * @author zxm
 *
 */
@Target(value = ElementType.PARAMETER)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface XRequestParam {

	String value() default "";
}
