package pub.willow.a.taskservice.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

/**
 * 字符串处理工具类
 * @author kimfu
 *
 */
public class StringUtil {
	
	/**
	 * 生成随即密码
	 * 
	 * @param pwd_len
	 *            生成的密码的总长度
	 * @return 密码的字符串
	 */
	public static String getRandomNum(int pwd_len) {
		// 35是因为数组是从0开始的，26个字母+10个数字
		final int maxNum = 36;
		int i; // 生成的随机数
		int count = 0; // 生成的密码的长度
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
				't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		while (count < pwd_len) {
			// 生成随机数，取绝对值，防止生成负数，
			i = Math.abs(r.nextInt(maxNum));
			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}
		return pwd.toString();
	}
	
	/**
	 * 字符串加密算法
	 * @param str - 请求的地址，格式为：mediaUserId=xxx&tweetId=xxx&mediaType=xxx
	 * @param key - 密钥：目前暂定为:iwomcic
	 * @return string - 加密之后的字符串
	 * */
	public static String getSign(String str, String key){
		String result = "";
		try {
			String doKey = str + "&" + key;
			result = MD5Util.MD5(doKey);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 去除字符串特殊字符
	 * */
	public static String replaceString(String value) {
		if (value != null) {
			value = value.replace("", "");
			
			StringBuilder newString = new StringBuilder();
			char ch;
			for (int i = 0; i < value.length(); i++) {
				ch = value.charAt(i);
				if ((ch >= 0x001E && ch <= 0x00C0) || (ch >= 0xFF00 && ch <= 0xFF5E) 
						|| (ch >= 0x2460 && ch <= 0x24E9) || (ch >= 0x2012 && ch <= 0x2032)
						|| (ch <= 0x9FA5 && ch >= 0x4E00) || (ch == 0xffe5)
						|| (ch <= 0x301A && ch >= 0x3001) || (ch <= 0x33FF && ch >= 0x3200)) {
					newString.append(ch);
				}
			}
			value = newString.toString();
			value=value.replaceAll("[\\x00-\\x09\\x11\\x12\\x14-\\x1F\\x7F]", ""); 
		}
		return value;
	}
	
	

	/**
	 * 将字符串转成int
	 * 
	 * @param str
	 *            - 转换的字符串
	 * @param def
	 *            - 默认或出现情况下的值
	 * @return
	 */
	public static int convertStr(String str, int def) {
		try {
			
			if (str == null || str.equals("") || str.equals("null") || str.equals("0") || str.equals("undefined"))
				return def;
			return Integer.parseInt(str.trim());
		} catch (Exception e) {
			return def;
		}
	}
	
	/**
	 * 将字符串转成float类型
	 */
	public static float convertStrToFloat(String str,float def){
		try {
			if (str == null || str.equals("") || str.equals("null") || str.equals("0") || str.equals("undefined"))
				return def;
			return Float.parseFloat(str.trim());
		} catch (Exception e) {
			return def;
		}
	}

	/**
	 * 将字符串转成long型
	 * 
	 * @param str
	 *            - 转换的字符串
	 * @param def
	 *            - 默认或出现情况下的值
	 * @return
	 */
	public static long convertStrToLong(String str, long def) {
		try {
			if (str == null || str.equals("null") || str.equals("0") || str.equals("undefined") || "".equals(str.trim())) {
				return def;
			}
			return Long.parseLong(str.trim());
		} catch (Exception e) {
			return def;
		}
	}

	/**
	 * 将以逗号分隔的字符穿两边加上 ''; 比如 aaa,bbbb 返回 'aaa','bbb'
	 * 
	 * @param str
	 *            - 转换的字符串
	 * @param def
	 *            -
	 * @return
	 */
	public static String convertStr4Sql(String str) {
		try {
			if (str == null || str.equals("0"))
				return str;
			String idsTem = "";
			if (str.contains(",")) {
				for (String idStr : str.split(",")) {
					idsTem += "'" + idStr + "',";
				}
				if (idsTem.length() > 0)
					idsTem = idsTem.substring(0, idsTem.length() - 1);
			} else {
				idsTem = "'" + str + "'";
			}
			return idsTem;
		} catch (Exception e) {
			return str;
		}
	}

	public static String formatNum(Object obj) {
		DecimalFormat df = new DecimalFormat("###,###,###.##");
		return df.format(Double.valueOf(StringUtil.rep(obj, "0")));
	}

	public static String rep(Object s, String mark) {
		if (s == null || s.equals("") || s.equals("null") || s.equals("undefined"))
			return mark;
		if (s instanceof String) {
			String new_name = (String) s;
			if (new_name.equals(""))
				return mark;

			return new_name.trim();
		}
		return s + "";
	}

	public static String rep(Object s) {
		
		if (s == null || s.equals("") || s.equals("null") || s.equals("undefined"))
			return "";
		if (s instanceof String) {
			String new_name = (String) s;
			// 长字符串替换 暂时不使用
			// new_name = StringUtil.longStringFomrat(new_name);
			return new_name.trim();
		}
		return s + "";
	}

	/**
	 * 获取支付传值:aaa=bbb|ccc=ddd
	 * 
	 * @param name
	 *            要获取的字段名称。
	 * @return
	 */
	public static String getPara(String src, String name) {
		src = "|" + src + "|";
		int begin = src.indexOf("|" + name + "=");
		if (begin > -1) {
			int end = src.indexOf("|", begin + 2);
			return src.substring(begin + 2 + name.length(), end);
		}
		return "";
	}

	/**
	 * 将原有list中含有oldKey的map 替换其key为newKey param dataList 旧list oldKey需要转换的key newKey 转后后的key
	 * 
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List changeList(List<Map> dataList, String oldKey, String newKey) {
		if (oldKey == null || newKey == null || oldKey.equals(newKey))
			return dataList; // 不需要转列(名称为空，或相等)
		if (dataList == null || dataList.size() <= 0)
			return dataList;
		ArrayList newList = new ArrayList();
		for (Map object : dataList) {
			Map newMap = new HashMap();
			Iterator it = object.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
				if (oldKey.equalsIgnoreCase(String.valueOf(key))) {
					newMap.put(newKey, value);
					newMap.put(oldKey, value);
				} else {
					newMap.put(key, value);
				}
			}
			newList.add(newMap);
		}
		return newList;
	}

	/**
	 * Pie图存在数据类型的判断
	 * 
	 * @param dataList
	 *            - 需要转发的list
	 * @param oldKey
	 *            - 需要查找的key
	 * @param newKey
	 *            - 替换后的新key
	 * @param sign
	 *            - 1:int,2:String,3:object
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List changeList(List<Map> dataList, String oldKey, String newKey, int sign) {
		if (oldKey == null || newKey == null || oldKey.equals(newKey))
			return dataList; // 不需要转列(名称为空，或相等)
		ArrayList newList = new ArrayList();
		for (Map object : dataList) {
			Map newMap = new HashMap();
			Iterator it = object.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
				if (oldKey.equalsIgnoreCase(String.valueOf(key))) {
					if (sign == 1) {
						newMap.put(newKey, StringUtil.convertStr(value + "", 0));
						newMap.put(oldKey, StringUtil.convertStr(value + "", 0));
					}
					if (sign == 2) {
						newMap.put(newKey, value + "");
						newMap.put(oldKey, value + "");
					}
					if (sign == 3) {
						newMap.put(newKey, value);
						newMap.put(oldKey, value);
					}
				} else {
					newMap.put(key, value);
				}
			}
			newList.add(newMap);
		}
		return newList;
	}

	/**
	 * 将数据补入list中
	 * 
	 * @param list
	 *            旧list
	 * @param id
	 *            map的key
	 * @param value
	 *            map的Value
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List put(List list, String id, String value) {
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map map = (Map) it.next();
			map.put(id, value);
		}
		return list;
	}

	/**
	 * 获得小列表不在大列表中的结果集合
	 * 
	 * @param smallList
	 *            - 小列表，使用,分隔
	 * @param bigList
	 *            - 大列表，使用,分隔
	 * @return string
	 */
	public static String findOtherList(String smallList, String bigList) {
		if (bigList == null || bigList.equals(""))
			return null;
		if (smallList == null || smallList.equals(""))
			return null;
		if (bigList.equals(smallList))
			return null;
		smallList = "," + smallList + ",";
		String[] bigs = bigList.split(",");
		String otherStr = "";
		for (String str : bigs) {
			String tmp = "," + str + ",";
			if (!smallList.contains(tmp))
				otherStr += str + ",";
		}
		if (otherStr.length() > 1)
			otherStr = otherStr.substring(0, otherStr.length() - 1);
		return otherStr;
	}

	/**
	 * 对list<map>这种数据结构 进行排序
	 * @param list
	 *            旧list
	 * @param key
	 *            map的key 按照此key对应的value进行排序
	 * @return 排序后的List
	 */
	public static List<Map<String, Integer>> sortListAsc(List<Map<String, Integer>> list, final String key) {
		Comparator<Map<String, Integer>> comparator = new Comparator<Map<String, Integer>>() {
			public int compare(Map<String, Integer> o1, Map<String, Integer> o2) {
				return StringUtil.convertStr((o1.get(key) + ""), 0) - StringUtil.convertStr((o2.get(key) + ""), 0);
			}
		};
		Collections.sort(list, comparator);
		return list;
	}

	/**
	 * 对list<map>这种数据结构 进行排序
	 * 
	 * @param list
	 *            旧list
	 * @param key
	 *            map的key 按照此key对应的value进行排序
	 * @return 排序后的List
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Map<String, Integer>> sortListDesc(List<Map<String, Integer>> list, final String key) {
		Map otherMap = new HashMap();
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			if (map.get("TYPE_ID").equals("其他")) {
				otherMap = map;
				break;
			}
		}
		list.remove(otherMap); // 剔除其他数据
		Comparator<Map> comparator = new Comparator<Map>() {
			public int compare(Map o1, Map o2) {
				return Integer.parseInt(o2.get(key) + "") - Integer.parseInt((o1.get(key)) + "");
			}
		};
		Collections.sort(list, comparator);
		if (otherMap.size() > 0)
			list.add(otherMap); // 追加其他数据到最下面
		return list;
	}

	/**
	 * 过滤所有的html标签
	 * */
	public static String htmlAllEncode(String value) {
		if(value==null || value.trim().equals("") || value.trim().equalsIgnoreCase("null"))
			return "";

		value = value.replaceAll("<table.*?>|</table>", "");
		value = value.replaceAll("<tr.*?>|</tr>", "");
		value = value.replaceAll("<td.*?>|</td>", "");
		value = value.replaceAll("<div.*?>|</div>", "");
		value = value.replaceAll("<span.*?>|</span>", "");
		value = value.replaceAll("<iframe.*?>|</iframe>", "");
		value = value.replaceAll("<input.*?>", "");
		value = value.replaceAll("<iframe", "");
		value = value.replaceAll("&nbsp;", " ");
		value = value.replaceAll(";", "");
		value = value.replaceAll("&", "&amp;");
		return value;
	}

	/**
	 * 截取字符串 中文的长度2 英文为1
	 * 
	 * @param title
	 *            旧title
	 * @param length
	 *            截取后的长度
	 * @return 排序后的List
	 * */
	public static String subString4Title(String title, int length) throws Exception {
		if(title==null || title.trim().equals("") || title.trim().equalsIgnoreCase("null"))
			return "...";
		title = htmlAllEncode(title);

		byte[] bytes = title.getBytes("Unicode");
		int n = 0;
		int i = 2;
		for (; i < bytes.length && n < length; i++) {
			if (i % 2 == 1)
				n++;
			else {
				if (bytes[i] != 0)
					n++;
			}
		}

		if (i % 2 == 1) {
			if (bytes[i - 1] != 0)
				i = i - 1;
			else
				i = i + 1;
		}
		return new String(bytes, 0, i, "Unicode");
	}

	public static String subString(Object src1, int byteline) {

		if (src1 == null || "".equals(src1))
			return "";
		String s = src1.toString();
		StringBuffer sb = new StringBuffer(s);
		int leng = 0;
		for (int i = 0; i < s.length(); i++) {
			String c = sb.substring(i, i + 1);
			if (c.getBytes().length > 1) {
				leng = leng + 2;
			} else {
				leng = leng + 1;
			}

			if (leng >= byteline) {
				return sb.substring(0, i + 1) + "...";
			}
		}
		return s;
	}

	/** 对list进行深复制 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Map> deepClone(List<Map> result) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(outStream);
		out.writeObject(result);

		ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
		ObjectInputStream in = new ObjectInputStream(inStream);
		List<Map> retList = (List<Map>) in.readObject();

		return retList;
	}

	/**
	 * 替换特殊码
	 * 
	 * @param str
	 *            - 需要替换的文字
	 * @param re
	 *            - 替换规则
	 * @return str - 替换后的文字
	 */
	public static String transSpe(String str, String[] re) {
		if (re == null || re.length <= 0)
			re = new String[] { "&,~!@", "+,~@$" }; // 默认为:&替换成~!@,+替换成~@$
		for (int i = 0; i < re.length; i++) {
			String a = re[i].split(",")[0];
			String b = re[i].split(",")[1];
			str = str.replace(b, a);
		}
		return str;
	}

	public static Date getDate(long unixDate) {
		try {
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			String strDate = sd.format(new Date(unixDate * 1000L));
			return sd.parse(strDate);
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 
	 * @param temp
	 *            需要过滤的正则表达式
	 * @param searchKey
	 *            需要过滤的词
	 * @return
	 */
	public static boolean getSearchKey(String temp, String searchKey) {
		Pattern pattern = Pattern.compile("^.*[" + temp + "].*");
		Matcher matcher = pattern.matcher(searchKey);
		return matcher.matches();
	}

	/**
	 * 获得客户端ip
	 * 
	 * */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");// 当使用反向代理的时候 通过此获取ip
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP"); // 当使用反向代理的时候 通过此获取ip
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr(); // 正常情况下获取Ip
		}
		return ip;
	}
	
	/**
	 * 获得服务器端IP地址
	 */
	public static String getHostAddr(){
		try {
			InetAddress localIP = InetAddress.getLocalHost();
			return localIP.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得合法xml数据
	 * 
	 * @param str
	 * @return str
	 */
	public static String getValidXml(String str) {
		if (str == null || str.equals("") || str.equalsIgnoreCase("null"))
			return "";
		return "<![CDATA[" + str + "]]>";
	}

	/**
	 * 转换各种浏览器支持的文件下载名称
	 * 
	 * @param fileName
	 *            - 需要转换的文件名
	 * @param agent
	 *            - 浏览器代理商
	 * @return java.lang.String - 转换之后的文件名
	 */
	public static String getDownFile(String fileName, String agent) {
		try {
			if (agent != null) {
				if (agent.indexOf("MSIE") >= 0) { // IE类浏览器
					fileName = new String(fileName.getBytes("gb2312"), "ISO-8859-1");
				}
				if (agent.indexOf("Mozilla") >= 0 && agent.indexOf("Chrome") < 0 && agent.indexOf("MSIE") < 0) { // FF浏览器
					fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
					fileName = "=?UTF-8?B?" + (new String(Base64.encodeBase64(fileName.getBytes("ISO-8859-1")))) + "?=";
				}
				if (agent.indexOf("Chrome") >= 0) { // Chorom浏览器
					fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
				}
			}
		} catch (Exception e) {
		}
		return fileName;
	}

	/**
	 * 字符高亮
	 * 
	 * @param keys
	 * @param text
	 * @return
	 */
	public static String getReplayKeys(String keys, String text) {
		if (keys == null || keys.equals("") || text == null || text.equals(""))
			return text;
		String[] key = keys.split(",");
		for (int i = 0; i < key.length; i++) {
			String myKey = key[i];
			if (myKey == null || myKey.equals(""))
				continue;
			text = text.replaceAll(key[i], "<span class='color_3'>" + key[i] + "</span>");
		}
		return text;
	}

	public static void main(String[] args) throws Exception {
		String name = "null" ;
		System.out.println("2--->"+StringUtil.rep(name, "")) ;
//		String text= "據記載" ;
//		System.out.println( Jsoup.parse(html))  ;
//		System.out.println( Jsoup.parse(text).text())  ;
//		System.out.println(StringUtil.replaceString("💥"));
		// String para="para=|userIds=1652752642,1770318172|indexType=NUM_OF_TWEETS|freq=week|dateId=2010-11-30";
		// System.out.print
		// System.out.println(getPara(para,"userIds"));
		// Date myDate = StringUtil.getDate(1297673149);
		// System.out.println(StringUtil.getSearchKey("中国|人", "")) ;
		// SimpleDateFormat fm2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		// System.out.println(myDate.toLocaleString());
		// System.out.println(myDate.toLocaleString());
		// System.out.println(fm2.format(myDate));
		// SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// sd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		// String strDate = sd.format(new Date(1297673149*1000L));
		// System.out.println("正八区当前时间:"+strDate);

		// System.out.println(""+new java.text.SimpleDateFormat("yyyy MM-dd HH:mm:ss").format(new java.util.Date
		// (1297673149*1000L)));
		// String timezone_info = System.getProperty("user.timezone");
		// System.out.println("当前的时区:"+timezone_info);
		// System.out.println("时区信息:"+TimeZone.getDefault());
		// String smallStr = "3,5,7";
		// String bigStr = "1,2,3,4,5,6,7";
		// String otherStr = StringUtil.findOtherList(smallStr, bigStr);
		// System.out.print(otherStr);
		// String str = "中国";
		// str = new String(str.getBytes("utf-8"),"ISO-8859-1");
		// System.out.println(str);
		// String str1 =URLEncoder.encode(str,"utf-8");
		// System.out.println(str1);

//		String sign = "clientid=10283&keywords=china&media=1&key=12345678"; // 331caff5b9bf29f54debebc8d659c198
		// 331caff5b9bf29f54debebc8d659c198
//		System.out.println(StringUtil.MD5(sign));
//		int num = 1413;
//		int length = 11;
//		String str = StringUtil.makeIntToString(num, length);
//		System.out.println(str);
//		num = 1313144143;
//		length = 9;
//		str = StringUtil.makeIntToString(num, length);
//		System.out.println(str);
//		String str = "/(00)\\";
//		System.out.println(StringUtil.getValidContent(str));
//		str = str.replace("\\", "");
//		String str = "♪秦小岚😘๓小米『米娅美妆』适合初学者的完美的翅膀眼线教程||『米娅美妆』适合初学者的完美的翅膀眼线教程" ;
//		System.out.println(StringFilter(str));
//		String docId = "2014102800000004493";
//		int folderId = 1413;
//		String hId = StringUtil.genernateHID(docId, folderId);
//		System.out.println(hId);
	}

	public static String percent(double p1, double p2) {
		String str = "";
		double p3 = p1 / p2;
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(2);
		str = nf.format(p3);
		return str;
	}

	/**
	 * 获得最大或最小
	 */
	public int getRightNum(int sNum, int rtNum, int ctNum, int sign) {
		if (sign == 0) { // 比小
			if (sNum < rtNum) {
				if (sNum < ctNum)
					return sNum;
				else
					return ctNum;
			} else {
				if (rtNum < ctNum)
					return rtNum;
				else
					return ctNum;
			}
		}
		if (sign == 1) { // 比大
			if (sNum > rtNum) {
				if (sNum > ctNum)
					return sNum;
				else
					return ctNum;
			} else {
				if (rtNum > ctNum)
					return rtNum;
				else
					return ctNum;
			}
		}
		return sNum;
	}

	/**
	 * 补齐小时段/工作日数据
	 * 
	 * @param data
	 *            - 需要补齐的数据
	 * @param sign
	 *            - 需要补齐的类型,1:小时段,2:工作日
	 * @param scene
	 *            - 场景StatUserBar,StatUserBarLine
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List addOther(List data, int sign, String scene) {
		if (data == null || data.size() <= 0)
			return data;
		int maxLeng = 0;
		String key = "";
		int start = 0;
		if (sign == 1) {
			maxLeng = 24;
			if (data.size() == maxLeng)
				return data;
			key = "HOUR_ID";
		} else if (sign == 2) {
			maxLeng = 8;
			start = 1;
			if (data.size() == maxLeng)
				return data;
			if (scene != null && scene.trim().equals("statUserBarLine"))
				key = "WEKDAY_ID";
			if (scene != null && scene.trim().equals("statUserBar"))
				key = "WORKDAY_ID";
		} else if (sign == 3) { // 补齐粉丝数分布
			maxLeng = 13;
			key = "TYPE";
		}

		for (int i = start; i < maxLeng; i++) {
			boolean ret = false;
			for (int j = 0; j < data.size(); j++) {
				Map map = (Map) data.get(j);
				int t = StringUtil.convertStr(map.get(key) + "", 0);
				if (i == t) {
					ret = true;
					break;
				}
			}
			if (!ret) {
				Map map = new HashMap();
				// map.put("Post", 0);
				map.put(key, i);
				// map.put("Thread", 0);
				if (start == 1)
					data.add(i - 1, map); // 每工作日
				else
					data.add(i, map); // 每小时
			}
		}
		return data;
	}

	/**
	 * 获得合法的SQL文本
	 */
	public static String getValidSQL(String str) {
		if (str == null || str.trim().equals(""))
			return "";
		str = str.replace("'", "");
		return str;
	}

	/**
	 * 替换"问题
	 */
	public static String getValidContent(String str) {
		if (str == null || str.trim().equals(""))
			return "";
		str = str.replace("\"", "");
		String reg = "[\n-\r]";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		str = m.replaceAll("");
		return str;
	}

	/**
	 * MD5 加密
	 * 
	 * @param s
	 *            - 需要加密的字符串
	 * @return
	 * @throws Exception
	 */
	public static String MD5(String s) throws Exception {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	/**
	 * 判断是否为数字 
	 */
	public static boolean isInteger(String str){
		return StringUtils.isNumeric(str);
//		IsNumeric
//	     if(str==null )
//	      return false;
//	     Pattern pattern = Pattern.compile("[0-9]+");
//	     return pattern.matcher(str).matches();
	}
	
	/**
	 * 通过mediaType得到Type
	 * */
	public static String convertMediaToType(int mediaType) {
		String result = "";
		if (mediaType == 1) {
			result = "1,2,3,4,5,7,17,31";
		}
		if (mediaType == 11) {
			result = "12,15,31";
		}
		if (mediaType == 2) {
			result = "21,22,23,24,28,32";
		}
		if (mediaType == 12) {
			result = "25,26,32";
		}
		return result;
	}
	
	 /**
	  * 当前时间戳+6位随机数
	  * @return
	*/
	public static String getRandNum(){
		String ranNum = new Random().nextInt(999999)+"" ;
		String currentTimeMillis = System.currentTimeMillis()+"" ;
		return currentTimeMillis+ranNum ;
	}
	
	// 去除首尾逗号
	public static String removeHeadAndtail(String str){
		if((str!=null)&&(str.length()>0)){
			str=StringUtils.removeEndIgnoreCase(str,",");
			str=StringUtils.removeStartIgnoreCase(str,",");
//			if(str.startsWith(",")){
//				str = str.substring(1,str.length());
//			}
//			if(str.endsWith(",")){
//				str = str.substring(0,str.length()-1);
//			}
		}
		return str;
	}
	
	public static String getNewProcess(int i,int s){
		try{
			String str = "total->"+s+",now->"+i+",process->"+((float)i/(float)s)*100+"%";
			return str;
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static String toString(Object obj, String def){
		String result = def;
		if(obj!=null){
			result = obj.toString();
		}
		return result;
	}
	
	/**
	 * 将Object对象转成JSON数据
	 */
	public static String objToString(Object obj){
		if(obj==null) return null;
		net.sf.json.JSONObject jsonString = net.sf.json.JSONObject.fromObject(obj);
		String content=jsonString.toString();
		return content;
	}
	
	/**
	 * 读取文本字符串
	 */
	public static List<String> readFileByLines(String fileName) {
        File file = new File(fileName);
        if(file.isFile()){
	        BufferedReader reader = null;
	        List<String> list = new ArrayList<String>();
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            String tempString = null;
	            // 一次读入一行，直到读入null为文件结束
	            while ((tempString = reader.readLine()) != null) {
	            	list.add(tempString);
	            }
	            reader.close();
	            return list;
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
        }else return new ArrayList<String>();
    }
	
	/**
	 * 左补齐字符位数
	 * @param num - 需要处理的数据
	 * @param length - 需要补齐的长度
	 */
	public static String makeIntToString(long num,int length){
		String str = num+"";
		 return StringUtils.leftPad(str,length,"0");
		
		
//		if(str.length()>=length) return str;
//		int bit = length-str.length();
//		String zero = "";
//		for(int i=0;i<bit;i++){
//			zero+="0";
//		}
//		return zero+str;
	}
	
	/**
	 * 判断是否为数字
	 * @param str - 需要判断的字符串
	 * @return boolean - true:是数字,false:不是数字
	 */
	public static boolean isNumberic(String str){
		return StringUtils.isNumeric(str);
//		try{
//			Pattern pattern = Pattern.compile("[0-9]*"); 
//		    return pattern.matcher(str).matches();
//		}catch(Exception e){
//			return false;
//		}
	}
	
	/**
	 * 将DocId和FolderId进行组合，生产HBase rowKey
	 * docId:yyyyMMdd+11位数
	 * folderId:?位数
	 * @param docId - 文章编号
	 * @param folderId - 所属任务编号
	 * @return HID - HBase rowKey:yyyyMMdd+folderId+0?+remove(0)+docId
	 */
	public static String genernateHID(String docId,int folderId){
		if(docId==null || docId.trim().equals("") ||docId.trim().length()!=19 || folderId==0) return null;
		
		int maxLength = 19;					//生成HID的长度
		
		String yyyyMMdd = docId.substring(0,8);
		String thisDocId = docId.substring(9,docId.length());
		long myId = StringUtil.convertStrToLong(thisDocId, 0);
		thisDocId = myId+"";
		
		int length = thisDocId.length();
		String folderIds = folderId+"";
		int folderLength = folderIds.length();
		String zeros = "";
		for(int i =0;i<maxLength-8-length-folderLength;i++){
			zeros+="0";
		}
		String hId = yyyyMMdd+folderId+""+zeros+thisDocId;
		return hId;
	}
	
	public static String StringFilter(String str){     
	     String regEx="[『』😘๓♪`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";  
	     Pattern   p   =   Pattern.compile(regEx);     
	     Matcher   m   =   p.matcher(str);     
	     return   m.replaceAll("").trim();     
    }
	
	/**
	 * 获得某对象指定的属性的值
	 * @param obj - 数据对象
	 * @param attr - 查询的属性
	 * @reutrn obj - 获得的属性值
	 */
	public static Object findValByAttr(Object obj,String attr){
		if(obj==null ||attr==null ||attr.trim().equals("")){
			System.out.println("object && attr must fill in");
			return null;
		}
		
		Class myClass = (Class) obj.getClass();  					//得到对象
	    Field[] fs = myClass.getDeclaredFields();					//得到类中的所有属性集合
	    Object val = null;
	    for(Field field:fs){
	    	field.setAccessible(true);
	    	String fName = field.getName();							//获得属性名称
	    	if(fName.equalsIgnoreCase(attr)){						
	    		try {
					val = field.get(obj);
				} catch (Exception e) {
					e.printStackTrace();
				} 
	    		break;
	    	}
	    }
	    if(val==null) System.out.println("no this attr or meet exception");
	    return val;
	}
}
