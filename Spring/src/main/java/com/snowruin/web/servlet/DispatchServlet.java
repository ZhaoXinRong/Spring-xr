package com.snowruin.web.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.snowruin.annotation.XResponseBody;
import com.snowruin.constans.Constants;
import com.snowruin.factory.InitBean;
import com.snowruin.util.ListUtils;
import com.snowruin.util.StringUtils;
import com.snowruin.web.bind.Bind;
import com.snowruin.web.bind.BindRequestAndModel;
import com.snowruin.web.mvc.Handler;
import com.snowruin.web.mvc.ModelAndView;
import com.snowruin.web.mvc.ViewResolve;

import lombok.extern.slf4j.Slf4j;


/**
 * 请求分发处理器
 * @author zxm
 * @date 2018-11-23
 */
@Slf4j
@WebServlet(name="dispatchServlet")
public class DispatchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public Map<String, String> requestMaps = new ConcurrentHashMap<>();

	
	@Override
	public void init() throws ServletException {
		InitBean initBean = new InitBean();
		initBean.initBeans();
		// 根据bean容器中注册的bean获得HandlerMapping
		Map<String, Method> bindRequestMapping = Handler.bindRequestMapping(initBean.beanContainerMap);
		ServletContext servletContext = this.getServletContext();
		servletContext.setAttribute("beanContainer", initBean.beanContainerMap);
		servletContext.setAttribute("bindRequestMapping", bindRequestMapping);
	}
	
	
    /**
     * Default constructor. 
     */
    public DispatchServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			doDispatch(request, response);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	/**
	 * 接收请求后转发到相应的方法
	 * @param request
	 * @param response
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws UnsupportedEncodingException 
	 */
	@SuppressWarnings({ "unchecked" })
	private void doDispatch(HttpServletRequest request,HttpServletResponse response) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		ServletContext servletContext = this.getServletContext();
		// 获取扫描Controller 注解后url 和方法绑定的Mapping ，也就是handlerMapping
		Map<String, Method> bindRequestMapping = (Map<String, Method>) servletContext.getAttribute("bindRequestMapping");
	 
 		List<Object> resultParameters = Bind.bingMethodParamters(bindRequestMapping, request);
		
		// 获取实例化的bean容器
		Map<String, Object> beanContainer =  (Map<String, Object>) servletContext.getAttribute("beanContainer");
		String requestURI = request.getRequestURI();
		
		String contextPath = request.getContextPath();
		
		String url = "";
		if(StringUtils.isNotEmpty(requestURI)) {
			url = requestURI.replace(contextPath, "");
		}
		
//		if(StringUtils.isNotEmpty(Constants.requestMap.get(url))) {
//			url = Constants.requestMap.get(url);
//		}
		
		if(bindRequestMapping.containsKey(url)) {
			Method method = bindRequestMapping.get(url);
			Class<?> returnType = method.getReturnType();
			// 如果返回是modelAndView ,开始绑定
			if("ModelAndView" .equals(returnType.getSimpleName())) {
				Object object = beanContainer.get(method.getDeclaringClass().getName());
				// 获取springmvc.xml 中配置的视图解析器
				ViewResolve viewResolve = (ViewResolve) beanContainer.get("com.snowruin.web.mvc.ViewResolve");
				String prefix = viewResolve.getPrefix();
				String suffix = viewResolve.getSuffix();
				ModelAndView modelAndView =  (ModelAndView) method.invoke(object,  resultParameters.toArray());
				//将request和model中的数据绑定,也就是渲染视图
				BindRequestAndModel.bindRequestAndModel(modelAndView, request);
				String viewName = modelAndView.getView();
				// 返回的路径
				String path = prefix + viewName + suffix;
				
				try {
				//	Constants.requestMap.put(path, url);
					request.getRequestDispatcher(path).forward(request, response);
					return;
				} catch (ServletException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				Object object = beanContainer.get(method.getDeclaringClass().getName());
				Object result =  method.invoke(object,  resultParameters.toArray());
				
				if(method.isAnnotationPresent(XResponseBody.class)) {
					response.setContentType("application/json;charset=utf-8");
					
					try {
						response.getWriter().write(JSON.toJSONString(result));
						return ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				response.setContentType("text/html;charset=utf-8");
				try {
					response.getWriter().println(result);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
