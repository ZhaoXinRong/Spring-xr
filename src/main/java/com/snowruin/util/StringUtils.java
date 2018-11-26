package com.snowruin.util;

/**
 * 字符串工具类
 * @author zxm
 * @date 2018-11-22
 */
public class StringUtils {

	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if(null == str || "" . equals(str)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断字符串是否不为空
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
}
