package pub.willow.a.taskservice.dao;

import java.util.List;

import pub.willow.a.taskservice.beans.TaskBean;

public interface TaskDao {
	
	public TaskBean queryOneTask(int status);
	
	public void insertTask(TaskBean taskBean);
	
	public int insertTask(List<TaskBean> taskBeanList);
	
	public void updateTaskStatus(int taskId, int status);
}