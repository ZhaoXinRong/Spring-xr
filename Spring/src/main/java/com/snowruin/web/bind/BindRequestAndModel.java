package com.snowruin.web.bind;

import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.snowruin.web.mvc.ModelAndView;
import com.snowruin.web.mvc.ModelMap;

/**
 * 
 * @author zxm
 * @date 2018-11-23
 */
public class BindRequestAndModel {
	
	public static void bindRequestAndModel(ModelAndView modelAndView,HttpServletRequest request) {
		ModelMap modelMap = modelAndView.getModelMap();
		if(!modelMap.isEmpty()) {
			Set<Entry<String,Object>> entrySet = modelMap.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				request.setAttribute(entry.getKey(), entry.getValue());
			}
		}
		
	}
	
	

}
