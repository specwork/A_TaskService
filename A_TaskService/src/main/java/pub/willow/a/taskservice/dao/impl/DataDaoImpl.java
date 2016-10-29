package pub.willow.a.taskservice.dao.impl;

import java.util.ArrayList;
import java.util.List;

import pub.willow.a.taskservice.beans.DataBean;
import pub.willow.a.taskservice.dao.DataDao;
import pub.willow.a.taskservice.dao.base.BaseDao;
import pub.willow.a.taskservice.dao.base.DBUtil;

public class DataDaoImpl extends BaseDao implements DataDao  {

	public void insertData(DataBean DataBean) {
		// TODO Auto-generated method stub
		
	}

	public int insertData(List<DataBean> dataBeanList) {

		if(dataBeanList == null || dataBeanList.size()<=0) {
			return 0;
		}
		String sql = "insert into data (task_id,client_id,keyword_id,keyword,title,summary,url,source,spider,nextpage,create_time) values(?,?,?,?,?,?,?,?,?,?,now())";
		
		DBUtil db = getDbUtilByDbName(A_PROJECT);
		List<String[]> paramsList = new ArrayList<String[]>();
		for(DataBean dataBean:dataBeanList) {
			String[] params = {dataBean.getTaskId()+"",dataBean.getClientId()+"",dataBean.getKeywordId()+"",dataBean.getKeyword(),dataBean.getTitle(),dataBean.getSummary(),dataBean.getUrl(),dataBean.getSource(),dataBean.getSpider(),dataBean.getNextpage()+""};
			paramsList.add(params);
		}
//		db.prepareExecuteUpdateBatch(sql, paramsList );
		db.prepareExecuteUpdateBatchByDuplicate(sql, paramsList);
		return 0;
	}
	
	
}