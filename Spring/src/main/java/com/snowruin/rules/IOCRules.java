package com.snowruin.rules;

import lombok.Getter;

/**
 * IOC 的配置规则
 * @author zxm
 * @date 2018-11-22
 */
@Getter
public enum IOCRules {
	
	/**
	 * bean 的配置规则
	 */
	BEAN_RULE("bean","id","class"),
	
	/**
	 * 扫描的配置规则
	 */
	SCAN_RULE("component-scan","base-package","null"),
	
	/**
	 * set 方式注入的规则
	 */
	SET_INJECTION("property","name","value"),
	
	/**
	 * 构造器注入的规则
	 */
	CONSTRUCTOR_INJECTION("constructor-arg","value","index");
	
	private String type;
	
	private String name;
	
	private String value;
	
	
	IOCRules(String property, String name, String value) {
        this.type  = property;
        this.name  = name;
        this.value = value;
    }
	
	   
}
