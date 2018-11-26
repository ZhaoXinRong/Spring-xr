package com.snowruin.web.mvc;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * model and view
 * @author zxm
 * @date 2018-11-23
 */
@Getter
@Setter
@Accessors(chain = true)
public class ModelAndView  {
	
	private String view;
	
	private ModelMap modelMap;
	
	public ModelAndView(String view){
		this.view = view;
	}
}
