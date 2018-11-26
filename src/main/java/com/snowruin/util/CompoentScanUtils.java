package com.snowruin.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.snowruin.annotation.XAutowired;
import com.snowruin.annotation.XController;
import com.snowruin.annotation.XReqository;
import com.snowruin.annotation.XService;

import lombok.extern.slf4j.Slf4j;

/**
 * 扫描包工具类
 * @author zxm
 * @date 2018-11-22
 */
@Slf4j
public class CompoentScanUtils {

	private final static List<String>  classNameList = Lists.newArrayList();
	private final static List<String> compoentList = Lists.newArrayList();
	private final static Map<String, String> interfaceAndImplMap = Maps.newConcurrentMap();
	
	
	/**
	 * 扫描指定包下的所有类名
	 * @param packageName 包名
	 * @return 类名集合
	 */
	public static List<String> getClassName(String packageName){
		Enumeration<URL> urls = null;
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		String newPackageName = packageName.replace(".", "/");
		try {
			urls = contextClassLoader.getResources(newPackageName);
			while(urls.hasMoreElements()) {
				URL url = urls.nextElement();
				File packageFile = new File(url.getPath());
				File[] files = packageFile.listFiles();
				if(files == null) {
					break;
				}
				for (File file : files) {
					// 如果是class 则添加到list中返回
					if (file.getName().endsWith(".class")) {
				          String templeName = (packageName.replace("/", ".") + "." + file.getName());
				          String newTempleName = templeName.substring(0, templeName.lastIndexOf("."));
				          classNameList.add(newTempleName);
					}else {
						// 如果是package ， 则继续遍历
						String nextPackageName = newPackageName + "." + file.getName();
						getClassName(nextPackageName);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return classNameList;
	}
	
	/**
	 * 返回 有注解的实例化顺序的链表
	 * @param packageName
	 * @return
	 */
	public static List<String> getCompomentList(String packageName){
		List<String> classNames = getClassName(packageName);
		// 将扫描的接口和其实现类，使用map对象上，模仿Spring接口注入，负责的原因是因为Java  不支持从接口获取实现类
		makeInterfaceAndImplMap(classNames);
		for (String className : classNames) {
			// 实例化每个类
			try {
				resolveComponent(className);
			} catch (ClassNotFoundException e) {
				log.info("扫描注解的时候，{}没有找到",className);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return compoentList;
	}
	
	/**
	 *  getCompomentList() 递归调用的字方法
	 * @param className
	 * @throws ClassNotFoundException
	 */
	public static void resolveComponent(String className) throws ClassNotFoundException {
		Class<?> clazz = Class.forName(className);
		addNewAnnotationClass(XController.class,clazz);
		addNewAnnotationClass(XService.class,clazz);
		addNewAnnotationClass(XReqository.class,clazz);
	}
	
	/**
	 * 
	 * @param annotationClass
	 * @param clazz
	 * @throws ClassNotFoundException
	 */
	public static <A extends Annotation> void addNewAnnotationClass(Class<A> annotationClass,Class<?> clazz) throws ClassNotFoundException {
		 // 如果类上有注解，判断属性上有没有注解
		if(!AnnotationUtils.isEmpty(annotationClass)) {
			Field[] fields = clazz.getDeclaredFields();
			
			if(fields == null || fields.length ==0) {
				ListUtils.add(compoentList, clazz.getName());
			}else {
				
				// 跳出递归的语句，也就是最底层的类，如果所有属性没有@XAutowired注解，则注入到链表中
				if(isEmptyAutowired(fields)) {
					ListUtils.add(compoentList, clazz.getName());
				}else {
					// 如果属性上有XAutowired，则继续递归
					for (Field field : fields) {
						// 递归具体的查找到哪个属性上有@XAutowired
						if(field.getAnnotation(XAutowired.class) != null) {
							// 如果有则根据类名查找类，然后去对应勒种的递归过程
							String newFieldName = field.getType().getName();
							// 如果是接口，则用其实现类注入
							if (Class.forName(newFieldName).isInterface()) {
								String nextName = convertInterfaceToImpl(newFieldName);
								if(!compoentList.contains(nextName)) {
									resolveComponent(nextName);
								}
							}else {
								resolveComponent(newFieldName);
							}
						}
					}
					ListUtils.add(compoentList, clazz.getName());
				}
				
			}
			
		}
		
	}
	
	/**
	 * 接口转换为实体类
	 * @param newFieldName
	 * @return
	 */
	private static String convertInterfaceToImpl(String newFieldName) {
		Set<Entry<String,String>> entries = interfaceAndImplMap.entrySet();
		for (Entry<String, String> entry : entries) {
			if(newFieldName.equals(entry.getKey()) && !entry.getValue().equals("proxy")) {
				return entry.getValue();
			}else if(newFieldName.equals(entry.getKey()) && entry.getValue().equals("proxy")) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	/**
	 * 判断属性中有没有注解
	 * @param fields
	 * @return
	 */
	private static boolean isEmptyAutowired(Field[] fields) {
		for (Field file : fields) {
			if(!AnnotationUtils.isEmpty(file.getAnnotation(XAutowired.class))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 组装接口和实现类
	 * @param classNames
	 * @return
	 */
	public static Map<String, String > makeInterfaceAndImplMap(List<String> classNames){
		Class<?> aClass = null;
		// interfaceNameList是所有接口类名的链表
		List<String> interfaceNameList = Lists.newArrayList();
		// 这个链表保存的是有事先类的接口的链表名，默认没有实现类的接口即为需要动态注入的链表
		List<String> interfaceExist = Lists.newArrayList();
		
		// 循环类名，将类名注入到链表中
		for (String className : classNames) {
			try {
				aClass = Class.forName(className);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(aClass.isInterface()) {
				interfaceNameList.add(className);
			}
		}
		
		for (String className : classNames) {
			Class<?> bClass = null;
			try {
				bClass = Class.forName(className);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Class<?>[] interfaces = bClass.getInterfaces();
			// 如果是接口的实现类
			if(interfaces != null && interfaces.length > 0) {
				for (String interfaceName : interfaceNameList) {
					for (Class<?> interfaceClass : interfaces) {
						// 如果既有接口，也有实现类，则组成map
						if(interfaceName.equals(interfaceClass.getName())) {
							interfaceAndImplMap.put(interfaceName, className);
						}
						
					}
				}
				
				
				
			}
		}
		// 需要动态代理注入的接口，在map中用value=proxy 来识别
		 interfaceNameList.removeAll(interfaceExist);
		 if (interfaceNameList != null && interfaceNameList.size() > 0) {
	            for (String spareInterfaceName :interfaceNameList) {
	                interfaceAndImplMap.put(spareInterfaceName, "proxy");
	            }
	            System.out.println("已经存在的" + interfaceNameList);
	     }
		return null;
	}
	
}
