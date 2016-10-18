package pub.willow.a.taskservice.dao.impl;

import java.util.ArrayList;
import java.util.List;

import pub.willow.a.baseservice.beans.DataBean;
import pub.willow.a.baseservice.db.BaseDao;
import pub.willow.a.baseservice.db.DBUtil;
import pub.willow.a.taskservice.dao.DataDao;

public class DataDaoImpl extends BaseDao implements DataDao  {

	public void insertData(DataBean DataBean) {
		// TODO Auto-generated method stub
		
	}

	public int insertData(List<DataBean> dataBeanList) {

		if(dataBeanList == null || dataBeanList.size()<=0) {
			return 0;
		}
		String sql = "insert into data (site_id,listpage_id,task_id,keyword_id,keyword,title,summary,url,source,spider,create_time) values(?,?,?,?,?,?,?,?,?,?,now())";
		
		DBUtil db = getDbUtilByDbName(A_PROJECT);
		List<String[]> paramsList = new ArrayList<String[]>();
		for(DataBean dataBean:dataBeanList) {
			String[] params = {dataBean.getSiteId()+"",dataBean.getListpageId()+"",dataBean.getTaskId()+"",dataBean.getKeywordId()+"",dataBean.getKeyword(),dataBean.getTitle(),dataBean.getSummary(),dataBean.getUrl(),dataBean.getSource(),dataBean.getSpider()};
			paramsList.add(params);
		}
//		db.prepareExecuteUpdateBatch(sql, paramsList );
		db.prepareExecuteUpdateBatchByDuplicate(sql, paramsList);
		return 0;
	}
	
	
}