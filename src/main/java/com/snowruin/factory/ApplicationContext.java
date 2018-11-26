package com.snowruin.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;
import com.snowruin.bean.ChildBeanDefinition;
import com.snowruin.bean.GenericBeanDefinition;
import com.snowruin.rules.IOCRules;
import com.snowruin.util.GetMethodName;
import com.snowruin.xml.FileSystemXmlApplicationContext;

import lombok.extern.slf4j.Slf4j;

/**
 * application 上下文
 * @author zxm
 * @date 2018-11-23
 */
@Slf4j
public class ApplicationContext extends FileSystemXmlApplicationContext implements BeanFactory {

	Map<String, GenericBeanDefinition> map = null;
	
	public ApplicationContext(String contextConfigLocation) {
		map = super.getBeanDefinitionMap(contextConfigLocation);
	}
	
	
	/**
	 * 获取bean
	 */
	@Override
	public Object getBean(String beanId) {
		assert beanId == null : "beanId 不存在";
		Object object = null;
		Class<?> clazz = null;
		Set<Entry<String,GenericBeanDefinition>> entrySet = map.entrySet();
		// 判断容器中是否存在BeanId
		if(map.containsKey(beanId)) {
			// 如果存在，则遍历每一个bean
			for (Entry<String, GenericBeanDefinition> entry : entrySet) {
				if(beanId .equals(entry.getKey())) {
					// 获取容器中的genericBeanDefinition对象
					GenericBeanDefinition genericBeanDefinition = entry.getValue();
					String beanName = genericBeanDefinition.getClassName();
					
					// 获取该genericBeanDefinition的，子元素
					List<ChildBeanDefinition> childBeanDefinitions = genericBeanDefinition.getChildBeanDefinitions();
					try {
						clazz = Class.forName(beanName);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						log.info("{}没有找到",beanName);
						e.printStackTrace();
					}
					
					try {
						object = clazz.newInstance();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						log.info("实例化对象异常");
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// 遍历子元素集合
					for (ChildBeanDefinition childBeanDefinition : childBeanDefinitions) {
						// 如果xml 中的属性和IOCRules 中定义的set Rule属性一致，则使用set注入
						if(IOCRules.SET_INJECTION.getType() .equals(childBeanDefinition.getChildrenType())) {
							setValue(clazz, childBeanDefinition, object);	
						}
						// 同理， 如果符合构造器注入的规则，则使用构造器注入
						else if (IOCRules.CONSTRUCTOR_INJECTION.getType() .equals(childBeanDefinition.getChildrenType())) {
							List<ChildBeanDefinition> constructorChildBeanDefinition = Lists.newArrayList();
							// 构造器注入时需要注入所有属性
							for (ChildBeanDefinition childBeanDefinition2 : childBeanDefinitions) {
								if(IOCRules.CONSTRUCTOR_INJECTION.getType() .equals(childBeanDefinition2.getChildrenType())) {
									constructorChildBeanDefinition.add(childBeanDefinition2);
								}
							}
							object = consValue(clazz,constructorChildBeanDefinition,object);
							break;
						}
					}
				}
			}
		}else {
			throw new RuntimeException("容器中不存在该bean");
		}
		return object;
	}
	
	/**
	 * 构造器注入
	 * @param clazz
	 * @param childBeanDefinitions
	 * @param object
	 * @return
	 */
	private Object consValue(Class<?> clazz , List<ChildBeanDefinition> childBeanDefinitions,Object object) {
		
		Constructor<?> constructor = null;
		Field[] fields = clazz.getDeclaredFields();
		@SuppressWarnings("rawtypes")
		Class [] classArray =  new Class[fields.length];
		for (int i = 0; i< fields.length; i++) {
			if("String" .equals(fields[i].getType().getSimpleName())) {
				classArray[i] = String.class;
			}else if("Integer" .equals(fields[i].getType().getSimpleName())) {
				classArray[i] = Integer.class;
			}else if ("int" .equals(fields[i].getType().getSimpleName())) {
				classArray[i] = int.class;
			}
		}		
				
		try {
			constructor = clazz.getConstructor(classArray);
			try {
				object = constructor.newInstance(childBeanDefinitions.get(0).getAttributeOne(),childBeanDefinitions.get(1).getAttributeOne());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			log.error("没有找到，{}",constructor);
			e.printStackTrace();
		} 
		return object;
	}
	
	
	/**
	 * set 注入
	 * @param clazz
	 * @param childBeanDefinition
	 * @param object
	 */
	private void setValue(Class<?> clazz , ChildBeanDefinition childBeanDefinition, Object object) {
		Field field = null;
		Method[] methods = null;
		
		// 只支持 int Interge String 类型的属性注入
		String type = null;
		String propertyName = childBeanDefinition.getAttributeOne();
		String propertyVlaue = childBeanDefinition.getAttributeTwo();
		
		// 拼接成set 方法名
		String methodName = GetMethodName.getSetMethodNameByField(propertyName);
		
		try {
			field = clazz.getDeclaredField(propertyName);
			type = field.getType().getSimpleName();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			methods = clazz.getMethods();
			
			for (Method method : methods) {
				if(methodName.equals(method.getName())) {
					try {
						// 判断属性是什么类
						if("String" .equals(type)) {
							method.invoke(object, propertyVlaue);
						}else if ("Integer" .equals(type)) {
							Integer propertyVlaue2 = Integer.valueOf(propertyVlaue);
							method.invoke(object, propertyVlaue2);
						}else if ("int" .equals(type)) {
							 Integer propertyValue2 = Integer.valueOf(propertyVlaue);
							 method.invoke(object, propertyValue2);
						}else {
						    log.error("暂时不支持该属性,{}", type);
						}
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						log.info("暂时不支持该属性,{}",type);
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("方法注入异常");
			e.printStackTrace();
		}
	}
}
