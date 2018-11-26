package com.snowruin.util;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * list 集合工具类
 * @author zxm
 * @date 2018-11-22
 */
public class ListUtils {
	
	/**
	 * 将指定元素添加到list中， 并保证list 中没有重复元素
	 * @param list
	 * @param t
	 */
	public static<T> void add(List<T> list, T t) {
		Set<T> set = new HashSet<T>(list);
		if(set.add(t)) {
			list.add(t);
		}
	}
	
	public static <T> boolean isNull(List<T> list) {
		return Objects.isNull(list);
	}
	
	public static <T> boolean isEmpty(List<T> list) {
		return (!isNull(list) && list.isEmpty());
	}
	
	public static <T>  boolean isNotEmpty(List<T> list) {
		return !isEmpty(list);
	}
}
