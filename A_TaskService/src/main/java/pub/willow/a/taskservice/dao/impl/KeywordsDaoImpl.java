package pub.willow.a.taskservice.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pub.willow.a.baseservice.db.BaseDao;
import pub.willow.a.baseservice.db.DBUtil;
import pub.willow.a.taskservice.dao.KeywordsDao;

public class KeywordsDaoImpl extends BaseDao implements KeywordsDao{

	public List<String> queryKeywords(int deleteFlag) {
		
		List<String> keywords = new ArrayList<String>();
		String sql = "select * from keywords where delete_flag="+ deleteFlag;
		DBUtil db = getDbUtilByDbName(A_PROJECT);
		
		 List<Map<String, String>>  listMap = db.executeQuery(sql);
		 if(listMap != null && listMap.size()>0) {
			 for(Map<String,String> map:listMap) {
				 String keyword = map.get("keyword");
				 keywords.add(keyword);
			 }
		 }
		return keywords;
	}
}
