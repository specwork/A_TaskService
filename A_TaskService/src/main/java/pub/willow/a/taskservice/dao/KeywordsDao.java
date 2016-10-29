package pub.willow.a.taskservice.dao;

import java.util.List;

import pub.willow.a.taskservice.beans.KeywordBean;

public interface KeywordsDao {
	
	public List<KeywordBean> queryKeywords(int status, int limit);
	
	public KeywordBean queryKeyword(int status);
	
	public void updateMention(KeywordBean bean);

	public void updateStatus(int id, int status);
	
}
