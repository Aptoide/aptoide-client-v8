/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import java.io.IOException;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * Created by marcelobenites on 10/7/16.
 */

public final class BoaCompraAuthorization {

  private final String secretKey;
  private final int storeId;

  public BoaCompraAuthorization(String secretKey, int storeId) {
    this.secretKey = secretKey;
    this.storeId = storeId;
  }

  public String generate(Request request) throws IOException {
    final Buffer buffer = new Buffer();
    try {

      buffer.write(request.url().encodedPath().getBytes());

      if (request.url().query() != null) {
        buffer.write("?".getBytes());
        buffer.write(request.url().query().getBytes());
      }

      if (request.body() != null) {
        buffer.write(getHexMd5(request.body()).getBytes());
      }

      return String.valueOf(storeId).concat(":").concat(buffer.sha256().hex());
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