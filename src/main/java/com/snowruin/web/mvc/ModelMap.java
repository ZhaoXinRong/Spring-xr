package com.snowruin.web.mvc;

import java.util.LinkedHashMap;

/**
 * modelMap 
 * @author zxm
 * @date 2018-11-23
 */
public class ModelMap extends LinkedHashMap<String, Object> implements Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 549346871210344825L;

	
	
	public Model addAttribute(String attributeName, Object attributeValue) {
		// TODO Auto-generated method stub
		this.put(attributeName, attributeValue);
		return this;
	}

}
