package com.snowruin.web.mvc;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.snowruin.annotation.XController;
import com.snowruin.annotation.XRequestMapping;
import com.snowruin.util.AnnotationUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 处理器
 * @author zxm
 * @date 2018-11-23
 */

@Slf4j
public class Handler {
	
	/**
	 * 
	 * @param beanContainerMap
	 * @return
	 */
	public static Map<String, Method> bindRequestMapping(Map<String, Object> beanContainerMap){
		Map<String,Method> handlerMappingMap = Maps.newConcurrentMap();
		
		if(beanContainerMap != null) {
			Set<Entry<String,Object>> entrySet = beanContainerMap.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				Class<? extends Object> clazz = entry.getValue().getClass();
				XController annotation = clazz.getAnnotation(XController.class);
				Method[] methods = clazz.getMethods();
				if(!AnnotationUtils.isEmpty(annotation) && methods != null) {
					for (Method method : methods) {
						XRequestMapping requestMappingAnnotation = method.getAnnotation(XRequestMapping.class);
						if(!AnnotationUtils.isEmpty(requestMappingAnnotation)) {
							String key = requestMappingAnnotation.value();
							handlerMappingMap.put(key, method);
						}
					}
				}
				
			}		
		}else {
			throw new RuntimeException("实例化bean异常，没有找到容器");
		}
		return handlerMappingMap;
	}
	
	
	
	
}
