package com.snowruin.bean;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.Setter;

/**
 * 存放xml 中bean的property中的bean
 * @author zxm
 * @date 2018-11-22
 */
@Getter
@Setter
@Accessors(chain = true)
public class ChildBeanDefinition {

	/**
	 * property 或者 constrctor-arg 类型
	 */
	private String childrenType;
	
	/**
	 * 第一个值
	 */
	private String attributeOne;
	
	/**
	 * 第二个值
	 */
	private String attributeTwo;
}
