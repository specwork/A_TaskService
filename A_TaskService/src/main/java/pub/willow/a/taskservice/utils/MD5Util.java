package pub.willow.a.taskservice.utils;

import java.security.MessageDigest;

/**
MD5加密工具
*/
public class MD5Util {

	/**
	 @roseuid 492E5CBE0280
	 */
	public MD5Util() {

	}

	public final static String MD5(String s) throws Exception {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
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

	public static void main(String[] args) throws Exception {
		System.out.println(MD5Util.MD5("iphone_dev_123456"));
		System.out.println();
		//System.out.println(MD5Util.MD5("111111_111111_uniebiz"));
		//System.out.println(MD5Util.MD5("111111"+"_"+1+"_uniebiz"));
//		System.out.println(MD5Util.MD5("36"+"|"+"1224829773"));
	}
}
