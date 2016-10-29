package pub.willow.a.taskservice.beans;

public class RedisKeyBean {

	private static final String SPIDER_LIST="SPIDER:LIST";
	private static final String SPIDER_LOCK="SPIDER:LOCK_${spider}";
	
	
	public static String getSpiderListKey() {
		return SPIDER_LIST;
	}
	public static String getSpiderLockKey(String spider) {
		if(spider == null) {
			return null;
		}
		return SPIDER_LOCK.replace("{spider}", spider);
	}
}
