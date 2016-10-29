package pub.willow.a.taskservice.beans;

public class ListpageBean {
	private int id;
	private int siteId;
	private String name;
	private String url;
	private String charset;
	private String keywordCharset;
	private int deleteFlag;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public String getKeywordCharset() {
		return keywordCharset;
	}
	public void setKeywordCharset(String keywordCharset) {
		this.keywordCharset = keywordCharset;
	}
	public int getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
}
