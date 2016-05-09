/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/05/2016.
 */

package cm.aptoide.pt.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by neuro on 14-04-2016.
 */
public class MathUtils {

	public static int greatestCommonDivisor(int a, int b) {
		while (b > 0) {
			int temp = b;
			b = a % b; // % is remainder
			a = temp;
		}
		return a;
	}

	public static int leastCommonMultiple(int a, int b) {
		return a * (b / greatestCommonDivisor(a, b));
	}

	public static int leastCommonMultiple(int[] input) {
		int result = input[0];
		for (int i = 1; i < input.length; i++) result = leastCommonMultiple(result, input[i]);
		return result;
	}

	public static String computeHmacSha1(String value, String keyString) throws
			InvalidKeyException, IllegalStateException, UnsupportedEncodingException,
			NoSuchAlgorithmException {
		System.out.println(value);
		System.out.println(keyString);
		SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(key);

		byte[] bytes = mac.doFinal(value.getBytes("UTF-8"));

		return convToHex(bytes);
	}

	private static String convToHex(byte[] data) {
//		StringBuilder buf = new StringBuilder();
//		for (int i = 0; i < data.length; i++) {
//			int halfbyte = (data[i] >>> 4) & 0x0F;
//			int two_halfs = 0;
//			do {
//				if ((0 <= halfbyte) && (halfbyte <= 9)) buf.append((char) ('0' + halfbyte));
//				else buf.append((char) ('a' + (halfbyte - 10)));
//				halfbyte = data[i] & 0x0F;
//			} while (two_halfs++ < 1);
//		}
//		return buf.toString();

		final StringBuilder buffer = new StringBuilder();
		for (byte b : data) {
			buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}
		return buffer.toString();
	}

	public static String computeSHA1sum(String text) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		byte[] sha1hash = md.digest();
		return convToHex(sha1hash);
	}
}
