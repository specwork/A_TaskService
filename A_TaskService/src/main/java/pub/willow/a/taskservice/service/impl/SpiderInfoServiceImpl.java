package pub.willow.a.taskservice.service.impl;

import java.util.Random;

import pub.willow.a.taskservice.service.SpiderInfoService;

public class SpiderInfoServiceImpl implements SpiderInfoService {

	private static final int TRY_TIME = 3;	// 获取锁重试次数
	private static final int DELAY = 10;	// 爬虫休眠时间
	
	public String getRandomSpider() {
		String[] spiders = {"localhost"};
		int spiderNum = spiders.length;
		return spiders[new Random().nextInt(spiderNum)];
	}
	
}
