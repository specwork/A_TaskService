package pub.willow.a.taskservice.dao;

import java.util.List;

public interface KeywordsDao {
	
	public List<String> queryKeywords(int deleteFlag);
	
}
