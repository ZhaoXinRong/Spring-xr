package com.snowruin.web.bind;

import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.snowruin.annotation.XRequestParam;


/**
 * 参数注解是XRequestParam 时。绑定数据的类
 * @author zxm
 * @date 2018-11-23
 */
public class BindByRequstParam implements BindParam {

	public Object bindingParamter(Parameter parameter, HttpServletRequest request,HttpServletResponse response)
			throws IllegalAccessException, InstantiationException, NoSuchMethodException {
		
		String simpleName = parameter.getType().getSimpleName();
		if("HttpServletRequest".equals(simpleName)) {
			return request;
		}
		
		if("HttpServletResponse".equals(simpleName)) {
			return response;
		}
		
		
		XRequestParam requestParam = parameter.getAnnotation(XRequestParam.class);
		String value =  requestParam.value();
		
		String parameterType = parameter.getType().getSimpleName();
		//测试，看看是什么，初步估计是空
        String parameter1 = request.getParameter(value);
        if (com.snowruin.util.StringUtils.isEmpty(parameter1)) {
            throw new RuntimeException("绑定参数异常");
        }
        
        if(parameterType.equals("String")) {
        	return parameter1;
        }else if(parameterType.equals("Integer") || parameterType.equals("int")) {
        		Integer valueOf = Integer.valueOf(parameter1);
        		return valueOf;
        }
		return null;
	}

}
