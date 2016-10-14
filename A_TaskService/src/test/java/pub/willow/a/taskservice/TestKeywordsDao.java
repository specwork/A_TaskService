package pub.willow.a.taskservice;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import pub.willow.a.taskservice.dao.KeywordsDao;

public class TestKeywordsDao extends AbstractDependencyInjectionSpringContextTests {
	
	@Override
	protected String[] getConfigLocations() {
		return new String[] { 
				"applicationContext-init.xml","applicationContext-dao.xml"
//				"applicationContext-*.xml"
	  			  };
	}
	
	@Resource(name="keywordsDao")
	public KeywordsDao keywordsDao;
	
	
	public void testKeywordsDao(){
		List<String> list = keywordsDao.queryKeywords(0);
		for(String s:list) {
			System.out.println(s);
		}
	}
	
}
