package com.snowruin.util;

/**
 * 判断是否为基本数据类型
 * @author zxm
 * @date 2018-11-22
 */
public class IsBasicTypeUtils {
	
	/**
	 * 是否为基本数据类型
	 * @param typeName
	 * @return
	 */
	public static boolean isBasicType(String typeName) {
		 if (("String") . equals(typeName)){
	            return true;
	     }else if("Integer" . equals(typeName)){
	            return true;
	     }else if("int" . equals(typeName)){
	            return true;
	     }else if("Long" . equals(typeName)){
	            return true;
	     }else if("Short" . equals(typeName)){
	            return true;
	     }else if("Float" . equals(typeName)){
	            return true;
	     }else if("Double" . equals(typeName)){
	            return true;
	     }else if("Byte" . equals(typeName)){
	            return true;
	     }
	     return false;
	}
}
