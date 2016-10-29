package pub.willow.a.taskservice.beans;

public class KeywordBean {
	
	private int id;
	private String medicine;
	private String type;
	private String client;
	private int clientId;
	private String keyword;
	private int mention_1;
	private int mention_2;
	private int status;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getMedicine() {
		return medicine;
	}
	public void setMedicine(String medicine) {
		this.medicine = medicine;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public int getMention_1() {
		return mention_1;
	}
	public void setMention_1(int mention_1) {
		this.mention_1 = mention_1;
	}
	public int getMention_2() {
		return mention_2;
	}
	public void setMention_2(int mention_2) {
		this.mention_2 = mention_2;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
