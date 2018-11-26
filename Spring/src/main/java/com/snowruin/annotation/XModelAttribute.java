package com.snowruin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ModelAttribute
 * @author zxm
 * @date 2018-11-23
 */
@Target(value = {ElementType.METHOD,ElementType.PARAMETER})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface XModelAttribute {
	
	String value() default "";
	
}
