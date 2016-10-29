package pub.willow.a.taskservice.beans;

public class DataBean {
	// test
	// and again
	private int siteId;
	private int listpageId;
	private int taskId;
	private int keywordId;
	private String keyword;
	private String title;
	private String summary;
	private String url;
	private String source;
	private String spider;
	private int nextpage; // 1:has nextpage; 2: doesn't hava nextpage;
	
	
	private String spiderTime;
	public int getSiteId() {
		return siteId;
	}
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	public int getListpageId() {
		return listpageId;
	}
	public void setListpageId(int listpageId) {
		this.listpageId = listpageId;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public int getKeywordId() {
		return keywordId;
	}
	public void setKeywordId(int keywordId) {
		this.keywordId = keywordId;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSpider() {
		return spider;
	}
	public void setSpider(String spider) {
		this.spider = spider;
	}
	public String getSpiderTime() {
		return spiderTime;
	}
	public void setSpiderTime(String spiderTime) {
		this.spiderTime = spiderTime;
	}
	public int getNextpage() {
		return nextpage;
	}
	public void setNextpage(int nextpage) {
		this.nextpage = nextpage;
	}
	
}
