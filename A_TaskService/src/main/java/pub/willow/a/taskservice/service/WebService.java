package pub.willow.a.taskservice.service;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import pub.willow.a.baseservice.beans.WebServiceBean;
import pub.willow.a.spider.ws.WSICrawlTaskService;

public class WebService{
	
	/**
	 * 根据serviceName获得WebService工厂
	 * @param monitorService  - 获得monitorService的地址
	 */
	public JaxWsProxyFactoryBean  findWebServiceFactory(WebServiceBean webServiceBean){
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		String serviceName = webServiceBean.getServiceName();
		
		if(serviceName==null || serviceName.trim().equals("")){
			return null;
		}
		String addr = "";
			factory.setServiceClass(WSICrawlTaskService.class);
			addr = "http://"+webServiceBean.getIp()+":"+webServiceBean.getPort()+"/"+webServiceBean.getProjectName()+"/"+webServiceBean.getFilterName()+
			"/"+webServiceBean.getServiceName();
		System.out.println(addr);
		factory.setAddress(addr);
		return factory;
	}
}
