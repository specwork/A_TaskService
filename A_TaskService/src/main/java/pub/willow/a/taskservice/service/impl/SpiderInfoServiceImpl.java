package pub.willow.a.taskservice.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import pub.willow.a.baseservice.redis.RedisClient;
import pub.willow.a.taskservice.service.SpiderInfoService;

public class SpiderInfoServiceImpl implements SpiderInfoService {
	
	@Resource(name="redisClient")
	private RedisClient redisClient;
	
	public List<String> getAllSpiders() {
		List<String> spiders = new ArrayList<String>();
		String key="SPIDER:LIST";
		if(redisClient.exists(key)) {
			byte[] spidersByte = redisClient.get(key.getBytes());
			
		}
		return null;
	}
	
}
