package pub.willow.a.taskservice.utils;

import java.util.ResourceBundle;

public class PropConfig {
	public static synchronized String getFileConfig(String fileName,String key){
	    ResourceBundle rb = ResourceBundle.getBundle(fileName);
	    return rb.getString(key);
	}
	
	public static void main(String[] args){
		String fileName = "kafka";
		String key = "metadata.broker.list";
		System.out.println(PropConfig.getFileConfig(fileName, key));
	}
}
