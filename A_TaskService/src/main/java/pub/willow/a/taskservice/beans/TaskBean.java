package pub.willow.a.taskservice.beans;

import java.io.Serializable;

import pub.willow.a.taskservice.utils.StringUtil;
public class TaskBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6218483147968905157L;
	
	private int id; // 任务唯一标识
	private String url; // 任务url
	private int keywordId;
	private String keyword;
	private int listpageId; // 板块id
	private int siteId; // 网站Id
	private int currentPage; // 翻页数量
	private String charset; // 网站编码格式
	private String source; // 网页源码
	private int nextpage; // 1:has nextpage; 2: doesn't hava nextpage;
	
	@Override
	public String toString() {
		return StringUtil.objToString(this);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public int getListpageId() {
		return listpageId;
	}

	public void setListpageId(int listpageId) {
		this.listpageId = listpageId;
	}


	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getNextpage() {
		return nextpage;
	}

	public void setNextpage(int nextpage) {
		this.nextpage = nextpage;
	}
	
}
