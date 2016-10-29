package pub.willow.a.taskservice.service.impl;

import java.util.List;

import javax.annotation.Resource;

import pub.willow.a.taskservice.beans.DataBean;
import pub.willow.a.taskservice.beans.KeywordBean;
import pub.willow.a.taskservice.beans.Status;
import pub.willow.a.taskservice.dao.DataDao;
import pub.willow.a.taskservice.dao.KeywordsDao;
import pub.willow.a.taskservice.service.MentionService;

public class MentionServiceImpl implements MentionService {

	@Resource(name="dataDao")
	public DataDao dataDao;
	@Resource(name = "keywordsDao")
	public KeywordsDao keywordsDao;

	public void calMention() {
		while(true) {
			KeywordBean bean = keywordsDao.queryKeyword(Status.WAITING);
			if(bean == null) {
				System.out.println("Done");
				break;
			}
			int keywordId = bean.getId();
			String type = bean.getType();
			if(type.contains("Brand")) {
				System.out.println("Brand");
				keywordsDao.updateStatus(keywordId, Status.DONE);
				continue;
			}
			
			List<DataBean> datas = dataDao.queryDataByKeyword(keywordId);
			String medicine = bean.getMedicine();
			String mentionKeyword_1 = getMentionKeyword_1(medicine);
			String mentionKeyword_2 = getMentionKeyword_2(medicine);
			
			int mention_1 = calMention(mentionKeyword_1,datas);
			int mention_2 = calMention(mentionKeyword_2,datas);
			
			bean.setMention_1(mention_1);
			bean.setMention_2(mention_2);
			
			keywordsDao.updateMention(bean);
		}
	}

	private int calMention(String mentionKeyword, List<DataBean> datas) {
		int mention = 0;
		for(DataBean data:datas) {
			String titleSummary = data.getTitle() + data.getSummary();
			if(titleSummary.contains(mentionKeyword)) {
				mention ++;
			}
		}
		return mention;
	}

	private String getMentionKeyword_1(String medicine) {

		if(medicine.equals("CNT")) {
			return "感康";
		} else if(medicine.equals("FBD")) {
			return "散列痛";
		} else if(medicine.equals("BTB")) {
			return "红霉素软膏";
		} else if(medicine.equals("VE")) {
			return "云南白药";
		} else if(medicine.equals("FLX")) {
			return "息斯敏";
		} else if(medicine.equals("SSD")) {
			return "云南白药";
		} else if(medicine.equals("PLD")) {
			return "雅克菱";
		}
		return null;
	}
	
	private String getMentionKeyword_2(String medicine) {

		if(medicine.equals("CNT")) {
			return "新康泰克";
		} else if(medicine.equals("FBD")) {
			return "芬必得";
		} else if(medicine.equals("BTB")) {
			return "百多邦";
		} else if(medicine.equals("VE")) {
			return "扶他林";
		} else if(medicine.equals("FLX")) {
			return "辅舒良";
		} else if(medicine.equals("SSD")) {
			return "舒适达";
		} else if(medicine.equals("PLD")) {
			return "保丽净";
		}
		return null;
	}
	
}
