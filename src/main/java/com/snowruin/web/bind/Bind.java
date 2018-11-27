package com.snowruin.web.bind;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.snowruin.annotation.XModelAttribute;
import com.snowruin.annotation.XRequestParam;
import com.snowruin.util.AnnotationUtils;
import com.snowruin.util.IsBasicTypeUtils;
import com.snowruin.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * bind
 * @author zxm
 * @date 2018-11-23
 */
@Slf4j
public class Bind {
	
	public static List<Object> bingMethodParamters(Map<String, Method> bindRequestMapping,HttpServletRequest request,HttpServletResponse response) {
		List<Object> resultParameters = Lists.newArrayList();
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String url = "";
		if(StringUtils.isNotEmpty(requestURI)) {
			url = requestURI.replace(contextPath, "");
		}
		log.info("请求的路径是：{}",url);

		
		if(StringUtils.isNotEmpty(url)) {
			Method method = bindRequestMapping.get(url);
			if(Objects.isNull(method)) {
				return  Lists.newArrayList();
			}
			
			Parameter[] parameters = method.getParameters();
			if(parameters == null || parameters.length ==0) {
				log.info("暂无请求参数");
			}else {
				log.info("请求参数是：{}",parameters);
				try {
					for (Parameter parameter : parameters) {
						Object bindEachParam = bindEachParam = bindEachParam(parameter, request,response);
						resultParameters.add(bindEachParam);
					}
					return resultParameters;
				} catch (IllegalAccessException | InstantiationException | NoSuchMethodException e) {
					// TODO Auto-generated catch block
					log.info("参数绑定出现异常：{}",e.getMessage());
					e.printStackTrace();
				}
			}
			return Lists.newArrayList();
		}
		return resultParameters;
	}
	
	private static Object bindEachParam(Parameter parameter,HttpServletRequest request,HttpServletResponse response) throws IllegalAccessException, InstantiationException, NoSuchMethodException {
		if(!AnnotationUtils.isEmpty(XRequestParam.class)) {
			BindParam bind = new BindByRequstParam();
			Object bindingParamter = bind.bindingParamter(parameter, request,response);
			return bindingParamter;
		}else if(!AnnotationUtils.isEmpty(parameter.getAnnotation(XModelAttribute.class))) {
			BindParam bind = new BindByModelAttribute();
			Object bindingParamter = bind.bindingParamter(parameter, request,response);
			return bindingParamter;
		}else if(parameter.getAnnotations() == null || parameter.getAnnotations() .length == 0) {
			boolean basicType = IsBasicTypeUtils.isBasicType(parameter.getType().getSimpleName());
			if(basicType) {
				BindParam bindParam =  new BindByRequstParam();
				Object bindingParamter = bindParam.bindingParamter(parameter, request,response);
				return bindingParamter;
			}else {
				BindParam bindParam  = new BindByModelAttribute();
				Object bindingParamter = bindParam.bindingParamter(parameter, request,response);
				return bindingParamter;
			}
		}
		return null;
	}

}
