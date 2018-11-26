package com.snowruin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.snowruin.rules.RequestMethod;


/**
 * XRequestMapping
 * @author zxm
 * @date 2018-11-23
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XRequestMapping {

	  String value() default "";
	  
	  RequestMethod[] method() default {};
}
