package com.snowruin.util;

/**
 * 根据传入的属性和类名，将属性名强转为类名的属性
 * @author zxm
 * @date 2018-11-22
 */
public class ConvertUtils {

	public static Object convert(String className,String param) {
		if("String" . equals(className)) {
			return param;
		}else if ("Integer" . equals(className) || "int" . equals(className)) {
			return Integer . valueOf(param);
		}else if ("Float" . equals(className)) {
			return Float . valueOf(param);
		}else if ("Double" . equals(className)) {
			return Double . valueOf(param);
		} else if("Long" . equals(className)) {
			return Long . valueOf(param);
		}else if ("Short" . equals(className)) {
			return Short . valueOf(param);
		} else if("Byte" .equals(className)) {
			return Byte . valueOf(param);
		}else if ("Boolean" . equals(className)) {
			return Boolean . valueOf(param);
		}
		return null;
	}
	
}
