package pub.willow.a.taskservice.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密类
 * @author albert.zhang
 *
 */
public class SecurityUtil {
	public static String MD5(String data) {
		return encrypt(data, "MD5");
	}

	public static String SHA1(String data) {
		return encrypt(data, "SHA-1");
	}

	public static String encrypt(String data, String algorithm) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		byte[] md5b = md.digest(data.getBytes());
		return bytes2Hex(md5b);
	}

	private static String bytes2Hex(byte[] b) {
		char[] digest = "0123456789abcdef".toCharArray();
		char[] c = new char[b.length * 2];
		for (int i = 0; i < b.length; i++) {
			byte b1 = b[i];
			c[(2 * i)] = digest[((b1 & 0xF0) >> 4)];
			c[(2 * i + 1)] = digest[(b1 & 0xF)];
		}
		return new String(c);
	}

	public static void main(String[] args) {
		System.out.println(encrypt("http://bbs.tianya.cn/list-free-1.shtml", "SHA-1"));
	}
}
