package pub.willow.a.taskservice.dao.impl;

import java.util.List;
import java.util.Map;

import pub.willow.a.taskservice.beans.TaskBean;
import pub.willow.a.taskservice.dao.TaskDao;
import pub.willow.a.taskservice.dao.db.BaseDao;
import pub.willow.a.taskservice.dao.db.DBUtil;

public class TaskDaoImpl extends BaseDao implements TaskDao {

	public TaskBean queryOneTask(int status) {
		String sql = "select * from task where status=" + status + " limit 1";
		DBUtil db = getDbUtilByDbName(A_PROJECT);
		 List<Map<String, String>> listMap = db.executeQuery(sql);
		 if(listMap == null || listMap.size()<=0) {
			 return null;
		 }
		 Map<String,String> map = listMap.get(0);
		 if(map == null) {
			 return null;
		 }
		 TaskBean task = new TaskBean();
		 task.setId(Integer.parseInt(map.get("id")));
		 task.setSiteId(Integer.parseInt(map.get("site_id")));
		 task.setListpageId(Integer.parseInt(map.get("listpage_id")));
		 task.setKeywordId(Integer.parseInt(map.get("keyword_id")));
		 task.setKeyword(map.get("keyword"));
		 task.setUrl(map.get("url"));
		 task.setCharset(map.get("charset"));
		return task;
	}

	public void insertTask(TaskBean taskBean) {

		String[] params = {taskBean.getKeywordId()+"",taskBean.getKeyword(),taskBean.getSiteId()+"",taskBean.getListpageId()+"",taskBean.getUrl(),taskBean.getCurrentPage()+"",taskBean.getCharset()};
		String sql = "insert ignore into task (keyword_id,keyword,site_id,listpage_id,url,current_page,charset,create_time,status) values(?,?,?,?,?,?,?,now(),0)";
		DBUtil db = getDbUtilByDbName(A_PROJECT);
		db.prepareExecuteUpdate(sql, params);
	}

	public int insertTask(List<TaskBean> taskBeanList) {
		
		String sql = "insert ignore into task (id,keyword_id,site_id,listpage_id,url,charset,create_time,status) values(?,?,?,?,?,?,?,?)";
		return 0;
	}

	public void updateTaskStatus(int taskId, int status) {
		String sql = "update task set status=" + status + " where id=" + taskId;
		DBUtil db = getDbUtilByDbName(A_PROJECT);
		db.executeUpdate(sql);
		
	}
	
}

