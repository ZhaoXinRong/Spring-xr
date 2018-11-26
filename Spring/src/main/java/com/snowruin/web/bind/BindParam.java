package com.snowruin.web.bind;

import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;

/**
 * BindParam
 * @author zxm
 * @date 2018-11-23
 */
public interface BindParam {

	Object bindingParamter(Parameter parameter, HttpServletRequest request) throws IllegalAccessException, InstantiationException, NoSuchMethodException;
}
