package com.llq.activiti;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.FileUtils;
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
	/**
	 * 2.查看流程定义信息
	 *     在项目中，我们主要和processDefinition打交道
	 *     ProcessDefinition : 流程规则的整体信息（流程ID和版本）
	 *         ID   {key}:{version}:{随机数}
	 *         NAME : 对应流程文件上process节点的name属性
	 *         KEY  : 对应流程文件上process节点的id属性
	 *         VERSION : 发布时，查找当前系统中对应key的最高版本，如果找到了，在最高版本上”加1“，否则为1
	 *     ActivityImpl      : 描述当前规则下每一个活动相关信息
	 * 
	 * 
	 * @throws Exception
	 */
	@Test
	public void queryProcessDefinition() throws Exception {
		
		//使用服务对象创建需要的查询对象
		ProcessDefinitionQuery definitionQuery=repositoryService.createProcessDefinitionQuery();
		//添加查询参数
		definitionQuery
		    .processDefinitionKey("LeaveFlow")
//		    .processDefinitionName(processDefinitionName)
//		                分页条件
//		    .listPage(firstResult, maxResult)
		    .orderByProcessDefinitionVersion().desc();
		
		//调用查询方法得到结果
		List<ProcessDefinition> pds=definitionQuery.list();
		//遍历结果
		for(ProcessDefinition pd:pds){
			System.out.println("id:"+pd.getId()+",name:"+pd.getName()+",key:"+pd.getKey()+",version:"+pd.getVersion());
			 ProcessDefinitionImpl pdImpl=(ProcessDefinitionImpl) repositoryService.getProcessDefinition(pd.getId());
			 System.out.println(pdImpl.getActivities());
			 break;
		}
		
	}
	
	//3.删除流程规则
	@Test
	public void deleteProcess() throws Exception {
		String deploymentId="1";
		//普通删除，如果当前规则下有正在执行的流程，则删除失败（保护）
//		repositoryService.deleteDeployment(deploymentId);
		//级联删除，相对比较暴力，会删除正在执行的流程信息和当前规则的所有历史，（一般不推荐开发给普通用户使用）
		repositoryService.deleteDeployment(deploymentId, true);
	}
	//4.查看流程附件（查看流程图片）
	@Test
	public void queryImage() throws Exception {
		//获取发布ID
		String deploymentId="801";
		//查找这次发布的所有资源文件名称
		List<String> names=repositoryService.getDeploymentResourceNames(deploymentId);
		String imageName=null;
		for(String name:names){
			//指定规则，获取需要的文件名称
			if(name.indexOf(".png")>=0){
				imageName=name;
			}
		}
		System.out.println(imageName);
		if(imageName!=null){
			//通过文件名称去数据库中查询对应的输入流
			InputStream inputStream=repositoryService.getResourceAsStream(deploymentId, imageName);
			//把流写到本地文件中
			File file=new File("d:/xx.png");
			FileUtils.copyInputStreamToFile(inputStream, file);
			
		}
	}
	
}
