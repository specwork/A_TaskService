package pub.willow.a.taskservice.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期操作类
 * @author albert.zhang
 *
 */
public class DateUtil {
	private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String date2Str(Date date) {
		if (date == null) {
			return null;
		}
		return DEFAULT_DATE_FORMAT.format(date);
	}
	
	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(1396507099000l);
		System.out.println(c.getTime());
	}
}
