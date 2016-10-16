package pub.willow.a.taskservice.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pub.willow.a.baseservice.beans.ListpageBean;
import pub.willow.a.baseservice.db.BaseDao;
import pub.willow.a.baseservice.db.DBUtil;
import pub.willow.a.baseservice.utils.StringUtil;
import pub.willow.a.taskservice.dao.ListpageDao;

public class ListpageDaoImpl extends BaseDao implements ListpageDao {

	public List<ListpageBean> queryListpage(int deleteFlag) {

		List<ListpageBean>  listpageBeans = new ArrayList<ListpageBean>();
		String sql = "select * from listpage where delete_flag=" + deleteFlag;
		DBUtil db = getDbUtilByDbName(A_PROJECT);
		
		 List<Map<String, String>>  listMap = db.executeQuery(sql);
		 if(listMap != null && listMap.size()>0) {
			 for(Map<String,String> map:listMap) {
				 String idStr = map.get("id");
				 String siteIdStr = map.get("site_id");
				 String name = map.get("name");
				 String url = map.get("url");
				 String charset = map.get("charset");
				 String keywordCharset = map.get("keyword_charset");
				
				 ListpageBean listpageBean = new ListpageBean();
				 
				 listpageBean.setId(StringUtil.convertStr(idStr, 0));
				 listpageBean.setSiteId(StringUtil.convertStr(siteIdStr, 0));
				 listpageBean.setName(name);
				 listpageBean.setUrl(url);
				 listpageBean.setCharset(charset);
				 listpageBean.setKeywordCharset(keywordCharset);
				 
				 listpageBeans.add(listpageBean);
			 }
		 }
		return listpageBeans;
	}
	
}

