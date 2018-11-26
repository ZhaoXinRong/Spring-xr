package com.snowruin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * responseBody 注解
 * @author zxm
 * @date 2018-11-26
 */
@Target(value = { ElementType.TYPE , ElementType.METHOD })
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface XResponseBody {

	
}
