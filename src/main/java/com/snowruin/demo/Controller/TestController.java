package com.snowruin.demo.Controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.snowruin.annotation.XAutowired;
import com.snowruin.annotation.XController;
import com.snowruin.annotation.XRequestMapping;
import com.snowruin.annotation.XRequestParam;
import com.snowruin.annotation.XResponseBody;
import com.snowruin.demo.service.TestService;
import com.snowruin.web.mvc.ModelAndView;
import com.snowruin.web.mvc.ModelMap;

@XController
public class TestController {

	@XAutowired
	private TestService testService;
	
	@XRequestMapping("/hello")
	@XResponseBody
	public List<String> sayHello(@XRequestParam("msg")String  msg) {
		System.out.println(testService.sayHello());
		List<String  > result = Lists.newArrayList();
		result.add("sdfsdf");
		result.add("111");
		result.add("33");
		result.add("444");
		System.out.println(msg);
		return result;
	}
	
	@XRequestMapping("/handler")
	public ModelAndView handler(@XRequestParam("msg")String msg,HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		return new ModelAndView("view").setModelMap((ModelMap)new ModelMap().addAttribute("message", msg));
	}
	
	
	@XRequestMapping("/test")
	public void handler(HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.sendRedirect("index.jsp");
	}
	
}
