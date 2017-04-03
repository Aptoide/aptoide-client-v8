/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/06/2016.
 */

package cm.aptoide.pt.networkclient.okhttp.cache;

import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.utils.AptoideUtils;
import java.io.IOException;
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
public class Sha1KeyAlgorithm implements KeyAlgorithm<Request, String> {

  private static final String TAG = Sha1KeyAlgorithm.class.getName();

  @Override public String getKeyFrom(Request request) {
    try {
      final Buffer bodyBuffer = new Buffer();
      final Request clonedRequest = request.newBuilder().build();

      String requestIdentifier;

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
      return AptoideUtils.AlgorithmU.computeSha1(requestIdentifier);
    } catch (IOException e) {
      CrashReport.getInstance().log(e);
    }

    return null;
  }
}
