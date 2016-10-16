package pub.willow.a.taskservice.service;

import javax.annotation.Resource;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import pub.willow.a.taskservice.service.task.GenerateTaskService;

public class TestGenerateTaskService extends AbstractDependencyInjectionSpringContextTests {
	
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
	
	@Resource(name="generateTaskService")
	public GenerateTaskService generateTaskService;
	
	
	public void testGenerateTaskService(){
		generateTaskService.generateTask();
	}
}
