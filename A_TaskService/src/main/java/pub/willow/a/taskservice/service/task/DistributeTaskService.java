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
import pub.willow.a.taskservice.service.WebService;
import pub.willow.simplespider.beans.SimpleSpiderBean;
import pub.willow.simplespider.ws.WSSpider;

/**
 * 任务分配模块
 * 
 * @author albert.zhang
 * 
 */
public class DistributeTaskService {
	
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
				JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
				factory.setServiceClass(WSSpider.class);
				String addr = "http://"+spider+":8080/SimpleSpider/service/wsSpider?wsdl";
				System.out.println(addr);
				factory.setAddress(addr);
				//设置cxf压缩模式
				factory.getInInterceptors().add(new GZIPInInterceptor());  
				factory.getOutInterceptors().add(new GZIPOutInterceptor());
				
				WSSpider handleTaskForCrawl = (WSSpider) factory.create();
				
				//设置客户端的配置信息，超时等.
				   Client proxy = ClientProxy.getClient(handleTaskForCrawl);
				   HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
				   HTTPClientPolicy policy = new HTTPClientPolicy();
				   policy.setConnectionTimeout(600000); //连接超时时间
				   policy.setReceiveTimeout(600000);//请求超时时间.
				   policy.setAllowChunking(true);
				   conduit.setClient(policy);
				
				System.out.println("发送任务至爬虫，爬虫的信息为："+spider);
				SimpleSpiderBean simpleSpiderBean = new SimpleSpiderBean();;
				simpleSpiderBean = handleTaskForCrawl.spiderHtml(simpleSpiderBean );
				return taskBean;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
}
