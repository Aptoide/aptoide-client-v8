/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.networkclient.okhttp.cache;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import cm.aptoide.pt.utils.MathUtils;
import okhttp3.Request;
import okio.Buffer;

/**
 * Created by sithengineer on 28/04/16.
 * <br><br>
 * Creates a unique SHA-1 key for each request using one of this methods, ordered
 * by preference:
 * <ol>
 * <li>Request body</li>
 * <li>URL</li>
 * </ol>
 */
public class Sha1KeyAlgorithm implements KeyAlgorithm {

	@Override
	public String getKeyFrom(Request request)  {
		try {
			String requestIdentifier;
			final Buffer bodyBuffer = new Buffer();
			final Request clonedRequest = request.newBuilder().build();

			if (clonedRequest.body() != null && clonedRequest.body().contentLength() > 0) {
				// best scenario: use request body as key
				clonedRequest.body().writeTo(bodyBuffer);
				requestIdentifier = clonedRequest.url().toString() + bodyBuffer.readUtf8();
			} else {
				// no body to use as key. use query string params if they exist or
				// in the worst case the url itself
				requestIdentifier = clonedRequest.url().toString();
			}

//			final MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
//			messageDigest.update(requestIdentifier.getBytes("UTF-8"));
//			byte[] bytes = messageDigest.digest();
//			final StringBuilder buffer = new StringBuilder();
//			for (byte b : bytes) {
//				buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
//			}
//			return buffer.toString();
			return MathUtils.computeSHA1sum(requestIdentifier);

		}catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}
}
