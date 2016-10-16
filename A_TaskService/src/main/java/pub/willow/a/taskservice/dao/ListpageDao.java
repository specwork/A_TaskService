package pub.willow.a.taskservice.dao;

import java.util.List;

import pub.willow.a.baseservice.beans.ListpageBean;

public interface ListpageDao {
	
	public List<ListpageBean> queryListpage(int deleteFlag);
}

