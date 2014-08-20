package com.llq.activiti;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.junit.Test;


/**
 * 使用API的步骤：
 *   1) 创建流程引擎ProcessEngine
 *   2) 获取相关服务对象实例
 *   3) 使用服务对象相关方法完成流程操作
 * @author Administrator
 *
 */
public class ProcessDefinitionTest {

	//创建流程引擎ProcessEngine
	private ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
	//创建仓库服务对象
	private RepositoryService repositoryService=processEngine.getRepositoryService();
	
	
	/**
	 * 1.发布规则
	 *     成功发布后，会在数据库3张表中添加数据：
	 *       act_ge_bytearray ：添加2条数据（“流程规则文件”和“流程图片”）
	 *       act_re_deployment ：添加1条数据（定义了规则的“显示列名”和“发布时间”）
	 *       act_re_procdef ：添加一条数据（流程规则相关信息[流程定义]）
	 *       
	 *       ID={key}:{version}:{随机数}
	 * @throws Exception
	 */
	@Test
	public void deployProcess() throws Exception {
		//创建一个发布配置对象
		DeploymentBuilder deploymentBuilder=repositoryService.createDeployment();
		//添加发布的资源文件（“流程规则文件”和“流程图片”）
		InputStream inputStream=Thread.currentThread().getContextClassLoader().getResourceAsStream("LeaveFlow.bpmn");
		InputStream image=Thread.currentThread().getContextClassLoader().getResourceAsStream("LeaveFlow.png");
		deploymentBuilder
		    .name("请假流程")
		    .addInputStream("LeaveFlow.bpmn", inputStream)
		    .addInputStream("LeaveFlow.png", image);
		//完成发布（使用deploy方法发布流程）
		deploymentBuilder.deploy();
		
	}
	
	//使用ZIP方式发布流程
	/**
	 * this.getClass().getResourceAsStream("Leave.Zip");从当前类所在包下加载指定名称文件的输入流
	 * this.getClass().getResourceAsStream("/Leave.Zip");从ClassPath根目录下加载指定名称文件的输入流
	 * this.getClass().getClassLoader().getResourceAsStream("Leave.Zip");从ClassPath根目录下加载指定名称文件的输入流
	 * Thread.currentThread().getContextClassLoader().getResourceAsStream("Leave.Zip");从ClassPath根目录下加载指定名称文件的输入流
	 * 
	 * 
	 * 如果以ZIP文件方式发布流程，那么在发布成功后，Activiti框架会自动解压ZIP包中的文件，一次添加到act_ge_bytearray表中
	 */
	@Test
	public void deployProcessByZIP() throws Exception {
		
		//创建一个发布配置对象
		DeploymentBuilder deploymentBuilder=repositoryService.createDeployment();
		//添加发布的资源文件
		InputStream in=this.getClass().getResourceAsStream("/Leave.Zip");
		ZipInputStream zipInputStream=new ZipInputStream(in);
		deploymentBuilder.addZipInputStream(zipInputStream);
		
		//调用deploy方法发布流程
		deploymentBuilder.deploy();
	}
}
