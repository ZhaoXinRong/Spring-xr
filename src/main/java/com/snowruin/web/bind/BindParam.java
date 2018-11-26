package com.snowruin.web.bind;

import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * BindParam
 * @author zxm
 * @date 2018-11-23
 */
public interface BindParam {

	Object bindingParamter(Parameter parameter, HttpServletRequest request,HttpServletResponse response) throws IllegalAccessException, InstantiationException, NoSuchMethodException;
}
