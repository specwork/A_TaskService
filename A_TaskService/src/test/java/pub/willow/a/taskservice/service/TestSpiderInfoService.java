package pub.willow.a.taskservice.service;

import javax.annotation.Resource;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class TestSpiderInfoService extends AbstractDependencyInjectionSpringContextTests {
	
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
	
	@Resource(name="spiderInfoService")
	public SpiderInfoService spiderInfoService;
	
	
}
