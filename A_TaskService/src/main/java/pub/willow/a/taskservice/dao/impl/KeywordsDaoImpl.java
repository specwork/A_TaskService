package pub.willow.a.taskservice.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pub.willow.a.taskservice.beans.KeywordBean;
import pub.willow.a.taskservice.dao.KeywordsDao;
import pub.willow.a.taskservice.dao.base.BaseDao;
import pub.willow.a.taskservice.dao.base.DBUtil;

public class KeywordsDaoImpl extends BaseDao implements KeywordsDao {

	public List<KeywordBean> queryKeywords(int status, int limit) {

		List<KeywordBean> keywords = new ArrayList<KeywordBean>();
		String sql = "select * from keywords where status=" + status + " limit " + limit;
		DBUtil db = getDbUtilByDbName(A_PROJECT);

		List<Map<String, String>> listMap = db.executeQuery(sql);
		if (listMap != null && listMap.size() > 0) {
			for (Map<String, String> map : listMap) {
				int id = Integer.parseInt(map.get("id"));
				String keyword = map.get("keyword");
				KeywordBean keywordBean = new KeywordBean();
				keywordBean.setId(id);
				keywordBean.setMedicine(map.get("medicine"));
				keywordBean.setType(map.get("type"));
				keywordBean.setClient(map.get("client"));
				keywordBean.setClientId(Integer.parseInt(map.get("client_id")));
				keywordBean.setKeyword(keyword);
				keywordBean.setStatus(status);

				keywords.add(keywordBean);
			}
		}
		return keywords;
	}

	public void updateMention(KeywordBean bean) {
		String sql = "update keywords set mention_1=" + bean.getMention_1() + ",mention_2=" + bean.getMention_2() + ",status=2 where id=" + bean.getId();
		DBUtil db = getDbUtilByDbName(A_PROJECT);
		db.executeUpdate(sql);
	}

	public void updateStatus(int id, int status) {
		String sql = "update keywords set status=" + status + " where id=" + id;
		DBUtil db = getDbUtilByDbName(A_PROJECT);
		db.executeUpdate(sql);

	}

	public KeywordBean queryKeyword(int status) {
		
		String sql = "select * from keywords where status=" + status + " limit 1";
		DBUtil db = getDbUtilByDbName(A_PROJECT);

		List<Map<String, String>> listMap = db.executeQuery(sql);
		if (listMap != null && listMap.size() > 0) {
			Map<String, String> map = listMap.get(0);
			int id = Integer.parseInt(map.get("id"));
			String keyword = map.get("keyword");
			KeywordBean keywordBean = new KeywordBean();
			keywordBean.setId(id);
			keywordBean.setMedicine(map.get("medicine"));
			keywordBean.setType(map.get("type"));
			keywordBean.setClient(map.get("client"));
			keywordBean.setClientId(Integer.parseInt(map.get("client_id")));
			keywordBean.setKeyword(keyword);
			keywordBean.setStatus(status);
			return keywordBean;
		}
		return null;
	}
}
