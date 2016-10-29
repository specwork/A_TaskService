package pub.willow.a.taskservice.service.parse;

import java.util.List;

import pub.willow.a.taskservice.beans.DataBean;
import pub.willow.a.taskservice.beans.TaskBean;

public interface ParseService {
	
	public List<DataBean> parseHtml(TaskBean taskBean);
}
