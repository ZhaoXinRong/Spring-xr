package com.snowruin.factory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.snowruin.annotation.XAutowired;
import com.snowruin.bean.GenericBeanDefinition;
import com.snowruin.constans.Constants;
import com.snowruin.util.AnnotationUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author zxm
 * @date  2018-11-22
 */
@Slf4j
public class InitBean extends BeanDefinition {
	 
	// 初始化后的bean 容器 key为class 名， value为实例对象
	public Map<String,Object> beanContainerMap = Maps.newConcurrentMap();

	
	/**
	 * 初始化配置bean
	 */
	public void initBeans() {
		// 初始化xml 配置
		initXmlBeans(Constants.CONTEXT_CONFIG_LOCATION);
		initXmlBeans(Constants.SPRING_MVC_CONFIG_LOCATION);
		// 初始化扫描注解的配置
		initAutowiredBeans(Constants.CONTEXT_CONFIG_LOCATION);
	}
	
	
	/**
	 * 初始化xml 中bean 内容的方法
	 * @param contextConfigLocation
	 */
	public void  initXmlBeans(String contextConfigLocation) {
		ApplicationContext applicationContext = new ApplicationContext(contextConfigLocation);
		Class<?> clazz = null ;
		Map<String, GenericBeanDefinition> beanDefinitionMap = super.getBeanDefinitionMap(contextConfigLocation);
		Set<Entry<String,GenericBeanDefinition>> entrySet = beanDefinitionMap.entrySet();
		for (Entry<String, GenericBeanDefinition> entry : entrySet) {
			String beanId = entry.getKey();
			String className = entry.getValue().getClassName();
			try {
				clazz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				log.info("xml 中{} 无法实例化",className);
				e.printStackTrace();
			}
			beanContainerMap.put(className, clazz.cast(applicationContext.getBean(beanId)));
		}
	}
	
	public static void main(String[] args) {
		InitBean aBean = new InitBean();
		aBean.initXmlBeans(Constants.SPRING_MVC_CONFIG_LOCATION);
	}
	
	
	/**
	 * 将所有的componentList（也就是加注解的类）里面的bean实例化
	 * @param contextConfigLocation
	 */
	public void initAutowiredBeans(String contextConfigLocation) {
		List<String> componentList = super.getComponentList(contextConfigLocation);
		log.info("实例化的顺序"+componentList);
		// 扫描到有注解的类，初始化类的名单
		for (String className : componentList) {
			// 将每一个类初始化
			try {
				initClass(className);
			} catch (ClassNotFoundException e) {
				log.info("没有找到{}",className);
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 初始化每一个类的方法，初始化的时候由于spring 需要实现使用接口注入，所以比较麻烦
	 * 需要根据类名来判断是否有接口，然后再将接口名称和实现类对应上装配到容器中
	 * @param className
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void initClass(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> clazz = Class.forName(className);
		// 先判断这个类有没有接口，如果有接口吗，则将接口装配
		Class<?>[] interfaces = clazz.getInterfaces();
		//如果类是接口，注入的对象则是动态代理的对象
		if(clazz.isInterface()) {
			// 调用 mybatis 的 类
			
		}else if (interfaces == null || interfaces.length == 0) {
			noInterfaceInit(className, className);
		}else {
			for (Class<?> class1 : interfaces) {
				boolean existInContainer = isExistInContainer(className);
				if(existInContainer) {
					beanContainerMap.put(class1.getName(), clazz.newInstance());
				}else {
					// 如果容器中没有哦，则县实例化实现类，然后再装配到容器中
					noInterfaceInit(className, class1.getName());
				}
			}
		}
	}
	
	/**
	 * 在实例化该类之前先判断该是否在容器中
	 * @param className
	 * @return
	 */
	public boolean isExistInContainer(String className) {
		Set<Entry<String,Object>> entrySet = beanContainerMap.entrySet();
		if(entrySet != null) {
			for (Entry<String, Object> entry : entrySet) {
				if(entry.getKey().equals(className)) {
					return true;
				}else {
					return false;
				}
			}
		}
		return false;
	}
	
	
	public void noInterfaceInit(String className,String interfaceName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> clazz = Class.forName(className);
		// bean实例化
		log.info("实例化的名字: {}",clazz.getName());
		Object object = clazz.newInstance();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			// 如果属性上有XAutowired 注解，则先将属性注入进去
			if (!AnnotationUtils.isEmpty(field.getAnnotation(XAutowired.class))) {
				// 设置私有属性可见
				field.setAccessible(true);
				// 如果有注解，在实例化链表里面搜寻类名
				Set<Entry<String,Object>> entrySet = beanContainerMap.entrySet();
				for (Entry<String, Object> entry : entrySet) {
					String type = field.getType().getName();
					if(entry.getKey().equals(type)) {
						field.set(object, entry.getValue());
					}
				}
			}
		}
		beanContainerMap.put(interfaceName, object);
	}
	
	
}
