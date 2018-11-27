package com.snowruin.constans;


import com.snowruin.xml.FileSystemXmlApplicationContext;

/**
 * 保存配置文件的路径
 * @author zxm
 *
 */
public interface Constants {
	
    String PATH = FileSystemXmlApplicationContext.class.getResource("/").getPath();

	String CONTEXT_CONFIG_LOCATION = "application.xml";
	
	String SPRING_MVC_CONFIG_LOCATION = "spring-mvc.xml";
	
	String MYBATIS_CONFIG_LOCATION = "MyUserMapper.xml";
}
