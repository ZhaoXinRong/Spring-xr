package com.snowruin.factory;

import java.util.List;
import java.util.Map;

import com.snowruin.bean.GenericBeanDefinition;
import com.snowruin.xml.XmlApplicationContext;

/**
 * 没有什么实质性的作用，就是提供一个有很多种要注入到容器中的类，
 * 这里提供一个整合，需要获取时直接上这里来获取
 * 实例化前的准备
 * @author zxm
 * @date 2018-11-22
 */
public class BeanDefinition extends XmlApplicationContext {
	
	
	public List<String> getComponentList(String contextConfigLocation){
		return super.getComponentList(contextConfigLocation);
	}

	public Map<String, GenericBeanDefinition> getBeanDefinitionXmlMap(String contextConfigLocation){
		return super.getBeanDefinitionMap(contextConfigLocation);
	}
}
