package pub.willow.a.taskservice.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import pub.willow.a.baseservice.beans.ListpageBean;

public class TestListpageDao extends AbstractDependencyInjectionSpringContextTests {
	
	@Override
	protected String[] getConfigLocations() {
		return new String[] { 
				"applicationContext-init.xml","applicationContext-dao.xml"
//				"applicationContext-*.xml"
	  			  };
	}
	
	@Resource(name="listpageDao")
	public ListpageDao listpageDao;
	
	
	public void testKeywordsDao(){
		List<ListpageBean> list = listpageDao.queryListpage(0);
		for(ListpageBean s:list) {
			
			System.out.println(s.getId());
			System.out.println(s.getSiteId());
			System.out.println(s.getName());
			System.out.println(s.getUrl());
			System.out.println(s.getCharset());
			System.out.println(s.getKeywordCharset());
			System.out.println(s.getDeleteFlag());
		}
	}
	
}
