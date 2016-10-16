package pub.willow.a.taskservice.dao;

import java.util.List;

import pub.willow.a.baseservice.beans.KeywordBean;

public interface KeywordsDao {
	
	public List<KeywordBean> queryKeywords(int status, int limit);
	
	public void updateStatus(int id, int status);
	
}
