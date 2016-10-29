package pub.willow.a.taskservice.service;

import javax.annotation.Resource;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class TestMentionService extends AbstractDependencyInjectionSpringContextTests {
	
	@Override
	protected String[] getConfigLocations() {
		return new String[] { 
				"applicationContext-init.xml",
				"applicationContext-dao.xml",
				"applicationContext-service.xml",
//				"applicationContext-*.xml"
	  			  };
	}
	
	@Resource(name="mentionService")
	public MentionService mentionService;
	
	public void test() {
		mentionService.calMention();
	}
}
