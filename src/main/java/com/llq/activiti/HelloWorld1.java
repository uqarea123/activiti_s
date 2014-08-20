package com.llq.activiti;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class HelloWorld1 {

	
	//搭建环境（创建核心ProcessEngine，检测环境搭建是否正确）
	//最简单的方式获取ProcessEngine实例，自动去加载classpath下名为activiti.cfg.xml配置文件
	//ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
	
	/**
	 * 
	 * 使用activiti相关API步骤：
	 *   1.创建核心流程引擎对象ProcessEngine
	 *   2.使用ProcessEngine获取需要的服务对象
	 *   3.使用服务对象相关方法完成操作
	 *   
	 * @throws Exception
	 */
	
	//1.发布流程规则
	@Test
	public void deployFlow() throws Exception {
		
		//1.创建核心流程引擎对象ProcessEngine
		ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
		//2.使用ProcessEngine获取需要的服务对象
		RepositoryService repositoryService=processEngine.getRepositoryService();
		//3.使用服务对象相关方法完成操作
		//创建发布配置对象
		DeploymentBuilder builder=repositoryService.createDeployment();
		//指定发布相关的文件
		builder.addClasspathResource("HelloWorld.bpmn").addClasspathResource("HelloWorld.png");
		//发布流程
		builder.deploy();
		
	}
	
	//2.启动流程实例
	@Test
	public void startFlow() throws Exception {
		
		//1.创建核心流程引擎对象ProcessEngine
		ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
		//创建运行时服务对象
		RuntimeService runtimeService=processEngine.getRuntimeService();
		//使用服务
		runtimeService.startProcessInstanceByKey("myProcess");
		
	}
	//3.查看任务
	@Test
	public void queryTask() throws Exception {
		
		//1.创建核心流程引擎对象ProcessEngine
		ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
		//2.得到任务服务
		TaskService taskService=processEngine.getTaskService();
		//3.使用服务
		String userId="老板";
		//查询用户的私有任务
		List<Task> tasks=taskService.createTaskQuery()//创建查询对象
		  .taskAssignee(userId)//添加过滤条件
		  .list();
		
		for(Task task:tasks){
			
			System.out.println("Id:"+task.getId()+",name:"+task.getName()+",assignee:"+task.getAssignee()+",createTime:"+task.getCreateTime());
		}

		
	}
	//4.办理任务
	@Test
	public void completeTask() throws Exception {
		//1.创建核心流程引擎对象ProcessEngine
		ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
		//2.得到任务服务
		TaskService taskService=processEngine.getTaskService();
		//3.使用任务服务完成任务（提交任务）
		String taskId="405";
		taskService.complete(taskId);
		
	}
}
