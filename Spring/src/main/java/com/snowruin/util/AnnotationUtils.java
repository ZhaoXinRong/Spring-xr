package com.snowruin.util;


/**
 * 注解工具类
 * @author zxm
 * @date 2018-11-22
 */
public class AnnotationUtils {
	
	/**
	 * 判断注解对象是否为空
	 * @param t  注解对象
	 * @return
	 */
	public static <T> boolean isEmpty(T t) {
		return t == null ? true : false;
	}
}
