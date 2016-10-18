package pub.willow.a.taskservice.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import pub.willow.a.baseservice.beans.RedisKeyBean;
import pub.willow.a.baseservice.redis.RedisClient;
import pub.willow.a.baseservice.utils.SerializeUtil;
import pub.willow.a.baseservice.utils.StringUtil;
import pub.willow.a.taskservice.service.SpiderInfoService;

public class SpiderInfoServiceImpl implements SpiderInfoService {

	private static final int TRY_TIME = 3;	// 获取锁重试次数
	private static final int DELAY = 10;	// 爬虫休眠时间
	@Resource(name = "redisClient")
	private RedisClient redisClient;

	public List<String> getAllSpiders() {
		List<String> spiders = new ArrayList<String>();
		String key = RedisKeyBean.getSpiderListKey();
		if (redisClient.exists(key)) {
			byte[] spidersByte = redisClient.get(key.getBytes());
			spiders = (List<String>) SerializeUtil.unserialize(spidersByte);
		}
		return spiders;
	}
	
	public String getRandomSpider() {
		String[] spiders = {"153.36.230.135"};
		int spiderNum = spiders.length;
		return spiders[new Random().nextInt(spiderNum)];
	}
	public String getAvailableSpider() {

		String spider = null;

		List<String> spiders = new ArrayList<String>();
		String key = RedisKeyBean.getSpiderListKey();
		if (!redisClient.exists(key)) {
			return spider;
		}
		byte[] spidersByte = redisClient.get(key.getBytes());
		spiders = (List<String>) SerializeUtil.unserialize(spidersByte);

		if (spiders == null ||spiders.size() <= 0) {
			return spider;
		}
		for(String s:spiders) {
			
			String spiderLockKey = RedisKeyBean.getSpiderLockKey(s);
			if(spiderLockKey == null) {
				System.out.println("spiderLockKey is null");
				continue;
			}
			boolean lock = redisClient.updateWithLock(spiderLockKey, "1", DELAY);
			if(lock) {
				spider = s;
				return spider;
			}
		}

		return spider;
	}

	private boolean isAvailableSpider(String spider) {
		synchronized(spider) {
			String spiderLockKey = RedisKeyBean.getSpiderLockKey(spider);
		
			
			int count = 0;
			while (count <= 3) {
				String randomStr = (System.currentTimeMillis()  +new Random().nextInt(1000)) + "";
				Long lock = redisClient.setnx(spiderLockKey, randomStr);
				if(lock != null && lock.intValue()==1) {
					if(randomStr.equals(redisClient.get(spiderLockKey))) {
						return true;
					}
				}
				count ++;
			}
		}
		return false;
	}

	public void setSpiders(List<String> spiders) {
		String key = RedisKeyBean.getSpiderListKey();
		byte[] spidersByte = SerializeUtil.serialize(spiders);
		redisClient.set(key.getBytes(), spidersByte);
	}
	
}
