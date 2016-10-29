package pub.willow.a.taskservice.service.impl;

import java.util.Random;

import pub.willow.a.taskservice.service.SpiderInfoService;

public class SpiderInfoServiceImpl implements SpiderInfoService {

	public String getRandomSpider() {
		String[] spiders = {"114.115.203.166","121.41.21.20","121.41.23.195","218.244.151.82","218.244.129.180","218.244.150.5","218.244.129.202","115.29.244.218","120.24.170.35","120.24.162.134","120.24.55.127","120.24.176.45","120.24.56.114","120.24.177.122","120.24.76.124","120.24.58.67","120.24.94.155","120.24.225.49","120.24.246.54","120.24.171.220","120.24.166.167","120.24.230.15","120.24.94.151","115.29.227.19","114.215.192.21","114.215.168.161","localhost"};
		int spiderNum = spiders.length;
		return spiders[new Random().nextInt(spiderNum)];
	}
	
}
