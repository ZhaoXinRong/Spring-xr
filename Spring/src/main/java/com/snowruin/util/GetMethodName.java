package com.snowruin.util;

/**
 * 根据属性名称拼接方法
 * @author zxm
 * @date 2018-11-22
 */
public class GetMethodName {

	public static String getSetMethodNameByField(String propertyName) {
		return "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
	}
}
