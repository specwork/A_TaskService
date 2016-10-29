package pub.willow.a.taskservice.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author paul.ge@timerchina.com
 * @version 创建时间：2013-8-27 上午10:39:21
 * 
 */
public class RegexUtil {
	
	/**
	 * 获取Matcher
	 * @param regex	正则表达式
	 * @param input	正则表达式处理的字符串
	 * @return	Matcher
	 */
	public static Matcher getMatcher(String regex, String input) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(input);
		return matcher;
	}
	
	/**
	 * 获取匹配的正则表达式的信息，匹配多条信息
	 * @param sourceCode
	 * @param regex
	 * @return
	 */
	public static List<String> getMatchInfoList(String sourceCode, String regex) {
		List<String> matchInfoList = new ArrayList<String>();
		Matcher matcher = getMatcher(regex, sourceCode);
		while(matcher.find()) {
			matchInfoList.add(matcher.group(1));
		}
		return matchInfoList;
	}
	
	/**
	 * 获取匹配的正则表达式信息，只匹配一条信息
	 * @param sourceCode	
	 * @param regex
	 * @return
	 */
	public static String getMatchInfoSingle(String sourceCode, String regex) {
		String matchInfo = null;
		Matcher matcher = getMatcher(regex, sourceCode);
		if(matcher.find()) {
			matchInfo = matcher.group(1);
		}
		return matchInfo;
	}

	/**
	 * 获取匹配的正则表达式信息，只匹配一条信息
	 * @param sourceCode	
	 * @param regex
	 * @return
	 */
	public static String getMatchInfoSingle(String sourceCode, String regex, int group) {
		String matchInfo = null;
		Matcher matcher = getMatcher(regex, sourceCode);
		if(matcher.find()) {
			matchInfo = matcher.group(group);
		}
		return matchInfo;
	}

}
