package pub.willow.a.taskservice.dao;

import java.util.List;

import pub.willow.a.taskservice.beans.DataBean;

public interface DataDao {
	
	
	public void insertData(DataBean DataBean);
	
	public int insertData(List<DataBean> dataBeanList);
	
	public List<DataBean> queryDataByKeyword(int keywordId);
}