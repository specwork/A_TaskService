package pub.willow.a.taskservice.beans;

import java.util.List;

public class SpiderParamsBean {
	
	private int id; // 编号
	private int siteId; // 网站ID
	private String listpageId; // 版块ID,-1表示所有版块
	private String crawlerType; // 任务类型,-1表示所有任务类型
	private List<String> headers;
	private int deleteFlag; // 是否冻结，0-未冻结，1-已冻结
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSiteId() {
		return siteId;
	}
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	public String getListpageId() {
		return listpageId;
	}
	public void setListpageId(String listpageId) {
		this.listpageId = listpageId;
	}
	public String getCrawlerType() {
		return crawlerType;
	}
	public void setCrawlerType(String crawlerType) {
		this.crawlerType = crawlerType;
	}
	public List<String> getHeaders() {
		return headers;
	}
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}
	public int getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
}
