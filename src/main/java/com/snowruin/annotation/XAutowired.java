package com.snowruin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注入
 * @author zxm
 * @date 2018-11-22
 */
@Target({
	ElementType.TYPE,
	ElementType.TYPE_PARAMETER,
	ElementType.CONSTRUCTOR,
	ElementType.METHOD,
	ElementType.FIELD
})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface XAutowired {
	/**
	 * 是否必须  默认是
	 * @return
	 */
	boolean required() default true;
}
