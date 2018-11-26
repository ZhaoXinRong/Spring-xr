package com.snowruin.web.bind;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.snowruin.annotation.XModelAttribute;
import com.snowruin.util.AnnotationUtils;
import com.snowruin.util.ConvertUtils;
import com.snowruin.util.GetMethodName;
import com.snowruin.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * BindByModelAttribute
 *  参数注解是MyModelAttribute时，绑定数据的类
 * @author zxm
 * @date 2018-11-23
 */
@Slf4j
public class BindByModelAttribute  implements BindParam{
	
	public Object bindingParamter(Parameter parameter, HttpServletRequest request,HttpServletResponse response)
			throws IllegalAccessException, InstantiationException, NoSuchMethodException {
		
		String simpleName = parameter.getType().getSimpleName();
		if("HttpServletRequest".equals(simpleName)) {
			return request;
		}
		
		if("HttpServletResponse".equals(simpleName)) {
			return response;
		}
		
		XModelAttribute modelAttribute = parameter.getAnnotation(XModelAttribute.class);
		Class<?> type = parameter.getType();
		if(!AnnotationUtils.isEmpty(modelAttribute)) {
			if(!type.getSimpleName().equals(modelAttribute.value())) {
				throw new RuntimeException("实体参数绑定异常，请检查");
			}
		}
		
		Field[] fields = type.getDeclaredFields();
		Object object = type.newInstance();
		
		for (Field field : fields) {
			String parameter1= request.getParameter(field.getName());
			if(StringUtils.isNotEmpty(parameter1)){
				Object setObject = ConvertUtils.convert(field.getType().getSimpleName(), parameter1);
				String methodName = GetMethodName.getSetMethodNameByField(field.getName());
				Method method = type.getMethod(methodName, field.getType());
				try {
					method.invoke(setObject, setObject);
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					log.error("{}属性赋值异常",field.getName());
					e.printStackTrace();
				}
			}
		}
		return object;
	}

}
