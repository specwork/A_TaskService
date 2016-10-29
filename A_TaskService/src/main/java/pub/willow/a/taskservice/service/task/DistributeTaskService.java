package pub.willow.a.taskservice.service.task;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.common.gzip.GZIPInInterceptor;
import org.apache.cxf.transport.common.gzip.GZIPOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import pub.willow.a.taskservice.beans.TaskBean;
import pub.willow.simplespider.beans.SimpleSpiderBean;
import pub.willow.simplespider.ws.WSSpider;

public class DistributeTaskService {

	public TaskBean distributeTask(TaskBean taskBean, String spider) {
		try {
			// 遍历任务，进行匹配
			if (taskBean != null) {
				JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
				factory.setServiceClass(WSSpider.class);
				String addr = "http://" + spider + ":8080/SimpleSpider/service/wsSpider?wsdl";
				System.out.println(addr);
				factory.setAddress(addr);
				// 设置cxf压缩模式
				factory.getInInterceptors().add(new GZIPInInterceptor());
				factory.getOutInterceptors().add(new GZIPOutInterceptor());

				WSSpider handleTaskForCrawl = (WSSpider) factory.create();

				// 设置客户端的配置信息，超时等.
				Client proxy = ClientProxy.getClient(handleTaskForCrawl);
				HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
				HTTPClientPolicy policy = new HTTPClientPolicy();
				policy.setConnectionTimeout(600000); // 连接超时时间
				policy.setReceiveTimeout(600000);// 请求超时时间.
				policy.setAllowChunking(true);
				conduit.setClient(policy);

				System.out.println("send task to spider :：" + spider);
				SimpleSpiderBean simpleSpiderBean = new SimpleSpiderBean();
				simpleSpiderBean.setTaskId(taskBean.getId());
				simpleSpiderBean.setUrl(taskBean.getUrl());
				simpleSpiderBean.setCharset(taskBean.getCharset());
				simpleSpiderBean.loadDefaultHeaders();
				simpleSpiderBean = handleTaskForCrawl.spiderHtml(simpleSpiderBean);
				taskBean.setSource(simpleSpiderBean.getSource());
				return taskBean;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
