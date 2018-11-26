package com.snowruin.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 存放 xml 的bean 
 * @author zxm
 * @date 2018-11-22
 */
@Getter
@Setter
@Accessors(chain = true)
public class GenericBeanDefinition {

	/**
	 * 对应xml 文件中的class
	 */
	private String className;
	
	/**
	 * bean 里面的property对应
	 */
	private List<ChildBeanDefinition> childBeanDefinitions;
	
}
