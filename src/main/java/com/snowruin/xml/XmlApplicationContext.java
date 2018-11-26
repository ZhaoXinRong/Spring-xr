package com.snowruin.xml;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.snowruin.bean.ChildBeanDefinition;
import com.snowruin.bean.GenericBeanDefinition;
import com.snowruin.constans.Constants;
import com.snowruin.rules.IOCRules;
import com.snowruin.util.CompoentScanUtils;
import com.snowruin.util.ListUtils;
import com.snowruin.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * xml 的解析，模仿IOC注入到BeanDefinition 中， 实际注入的是GenericBeanDeinition
 * @author zxm
 * @date 2018-11-22
 */
@Slf4j
public class XmlApplicationContext {

	/**
	 * 将 xml 中的bean 注入到map容器中
	 * @param contextConfigLocation
	 * @return
	 */
	public Map<String, GenericBeanDefinition> getBeanDefinitionMap(String contextConfigLocation){

		Map<String, GenericBeanDefinition> beanDefinitionXmlMap = Maps.newConcurrentMap();
		List<Element> elements = this.getElements(contextConfigLocation);
		// 遍历bean 。，注入到beanDefinitionMap
		for (Element element : elements) {
			if ("bean".equals(element.getName())) {
				// 声明一个bean 的map ，用来存放当前的bean的子元素
				GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
				List<ChildBeanDefinition> childBeanDefinitions = Lists.newArrayList();
				String beanId = element.attributeValue(IOCRules.BEAN_RULE.getName());
				String beanClass = element.attributeValue(IOCRules.BEAN_RULE.getValue());
				// 保证子元素确实存在
				if(StringUtils.isNotEmpty(beanId) && StringUtils.isNotEmpty(beanClass)) {
					// 当前的bean的className
					genericBeanDefinition.setClassName(beanClass);
					// 当前bean的所有子元素
					@SuppressWarnings("unchecked")
					List<Element> childrenElements = element.elements();
					if(ListUtils.isNotEmpty(childrenElements)) {
						for (Element childrenElement : childrenElements) {
							if(IOCRules.SET_INJECTION.getType() .equals(childrenElement.getName())) {
								ChildBeanDefinition childBeanDefinition = new ChildBeanDefinition()
																			.setChildrenType(IOCRules.SET_INJECTION.getType());
								
								String name = IOCRules.SET_INJECTION.getName();
								String value = IOCRules.SET_INJECTION.getValue();
								this.setChildBeanDefinitionByType(childrenElement, childBeanDefinition, name, value, childBeanDefinitions);
							}else if (IOCRules.CONSTRUCTOR_INJECTION.getType() .equals(childrenElement.getName())) {
								ChildBeanDefinition childBeanDefinition = new ChildBeanDefinition()
																		.setChildrenType(IOCRules.SET_INJECTION.getType());
								String name = IOCRules.CONSTRUCTOR_INJECTION.getName();
								String value = IOCRules.CONSTRUCTOR_INJECTION.getValue();
								setChildBeanDefinitionByType(childrenElement, childBeanDefinition, name, value, childBeanDefinitions);
							}
						}
					}
				}else {
					log.info("{}下面没有元素",beanId);
				}
				genericBeanDefinition.setChildBeanDefinitions(childBeanDefinitions);
				beanDefinitionXmlMap.put(beanId, genericBeanDefinition);
			}
		}
		return beanDefinitionXmlMap;
	}
	
	
	/**
	 * 根据指定的xml，获得注解扫描的bean 容器
	 * @param contextConfigLocation
	 * @return
	 */
	public List<String> getComponentList(String contextConfigLocation){
		List<String> componentList = Lists.newArrayList();
		List<Element> elements = this.getElements(contextConfigLocation);
		for (Element element : elements) {
			if(element.getName() .equals(IOCRules.SCAN_RULE.getType())) {
				String packageName = element.attributeValue(IOCRules.SCAN_RULE.getName());
				componentList.addAll(resolveComponentList(packageName));
			}
		}
		return componentList;
	}
	
	/**
	 * 根据要扫描的包名，返回有注解扫描的类
	 * @param packageName
	 * @return
	 */
	public List<String> resolveComponentList(String packageName){
		if(StringUtils.isEmpty(packageName)) {
			throw new RuntimeException("请正确配置" + IOCRules.SCAN_RULE.getType() + "的属性");
		}
		List<String> compomentList = Lists.newArrayList();
		List<String> compomentListAfter = CompoentScanUtils.getCompomentList(packageName);
		compomentList.addAll(compomentListAfter);
		return compomentList;
	}
	
	/**
	 * 将每个bean 的子元素注入到容器中
	 * @param element
	 * @param childBeanDefinition
	 * @param name
	 * @param value
	 * @param childBeanDefinitions
	 */
	private  void setChildBeanDefinitionByType(Element element,ChildBeanDefinition childBeanDefinition,String name , String value,List<ChildBeanDefinition> childBeanDefinitions) {
		
		if(!ListUtils.isNull(childBeanDefinitions)) {
			childBeanDefinition.setAttributeOne(element.attributeValue(name))
			.setAttributeTwo(element.attributeValue(value));
			childBeanDefinitions.add(childBeanDefinition);
		}else {
			throw new RuntimeException("未按照格式配置xml文件或者暂不支持配置属性");
		}
	} 
	
	/**
	 * 获取bean 的所有子元素
	 * @param contextConfigLocation
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Element> getElements(String contextConfigLocation){
		// 创建 reader 对象
		SAXReader saxReader = new SAXReader();
		Document document = null ;
		String pathName = Constants.PATH + contextConfigLocation;
		try {
			document = saxReader.read(new File(pathName));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			log.info("未找到文件{}",pathName);
			e.printStackTrace();
		}
		// 获取跟节点
		Element rootElement = document.getRootElement();
		// 返回所有的bean 节点
		return rootElement.elements();
	}
	
}
