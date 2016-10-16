package pub.willow.a.taskservice.service.task.impl;

import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;

import pub.willow.a.baseservice.beans.KeywordBean;
import pub.willow.a.baseservice.beans.ListpageBean;
import pub.willow.a.baseservice.beans.Status;
import pub.willow.a.baseservice.beans.TaskBean;
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

		List<ListpageBean> listpages = listpageDao.queryListpage(0);

		if (listpages == null || listpages.size() <= 0) {
			return;
		}

		while (true) {
			List<KeywordBean> keywords = keywordsDao.queryKeywords(Status.WAITING, 100);
			if (keywords == null || keywords.size() <= 0) {
				break;
			}
			for (KeywordBean keywordBean : keywords) {

				int keywordId = keywordBean.getId();
				String keyword = keywordBean.getKeyword();
				try {
					for (ListpageBean listpage : listpages) {
						int siteId = listpage.getSiteId();
						int listpageId = listpage.getId();
						String listpageUrl = listpage.getUrl();
						String charset = listpage.getCharset();
						String keywordCharset = listpage.getKeywordCharset();
						String keywordAfterEncode;

						keywordAfterEncode = URLEncoder.encode(keyword, keywordCharset);

						String url = listpageUrl.replace("{keyword}", keywordAfterEncode);

						TaskBean taskBean = new TaskBean();

						taskBean.setSiteId(siteId);
						taskBean.setListpageId(listpageId);
						taskBean.setKeywordId(keywordId);
						taskBean.setKeyword(keyword);
						taskBean.setCharset(charset);
						taskBean.setUrl(url);

						taskDao.insertTask(taskBean);
					}
					keywordsDao.updateStatus(keywordId, Status.DONE);
				} catch (Exception e) {
					e.printStackTrace();
					keywordsDao.updateStatus(keywordId, Status.ERROR);
					return;
				}
			}

		}

	}

}
