package pub.willow.a.taskservice.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class WebpageUtil {
	
	/**
	 * 获取网页的编码
	 * @param sourceCode	网页源码
	 * @return	网页编码
	 */
	public static String getWebpageCharset(String sourceCode) {
		String charset = null;
		Matcher matcher = RegexUtil.getMatcher("<meta[^>]*?charset=\"?([^\"]*+)\"", sourceCode);
		int i = 0;
		if(matcher.find()) {
			i++;
			charset = matcher.group(1);
		}
		// 如果页面中的编码多于1个，那么把编码置为空，有httpClient获取网页编码
		if(i > 1) {
			charset = null;
		}
		return charset;
	}
	
	/**
	 * 改变css的路径是绝对路径
	 * @param url	url
	 * @param sourceCode	网页源码
	 * @return	改变css的路径是绝对路径的网页源码
	 */
	public static String changeCssAbsolutePath(String url, String sourceCode) {
		sourceCode = replaceRegex("<link[^>]*?href=\"([^\"]*?)\"\\s*?[^>]*?type=\"?text/css\"?[^>]*?>", url, sourceCode);
		sourceCode = replaceRegex("<link[^>]*?type=\"?text/css\"?\\s*?[^>]*?href=\"([^\"]*?)\"[^>]*?>", url, sourceCode);
		
		sourceCode = replaceRegex("<link[^>]*?rel=\"?stylesheet\"?[^>]*?href=\"([^\"]*?)\"[^>]*?>", url, sourceCode);
		sourceCode = replaceRegex("<link[^>]*?href=\"([^\"]*?)\"[^>]*?rel=\"?stylesheet\"?[^>]*?>", url, sourceCode);
		return sourceCode;
	}
	
	/**
	 * 用新的字符串替换正则表达式，被替换部分是新的字符串加上正则表达式的字符串
	 * @param regex	要替换的正则表达式
	 * @param replaceStr	需要替换的新的字符串	
	 * @param sourceCode	需要替换的文本
	 * @return	替换后的文本
	 */
	public static String replaceRegex(String regex, String replaceStr, String sourceCode) {
		StringBuffer sourceCodeBuf = new StringBuffer(sourceCode);
		Matcher matcher = RegexUtil.getMatcher(regex, sourceCode);
		String cssPath = null;
		String newUrl = replaceStr;
		List<String> matchList = new ArrayList<String>();
		while(matcher.find()) {
			cssPath = matcher.group(1).trim();
			if(cssPath.startsWith("//")) {
				// 缺少协议，如http:
				newUrl = getUrlProcotol(replaceStr); 
			} else if(cssPath.startsWith("/")) {
				// 相对根路径
				newUrl = getTopLevelDomain(replaceStr);
			} else if(cssPath.startsWith("./") || cssPath.startsWith("../")) {
				// 相对根路径
				newUrl = getTopLevelDomain(replaceStr);
			} else {
				// 相对上一层路径
				newUrl = getUpLevelDomain(replaceStr);
			}
			int index = 0;
			if(cssPath.indexOf("://") == -1 && !matchList.contains(cssPath) && !cssPath.equals("")) {
				matchList.add(cssPath);
				while(true) {
					index = sourceCodeBuf.indexOf(cssPath, index);
					if(index == -1) {
						break;
					} else {
						if(cssPath.startsWith("./")) {
							sourceCodeBuf.deleteCharAt(index);
							cssPath = cssPath.substring(1, cssPath.length());
						} else if(cssPath.startsWith("../")) {
							sourceCodeBuf.deleteCharAt(index);
							sourceCodeBuf.deleteCharAt(index);
							cssPath = cssPath.substring(2, cssPath.length());
						}
						sourceCodeBuf.insert(index, newUrl);
						index = index + cssPath.length() + newUrl.length();
					}
				}
			}

		}
		
		return sourceCodeBuf.toString();
	}
	
	/**
	 * 改变image的路径是绝对路径
	 * @param url	url
	 * @param sourceCode	网页源码
	 * @return	改变image的路径是绝对路径的网页源码
	 */
	public static String changeImageAbsolutePath(String url, String sourceCode) {
		sourceCode = replaceRegex("<img[^>]*?src=\"([^\"]*?)\"[^>]*?>", url, sourceCode);
		return sourceCode;
	}
	
	/**
	 * 获取url的一级域名部分
	 * @param url	完整的url
	 * @return	一级域名
	 */
	public static String getTopLevelDomain(String url) {
		Matcher matcher = RegexUtil.getMatcher("(.*?://.*?/).*+", url);
		if(matcher.find()) {
			url = matcher.group(1);
		}
		
		return url;
	}
	
	public static String getCurrentDomain(String url) {
		return url;
	}
	
	/**
	 * 获取url的协议，如http://www.baidu.com，返回结果是http:
	 * @param url
	 * @return
	 */
	public static String getUrlProcotol(String url) {
		Matcher matcher = RegexUtil.getMatcher("(.*?)//", url);
		if(matcher.find()) {
			url = matcher.group(1);
		}
		
		return url;
	}
	
	/**
	 * 获取url的相对路径，上一层的url
	 * @param url	完整的url
	 * @return	上一层的url
	 */
	public static String getUpLevelDomain(String url) {
		String tempUrl = url.substring(0, url.lastIndexOf("/")+1);
		if(tempUrl.equals("http://")) {
			tempUrl = url + "/";
		} 
		return tempUrl;
	}
	
	/**
	 * 获取相对路径
	 * @param url
	 * @return
	 */
	public static String getRelativeDomain(String url) {
		if(url.lastIndexOf("/") == url.length()-1) {
			url = url.substring(0, url.lastIndexOf("/")); 
			url = url.substring(0, url.lastIndexOf("/")+1); 
		} else {
			url = url.substring(0, url.lastIndexOf("/"));
			url = url.substring(0, url.lastIndexOf("/")+1);
		}
		
		return url;
	}
	
	/**
	 * 获取网页body标签内的内容，包含body标签
	 * @param sourceCode
	 * @return
	 */
	public static String getBodyHtmlContainBody(String sourceCode) {
		String bodyHtml = null;
		Matcher matcher = RegexUtil.getMatcher("(<body.*?</body>)", sourceCode);
		if(matcher.find()) {
			bodyHtml = matcher.group(1);
		}
		
		if(bodyHtml == null) {
			matcher = RegexUtil.getMatcher("(<body.*+)", sourceCode);
			if(matcher.find()) {
				bodyHtml = matcher.group(1);
			}
		}
		
		if(bodyHtml == null)
			bodyHtml = sourceCode;
		
		return bodyHtml;
	}
	
	/**
	 * 获取网页body标签内的内容
	 * @param sourceCode
	 * @return
	 */
	public static String getBodyHtml(String sourceCode) {
		String bodyHtml = null;
		Matcher matcher = RegexUtil.getMatcher("<body(.*?)</body>", sourceCode);
		if(matcher.find()) {
			bodyHtml = matcher.group(1);
		}
		return bodyHtml;
	}
	
	/**
	 * 获取html源码中的某个标签内的html内容
	 * @param sourceCode	源码
	 * @param tag	标签
	 * @return	标签内的内容的列表
	 */
	public static List<String> getTagList(String sourceCode, String tag) {
		List<String> tagList = new ArrayList<String>();
		Matcher matcher = RegexUtil.getMatcher("<"+tag+".*?>(.*?)"+"</"+tag+">", sourceCode);
		while(matcher.find()) {
			tagList.add(matcher.group(1));
		}
		return tagList;
	}
	
	public static void main(String[] args) {
		String url = "?cat=54220008&s=60&q=%D7%D4%D0%D0%B3%B5&sort=new&style=g&tmhkmain=0&type=pc#J_Filter";
		String originalUrl = "https://list.tmall.com/search_product.htm?q=%D7%D4%D0%D0%B3%B5&sort=new";
//		System.out.println(originalUrl.indexOf("list.tmall.com/search_product.htm")>-1);
		
//		else if(url.startsWith("?cat=") && originalUrl.indexOf("list.tmall.com/search_product.htm")>-1)
		
		String crawlingUrl = WebpageUtil.getWholeUrl(url, originalUrl);
		System.out.println(crawlingUrl);
//		String time = "6&nbsp;小时前";
//		System.out.println(escapeChar(time));
	}
	
	/**
	 * 转义网页源码中的字符
	 * @param sourceCode	网页源码
	 * @return	转义后的网页源码
	 */
	public static String escapeChar(String sourceCode) {
		if(sourceCode == null)
			return null;
		
		// 转义空格
		sourceCode = sourceCode.replaceAll("&nbsp;", " ");
		// 转义中文双引号“
		sourceCode = sourceCode.replaceAll("&ldquo;", "“");
		// 转义中文双引号”
		sourceCode = sourceCode.replaceAll("&rdquo;", "”");
		// 转义和&
		sourceCode = sourceCode.replaceAll("&amp;", "&");
		
		sourceCode = sourceCode.replaceAll("&lt;", "<");
		sourceCode = sourceCode.replaceAll("&gt;", ">");
		return sourceCode;
	}
	
	/**
	 * 过滤javascript代码
	 * @param sourceCode
	 * @return
	 */
	public static String filterJavaScript(String sourceCode) {
		sourceCode = RegexUtil.getMatcher("(<script[^>]*?type=[\"\']?text/javascript[\"\']?[^>]*?>.*?<[^>]*?/script>)", sourceCode).replaceAll("");
		sourceCode = RegexUtil.getMatcher("(<script[^>]*?src=[\"\']?.*?[\"\']?[^>]*?>.*?<[^>]*?/script>)", sourceCode).replaceAll("");
		sourceCode = RegexUtil.getMatcher("<script[^>]*?>(.*?)<[^>]*?/script>", sourceCode).replaceAll("");
		
		sourceCode = RegexUtil.getMatcher("(<br.*?>)", sourceCode).replaceAll("<br/> ");
		
		return sourceCode;
	}
	
	public static String filterTbody(String sourceCode, String firstTopicSourceCode) {
		if(firstTopicSourceCode.toLowerCase().indexOf("<tbody>") != -1 && sourceCode.toLowerCase().indexOf("<tbody>") == -1) {
			// 如果首贴有tbody而网页源码没有他body，那么首贴删除tbody
			firstTopicSourceCode = RegexUtil.getMatcher("<tbody>", firstTopicSourceCode).replaceAll("");
		}
		
		return firstTopicSourceCode;
	}
	
	/**
	 * 过滤标签
	 * @param sourceCode	源码
	 * @return
	 */
	public static String filterTag(String sourceCode) {
		if(sourceCode == null)
			return null;
		
		Matcher matcher = RegexUtil.getMatcher("(<.*?>)", sourceCode);
		while(matcher.find()) {
			sourceCode = sourceCode.replace(matcher.group(1), "");
		}
		sourceCode = sourceCode.replaceAll("&nbsp;", " ");
		sourceCode = sourceCode.replaceAll("&lt;", "<");
		sourceCode = sourceCode.replaceAll("&gt;", ">");
		sourceCode = sourceCode.trim();
		return sourceCode;
	}
	
	public static String filterMessyCode(String sourceCode) {
		if(sourceCode == null)
			return null;
		
		sourceCode = sourceCode.replaceAll("�", "");
		sourceCode = sourceCode.replaceAll("👌", "");
		sourceCode = sourceCode.trim();
		return sourceCode;
	}
	
	public static String getWholeUrl(String url, String originalUrl) {
		String newUrl = ""; 
		if(originalUrl.indexOf("weixin.sogou.com/weixin")>-1 && !url.startsWith("http://") && url.indexOf("websearch")>-1){
			newUrl = "http://weixin.sogou.com"+url;
		} else if(originalUrl.indexOf("weixin.sogou.com/weixin")>-1 && !url.startsWith("http://") && url.indexOf("websearch")==-1){
			newUrl = "http://weixin.sogou.com/weixin"+url;
		} else if(originalUrl.indexOf("sogou.com/web")>-1 && !url.startsWith("http://")){
			newUrl = "http://www.sogou.com/web"+url;
		} else if(originalUrl.indexOf("list.tmall.com/search_product.htm")>-1 && !url.startsWith("//detail.tmall.com")){
			newUrl = "https://list.tmall.com/search_product.htm"+url;
		} else if(url.startsWith("//")) {
			// 缺少协议，如http:
			newUrl = getUrlProcotol(originalUrl) + url;
		} else if(url.startsWith("/")) {
			// 相对根路径
			newUrl = getTopLevelDomain(originalUrl) + url;
		} else if(url.indexOf("://") != -1) {
			// 有完整路径
			newUrl = url;
		} else if(url.startsWith("../")) {
			// 相对路径
			newUrl = getRelativeDomain(originalUrl) + url.substring(3, url.length());
		} else {
			// 相对上一层路径
			newUrl = getUpLevelDomain(originalUrl) + url;
		} 
		newUrl = newUrl.replaceAll("//", "/");
		newUrl = newUrl.replaceAll("http:/", "http://");
		newUrl = newUrl.replaceAll("https:/", "https://");
		return newUrl;
	}
	
	public static String filterHtml(String sourceCode) {
		String matcherStr = RegexUtil.getMatchInfoSingle(sourceCode, "<html(.*?)>");
		if(matcherStr == null)
			return sourceCode;
		
		sourceCode = sourceCode.replace(matcherStr, "");
		
		matcherStr = RegexUtil.getMatchInfoSingle(sourceCode, "(.*?)<html>");
		if(matcherStr != null)
			sourceCode = sourceCode.replace(matcherStr, "");
		
		return sourceCode;
	} 
	
	public static String filterHtmlTags(String sourceCode) {
		if(sourceCode == null)
			return sourceCode;
		
		String html = sourceCode.replaceAll("(<[^>]*?>)", "").trim();
		return html;
	}
	
}
