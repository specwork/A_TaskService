package pub.willow.a.taskservice.service.task;

import javax.annotation.Resource;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.common.gzip.GZIPInInterceptor;
import org.apache.cxf.transport.common.gzip.GZIPOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import pub.willow.a.baseservice.beans.TaskBean;
import pub.willow.a.baseservice.beans.WebServiceBean;
import pub.willow.a.spider.ws.WSICrawlTaskService;
import pub.willow.a.taskservice.service.WebService;

/**
 * 任务分配模块
 * 
 * @author albert.zhang
 * 
 */
public class DistributeTaskService {

	@Resource(name="webService")
	private WebService webService;
	
	/**
	 * 根据任务信息生产爬虫service,进行任务执行
	 * 
	 * @param taskArray
	 * @throws Exception 
	 */
	public TaskBean distributeTask(TaskBean taskBean,String spider)  {
		try{
			// 遍历任务，进行匹配
			if(taskBean != null) {
				WebServiceBean webServiceBean = new WebServiceBean();
				webServiceBean.setIp(spider);
				webServiceBean.setPort(8080);
				webServiceBean.setFilterName("service");
				webServiceBean.setProjectName("A_Spider");
				webServiceBean.setServiceName("wsCrawlTaskService?wsdl");
				JaxWsProxyFactoryBean factory=  webService.findWebServiceFactory(webServiceBean);
				//设置cxf压缩模式
				factory.getInInterceptors().add(new GZIPInInterceptor());  
				factory.getOutInterceptors().add(new GZIPOutInterceptor());
				
				WSICrawlTaskService handleTaskForCrawl = (WSICrawlTaskService) factory.create();
				
				//设置客户端的配置信息，超时等.
				   Client proxy = ClientProxy.getClient(handleTaskForCrawl);
				   HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
				   HTTPClientPolicy policy = new HTTPClientPolicy();
				   policy.setConnectionTimeout(600000); //连接超时时间
				   policy.setReceiveTimeout(600000);//请求超时时间.
				   policy.setAllowChunking(true);
				   conduit.setClient(policy);
				
				System.out.println("发送任务至爬虫，爬虫的信息为："+spider);
				taskBean = handleTaskForCrawl.crawlTask(taskBean);
				return taskBean;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
}
