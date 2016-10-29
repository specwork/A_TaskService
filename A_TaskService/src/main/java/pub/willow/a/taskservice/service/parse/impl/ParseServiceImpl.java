package pub.willow.a.taskservice.service.parse.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pub.willow.a.taskservice.beans.DataBean;
import pub.willow.a.taskservice.beans.TaskBean;
import pub.willow.a.taskservice.service.parse.ParseService;
import pub.willow.a.taskservice.utils.RegexUtil;
import pub.willow.a.taskservice.utils.WebpageUtil;

public class ParseServiceImpl implements ParseService {

	public List<DataBean> parseHtml(TaskBean taskBean) {

		if (taskBean == null) {
			return null;
		}
		int clientId = taskBean.getClientId();
		String source = taskBean.getSource();

		// 1、获取主题帖的列表
		Map<String, String> fieldRuleMap = getFieldRuleMap(clientId);

		List<Map<String, String>> topicMapList = fetch(source, fieldRuleMap);
		List<DataBean> dataBeanList = toBean(topicMapList, taskBean);
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
					if (fieldInfo == null && fieldRule.contains("line-clamp")) {
						fieldInfo = RegexUtil.getMatchInfoSingle(topic, fieldRule, 2);
					}
					// 处理经过正则抽取之后的html乱码
					fieldInfo = DomTree.fiterHtml(fieldInfo);
					fieldInfo = WebpageUtil.filterHtmlTags(fieldInfo);
					// 2、处理乱码
					fieldInfo = WebpageUtil.filterMessyCode(fieldInfo);
					// 3、转义字符
					fieldInfo = WebpageUtil.escapeChar(fieldInfo);
					if (fieldInfo != null)
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
		if (topicMapList == null || topicMapList.size() <= 0) {
			return null;
		}
		List<DataBean> dataBeanList = new ArrayList<DataBean>();
		for (Map<String, String> m : topicMapList) {

			DataBean dataBean = new DataBean();
			dataBean.setTaskId(taskBean.getId());
			dataBean.setClientId(taskBean.getClientId());
			dataBean.setKeywordId(taskBean.getKeywordId());
			dataBean.setKeyword(taskBean.getKeyword());
			String title = m.get("title");
			dataBean.setTitle(title);
			dataBean.setUrl(m.get("url"));
			String summary = m.get("summary");
			summary = filterSummary(summary);

			dataBean.setSummary(summary);
			
			String source = getSource(title);

			dataBean.setSource(source);
			dataBeanList.add(dataBean);
		}

		return dataBeanList;
	}

	private static String filterSummary(String summary) {
		if (summary == null || summary.length() < 500) {
			return summary;
		}
		summary = summary.replaceAll("A\\.setup.*", "");
		summary = RegexUtil.getMatchInfoSingle(summary,
				"^\\D[^\u3007\u4E00-\u9FCB\uE815-\uE864]*(.*?)[^\u3007\u4E00-\u9FCB\uE815-\uE864]*$");
		if (summary != null && summary.length() > 5000) {
			summary = summary.substring(0, 4999);
		}
		return summary;
	}

	private String getSource(String title) {
		String source = title;
		if (title == null) {
			return null;
		}
		if (title.contains("_")) {
			source = title.substring(title.lastIndexOf("_") + 1).trim();
			if (source.contains("-")) {
				source = source.substring(source.lastIndexOf("-") + 1).trim();
			}
		} else if (title.contains("-")) {
			source = title.substring(title.lastIndexOf("-") + 1).trim();
		}
		if (source.contains("...") || source.length() > 16) {
			source = null;
		}
		return source;
	}

	private Map<String, String> getFieldRuleMap(int clientId) {

		Map<String, String> fieldRuleMap = new HashMap<String, String>();
		if (clientId == 1) {
			// fieldRuleMap.put("list",
			// "(<h3.*?)<[sd][pi][av]n?\\s+class=\"c-tools\"");
			fieldRuleMap.put("list", "(<h3.*?<[sd][pi][av]n?\\s+class=\"c-tools\"[^>]*>)");
			fieldRuleMap.put("title", "<h3.*?<a[^>]*>(.*?)</a>");
			fieldRuleMap.put("summary", "</h3>(.*)");
			fieldRuleMap.put("url", "<h3.*?<a[^>]*href\\s*=\\s*[\"']([^\"']*)");
		} else {
			fieldRuleMap.put("list", "(<div\\s*class=\"result\\s*c-result.*?</div>\\s*</div>\\s*</div>)");
			fieldRuleMap.put("title", "(<h3.*?</h3>)");
			// fieldRuleMap.put("summary",
			// "(<p\\s*class=\"c-line-clamp.*?</p>)");
			fieldRuleMap.put("summary", "(<p[^>]*c-line-clamp[43].*?</p>)|<div\\s*class=\"c-span\\d+[^>]*>(.*)");
			fieldRuleMap.put("url", "<a href=\"([^\"]*)[^>]*>\\s*<h3");
		}

		return fieldRuleMap;

	}

	public static void main(String[] args) {
		String summary = ".wa-ua-tagentity-count{overflow:hidden;text-overflow:ellipsis;white-space:nowrap;width:100%;}.wa-ua-tagentity-listitem {padding:7px 0;}.wa-ua-tagentity-listitem-title, .wa-ua-tagentity-listitem-abstract{overflow:hidden;text-overflow:ellipsis;white-space:nowrap;width:100%;line-height:22px;} 苹果牛奶西瓜胡萝卜 草莓茶叶西红柿绿豆 以上推荐基于网友回答提供（共302条）发烧后能吃什么？蛋：鸡蛋所含营养的确丰富，但不宜在发烧期间多吃鸡蛋，这是因为鸡蛋内的蛋白质在体内分解后，会产生一定的额外热量，使机体热量增高，加剧发烧症状，并延长发热时间，增加患者痛苦。 二忌多喝茶：喝浓茶会使大脑保持兴奋的状态，且使脉搏加快，血压升高，进而使患者体温升高、烦发烧了能吃什么发烧本身不是疾病，而是一种症状。其实，它是体内抵抗感染的机制之一。发烧甚至可能有它的用途：缩短疾病时间、增强抗生素的效果、使感染较不具传染性。这些能力应可以抵消发烧时所经历的不舒服。如果你需要额外的缓解，可以试试下面的方法。 补充液体 当你感到热时，你的身体会百度知道&#xe734";
		summary = filterSummary(summary);
		System.out.println(summary);
	}
}
