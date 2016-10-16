package pub.willow.a.taskservice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import pub.willow.a.taskservice.service.SpiderInfoService;

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
	
	
	public void testKeywordsDao(){
		List<String> list = spiderInfoService.getAllSpiders();
		for(String s:list) {
			System.out.println(s);
		}
	}
	
	public void testSetSpider() {
		List<String> spiders = new ArrayList<String>();
		spiders.add("localhost");
		spiderInfoService.setSpiders(spiders);
	}
	
	public void testGetAvailableSpider() {
		while(true) {
			String spider = spiderInfoService.getAvailableSpider();
			System.out.println(new Date() + " " + spider);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
