/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * Created by marcelobenites on 10/7/16.
 */

public final class BoaCompraAuthorization {

  private final String secretKey;
  private final int merchantId;

  public BoaCompraAuthorization(String secretKey, int merchantId) {
    this.secretKey = secretKey;
    this.merchantId = merchantId;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public int getMerchantId() {
    return merchantId;
  }

  public String generate(Request request) throws IOException {
    final Buffer buffer = new Buffer();
    try {

      buffer.write(request.url().encodedPath().getBytes());

      if (request.url().query() != null) {
        buffer.write("?".getBytes());
        buffer.write(request.url().encodedQuery().getBytes());
      }

      if (request.body() != null) {
        buffer.write(getHexMd5(request.body()).getBytes());
      }

      return String.valueOf(merchantId).concat(":").concat(getHmacSHA256(buffer));
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new IOException(e);
    } finally {
      buffer.close();
    }
  }

  private String getHmacSHA256(Buffer buffer) throws NoSuchAlgorithmException, InvalidKeyException {
    final Buffer resultBuffer = new Buffer();
    try {
      Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
      hmacSHA256.init(secretKeySpec);
      resultBuffer.write(hmacSHA256.doFinal(buffer.readByteArray()));
      return resultBuffer.readByteString().hex();
    } finally {
      buffer.close();
    }
  }

  private String getHexMd5(RequestBody body) throws IOException {
    final Buffer buffer = new Buffer();
    try {
      body.writeTo(buffer);
      return buffer.md5().hex();
    } finally {
      buffer.close();
    }
  }
}