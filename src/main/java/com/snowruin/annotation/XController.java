package com.snowruin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * controller 注解
 * @author zxm
 * @date 2018-11-22
 */
@Target(value = ElementType.TYPE )
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface XController {
	
	// 注解的名称
	String value() default "";
	
}
