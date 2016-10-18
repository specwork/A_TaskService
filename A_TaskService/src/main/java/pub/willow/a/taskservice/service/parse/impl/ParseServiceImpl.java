package pub.willow.a.taskservice.service.parse.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pub.willow.a.baseservice.beans.DataBean;
import pub.willow.a.baseservice.beans.TaskBean;
import pub.willow.a.baseservice.utils.RegexUtil;
import pub.willow.a.baseservice.utils.WebpageUtil;
import pub.willow.a.taskservice.service.parse.ParseService;

public class ParseServiceImpl implements ParseService {

	public List<DataBean> parseHtml(TaskBean taskBean) {

		if (taskBean == null) {
			return null;
		}
		int listpageId = taskBean.getListpageId();
		String source = taskBean.getSource();
		
		// 1、获取主题帖的列表
		Map<String, String> fieldRuleMap = getFieldRuleMap(listpageId);
		
		List<Map<String, String>> topicMapList = fetch(source, fieldRuleMap);
		List<DataBean> dataBeanList = toBean(topicMapList,taskBean);
		return dataBeanList;
	}

	public List<Map<String, String>> fetch(String source, Map<String, String> fieldRuleMap) {
		List<Map<String, String>> topicMap = new ArrayList<Map<String, String>>();

		// 1、获取主题帖的列表
		List<String> topicList = RegexUtil.getMatchInfoList(source, fieldRuleMap.remove("list"));

		// 2、获取主题帖每个字段的信息
		String fieldName = null;
		String fieldRule = null;
		String fieldInfo = null;
		String tempTopic = null;
		try {
			for (String topic : topicList) { // 遍历主题帖
				tempTopic = topic;
				Map<String, String> fieldInfoMap = new HashMap<String, String>();
				Set<String> fieldRuleSet = fieldRuleMap.keySet();
				Iterator<String> fieldRuleIt = fieldRuleSet.iterator();
				while (fieldRuleIt.hasNext()) {
					topic = tempTopic;
					fieldName = fieldRuleIt.next();
					fieldRule = fieldRuleMap.get(fieldName);
					
					fieldInfo = RegexUtil.getMatchInfoSingle(topic, fieldRule);
					if(fieldInfo == null && fieldRule.contains("line-clamp")) {
						System.out.println(fieldInfo);
						fieldInfo = RegexUtil.getMatchInfoSingle(topic, fieldRule, 2);
					}
					// 处理经过正则抽取之后的html乱码
					fieldInfo = DomTree.fiterHtml(fieldInfo);
					fieldInfo = WebpageUtil.filterHtmlTags(fieldInfo);
					// 2、处理乱码
					fieldInfo = WebpageUtil.filterMessyCode(fieldInfo);
					// 3、转义字符
					fieldInfo = WebpageUtil.escapeChar(fieldInfo);
					if(fieldInfo != null)
						fieldInfo = fieldInfo.replaceAll("\\s{1,}", " ");
					fieldInfoMap.put(fieldName, fieldInfo);
				}
				topicMap.add(fieldInfoMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return topicMap;
	}

	private List<DataBean> toBean(List<Map<String, String>> topicMapList, TaskBean taskBean) {
		if(topicMapList == null || topicMapList.size()<=0) {
			return null;
		}
		List<DataBean> dataBeanList = new ArrayList<DataBean>();
		for(Map<String, String> m:topicMapList) {
			DataBean dataBean = new DataBean();
			dataBean.setTaskId(taskBean.getId());
			dataBean.setKeywordId(taskBean.getKeywordId());
			dataBean.setKeyword(taskBean.getKeyword());
			dataBean.setListpageId(taskBean.getListpageId());
			dataBean.setSiteId(taskBean.getSiteId());
			dataBean.setTitle(m.get("title"));
			dataBean.setUrl(m.get("url"));
			dataBean.setSummary(m.get("summary"));
			dataBeanList.add(dataBean);
		}
		
		return dataBeanList;
	}

	private Map<String, String> getFieldRuleMap(int listpageId) {

		Map<String, String> fieldRuleMap = new HashMap<String, String>();
		if (listpageId == 1) {
//			fieldRuleMap.put("list", "(<h3.*?)<[sd][pi][av]n?\\s+class=\"c-tools\"");
			fieldRuleMap.put("list", "(<h3.*?<[sd][pi][av]n?\\s+class=\"c-tools\"[^>]*>)");
			fieldRuleMap.put("title", "<h3.*?<a[^>]*>(.*?)</a>");
			fieldRuleMap.put("summary", "</h3>(.*)");
			fieldRuleMap.put("url", "<h3.*?<a[^>]*href\\s*=\\s*[\"']([^\"']*)");
		} else {
			fieldRuleMap.put("list", "(<div\\s*class=\"result\\s*c-result.*?</div>\\s*</div>\\s*</div>)");
			fieldRuleMap.put("title", "(<h3.*?</h3>)");
//			fieldRuleMap.put("summary", "(<p\\s*class=\"c-line-clamp.*?</p>)");
			fieldRuleMap.put("summary", "(<p[^>]*c-line-clamp[43].*?</p>)|百度知道</h3>(.*)");
			fieldRuleMap.put("url", "<a href=\"([^\"]*)[^>]*>\\s*<h3");
		}

		return fieldRuleMap;

	}
}
