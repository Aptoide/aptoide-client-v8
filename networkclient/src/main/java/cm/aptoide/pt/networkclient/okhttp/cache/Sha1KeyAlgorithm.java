/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/04/2016.
 */

package cm.aptoide.pt.networkclient.okhttp.cache;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Request;
import okio.Buffer;

/**
 * Created by sithengineer on 28/04/16.
 */
public class Sha1KeyAlgorithm implements KeyAlgorithm {

	@Override
	public String getKeyFrom(Request request)  {
		try {

			Buffer bodyBuffer = new Buffer();
			request.body().writeTo(bodyBuffer);
			String requestBody = bodyBuffer.readUtf8();

			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			messageDigest.update(requestBody.getBytes("UTF-8"));
			byte[] bytes = messageDigest.digest();
			StringBuilder buffer = new StringBuilder();
			for (byte b : bytes) {
				buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			}
			return buffer.toString();

		}catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}
}
