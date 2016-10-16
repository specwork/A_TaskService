package pub.willow.a.taskservice.service;

import java.util.List;

public interface SpiderInfoService {
	
	public List<String> getAllSpiders();
	
	public String getAvailableSpider();
	
	public String getRandomSpider();
	
	public void setSpiders(List<String> spiders);
}
