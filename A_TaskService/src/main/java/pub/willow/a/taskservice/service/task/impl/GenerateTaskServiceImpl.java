package pub.willow.a.taskservice.service.task.impl;

import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;

import pub.willow.a.taskservice.beans.KeywordBean;
import pub.willow.a.taskservice.beans.ListpageBean;
import pub.willow.a.taskservice.beans.Status;
import pub.willow.a.taskservice.beans.TaskBean;
import pub.willow.a.taskservice.dao.KeywordsDao;
import pub.willow.a.taskservice.dao.ListpageDao;
import pub.willow.a.taskservice.dao.TaskDao;
import pub.willow.a.taskservice.service.task.GenerateTaskService;

public class GenerateTaskServiceImpl implements GenerateTaskService {

	@Resource(name = "keywordsDao")
	public KeywordsDao keywordsDao;

	@Resource(name = "listpageDao")
	public ListpageDao listpageDao;

	@Resource(name = "taskDao")
	public TaskDao taskDao;

	public void generateTask() {

		String charset = "UTF-8";
		String keywordCharset = "UTF-8";

		while (true) {
			List<KeywordBean> keywords = keywordsDao.queryKeywords(Status.WAITING, 100);
			if (keywords == null || keywords.size() <= 0) {
				break;
			}
			for (KeywordBean keywordBean : keywords) {

				int keywordId = keywordBean.getId();
				String keyword = keywordBean.getKeyword();
				int clientId = keywordBean.getClientId();
				try {
					if (clientId == 1) {
						String listpageUrl = "https://www.baidu.com/s?ie=utf-8&wd={keyword}&rn=30";

						String keywordAfterEncode;

						keywordAfterEncode = URLEncoder.encode(keyword, keywordCharset);

						String url = listpageUrl.replace("{keyword}", keywordAfterEncode);

						TaskBean taskBean = new TaskBean();
						taskBean.setClientId(clientId);
						taskBean.setCurrentPage(1);
						taskBean.setKeywordId(keywordId);
						taskBean.setKeyword(keyword);
						taskBean.setCharset(charset);
						taskBean.setUrl(url);

						taskDao.insertTask(taskBean);
						keywordsDao.updateStatus(keywordId, Status.DONE);
					} else {
						TaskBean taskBean = new TaskBean();
						taskBean.setClientId(clientId);
						taskBean.setCurrentPage(1);
						taskBean.setKeywordId(keywordId);
						taskBean.setKeyword(keyword);
						taskBean.setCharset(charset);
						
						String keywordAfterEncode = URLEncoder.encode(keyword, keywordCharset);
						
						String listpageUrl = "https://m.baidu.com/s?ie=utf-8&wd={keyword}";
						String url = listpageUrl.replace("{keyword}", keywordAfterEncode);
						taskBean.setCurrentPage(1);
						taskBean.setUrl(url);

						taskDao.insertTask(taskBean);
						
						listpageUrl = "https://m.baidu.com/s?ie=utf-8&wd={keyword}&pn=10";
						url = listpageUrl.replace("{keyword}", keywordAfterEncode);
						taskBean.setCurrentPage(2);
						taskBean.setUrl(url);
						
						taskDao.insertTask(taskBean);
						
						listpageUrl = "https://m.baidu.com/s?ie=utf-8&wd={keyword}&pn=20";
						url = listpageUrl.replace("{keyword}", keywordAfterEncode);
						taskBean.setCurrentPage(3);
						taskBean.setUrl(url);
						
						taskDao.insertTask(taskBean);
						
						keywordsDao.updateStatus(keywordId, Status.DONE);
					}

				} catch (Exception e) {
					e.printStackTrace();
					keywordsDao.updateStatus(keywordId, Status.ERROR);
					return;
				}
			}

		}

	}

}
