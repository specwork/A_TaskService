package pub.willow.a.taskservice.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import pub.willow.a.baseservice.beans.ListpageBean;
import pub.willow.a.baseservice.beans.TaskBean;

public class TestTaskDao extends AbstractDependencyInjectionSpringContextTests {
	
	@Override
	protected String[] getConfigLocations() {
		return new String[] { 
				"applicationContext-init.xml","applicationContext-dao.xml"
//				"applicationContext-*.xml"
	  			  };
	}
	
	@Resource(name="taskDao")
	public TaskDao taskDao;
	
	
	public void testQueryTaskDao(){
		TaskBean task = taskDao.queryOneTask(0);
		System.out.println(task.getCharset());
	}
	
	public void testInsertTaskDao() {
		
	}
}
