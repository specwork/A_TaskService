package pub.willow.a.taskservice.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import pub.willow.a.baseservice.beans.KeywordBean;

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
		List<KeywordBean> list = keywordsDao.queryKeywords(0,10);
		for(KeywordBean s:list) {
			System.out.println(s);
		}
	}
	
}
