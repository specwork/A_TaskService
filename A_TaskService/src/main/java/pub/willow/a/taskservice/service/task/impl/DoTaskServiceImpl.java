package pub.willow.a.taskservice.service.task.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import pub.willow.a.taskservice.beans.DataBean;
import pub.willow.a.taskservice.beans.Status;
import pub.willow.a.taskservice.beans.TaskBean;
import pub.willow.a.taskservice.dao.DataDao;
import pub.willow.a.taskservice.dao.TaskDao;
import pub.willow.a.taskservice.service.parse.ParseService;
import pub.willow.a.taskservice.service.task.DistributeTaskService;
import pub.willow.a.taskservice.service.task.DoTaskService;

public class DoTaskServiceImpl implements DoTaskService {
	@Resource(name="taskDao")
	public TaskDao taskDao;
	@Resource(name="dataDao")
	public DataDao dataDao;
	@Resource(name="distributeTaskService")
	public DistributeTaskService distributeTaskService;
	@Resource(name="parseService")
	public ParseService parseService;
	
	public void doTask() {
		while(true) {
			String spider = getRandomSpider();
			if(spider == null) {
				continue;
			}
			TaskBean task = taskDao.queryOneTask(Status.WAITING);
			if(task == null) {
				System.out.println("No task.");
				break;
			}
			int taskId = task.getId();
			System.out.print(new Date()+ " taskId->" +taskId);
			try {
				
				task = distributeTaskService.distributeTask(task,spider);
				String source = task.getSource();
				int keywordId = task.getKeywordId();
				String keyword = task.getKeyword();
				int clientId = task.getClientId();
				String path = "D:\\Willow\\A_Project\\" +taskId+"_"+keywordId+"_"+clientId + ".txt";
				saveFile(source,path );
				task.setKeyword(keyword);
				int nextpage = source.contains(">下一页")?1:0;
				 List<DataBean> dataBeanList = parseService.parseHtml(task);
				 setSpider(dataBeanList,spider,nextpage);
				 dataDao.insertData(dataBeanList);
				 
				taskDao.updateTaskStatus(taskId , Status.DONE);
//				Thread.sleep(5000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					System.out.println(task.getSource());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				taskDao.updateTaskStatus(taskId , Status.ERROR);
			}
		}
		
	}
	
	private void setSpider(List<DataBean> dataBeanList, String spider, int nextpage) {
		if(dataBeanList == null || dataBeanList.size()<=0){
			return ;
		}
		for(DataBean dataBean: dataBeanList){
			dataBean.setSpider(spider);
			dataBean.setNextpage(nextpage);
		}
		
	}

	private String getRandomSpider() {
		String[] spiders = {"localhost"};
		int spiderNum = spiders.length;
		return spiders[new Random().nextInt(spiderNum)];
	}
	
	public static void saveFile(String content, String path) {
		File f = new File(path);
		FileWriter fw;
		BufferedWriter bw;
		try {
			fw = new FileWriter(f);// 初始化输出流
			bw = new BufferedWriter(fw);// 初始化输出字符流
			bw.write(content);// 写文件
			bw.flush();
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
