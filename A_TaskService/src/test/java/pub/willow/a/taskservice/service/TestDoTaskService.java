package pub.willow.a.taskservice.service;

import javax.annotation.Resource;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import pub.willow.a.taskservice.service.task.DoTaskService;

public class TestDoTaskService extends AbstractDependencyInjectionSpringContextTests {
	
	@Override
	protected String[] getConfigLocations() {
		return new String[] { 
				"applicationContext-init.xml",
				"applicationContext-dao.xml",
				"applicationContext-service.xml",
				"applicationContext-redis.xml"
//				"applicationContext-*.xml"
	  			  };
	}
	
	@Resource(name="doTaskService")
	public DoTaskService doTaskService;
	
	
	public void testDoTaskService(){
		doTaskService.doTask();
	}
}
