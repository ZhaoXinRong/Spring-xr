package com.snowruin.xml;

import java.util.Map;

import com.snowruin.bean.GenericBeanDefinition;

/**
 * 
 * @author zxm
 *
 */
public class FileSystemXmlApplicationContext  extends XmlApplicationContext{
	
	public Map<String, GenericBeanDefinition> getGenericBeanDefinition(String contextConfigLocation){
		return super.getBeanDefinitionMap(contextConfigLocation);
	}
	
}
