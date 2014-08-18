package com.llq.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

public class HelloWorld {

	//搭建环境（创建核心ProcessEngine，检测环境搭建是否正确）
	@Test
	public void test1(){
		
		//创建流程引擎配置对象
		ProcessEngineConfiguration cfg=ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
	
		//数据库相关配置
		cfg.setJdbcDriver("com.mysql.jdbc.Driver");
		cfg.setJdbcUrl("jdbc:mysql:///activiti?createDatabaseIfNotExist=true");
		cfg.setJdbcUsername("root");
		cfg.setJdbcPassword("root");
		
		//设置数据库建表策略
		cfg.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		
		//使用配置对象创建ProcessEngine
		ProcessEngine processEngine=cfg.buildProcessEngine();
		
	}
	
	
}
