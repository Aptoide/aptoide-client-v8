/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/04/2016.
 */

package cm.aptoide.pt.networkclient.okhttp.cache;

import android.support.annotation.NonNull;
import cm.aptoide.pt.logger.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import lombok.Data;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

/**
 * @author SithEngineer
 */
public @Data class RequestCacheEntry {

  private static final String DEFAULT_CHARSET = "UTF-8";

  private static final String TAG = RequestCacheEntry.class.getName();

  // response data
  private int code;
  private String message;
  private String protocol;
  private String body;
  private String bodyMediaType;
  private Map<String, List<String>> headers;

  public RequestCacheEntry() {
  }

  public RequestCacheEntry(Response response) {

    final ResponseBody responseBody = response.body();

    this.code = response.code();
    this.message = response.message();
    this.protocol = response.protocol().toString();
    this.headers = response.headers().toMultimap();
    this.bodyMediaType = responseBody.contentType().toString();

    Charset charset = Charset.forName(DEFAULT_CHARSET);
    charset = responseBody.contentType().charset(charset);

    BufferedSource source = null;
    try {
      source = responseBody.source();
      this.body = source.readString(charset);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        source.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @NonNull public static RequestCacheEntry fromString(@NonNull String data) {
    try {
      return new ObjectMapper().readValue(data, RequestCacheEntry.class);
    } catch (IOException e) {
      Logger.e(TAG, "", e);
    }
    return null;
  }

  @Override public String toString() {
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      Logger.e(TAG, "", e);
    }
    return null;
  }

  public Response getResponse(Request request) {
    Response.Builder builder = new Response.Builder();

    builder.code(code);
    builder.message(message);

    try {
      builder.protocol(Protocol.get(protocol));
    } catch (IOException e) {
      e.printStackTrace();
    }

    ResponseBody responseBody = ResponseBody.create(MediaType.parse(this.bodyMediaType), body);
    builder.body(responseBody);

    Headers.Builder headersBuilders = new Headers.Builder();
    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
      for (String entryValue : entry.getValue()) {
        headersBuilders.add(entry.getKey(), entryValue);
      }
    }
    builder.headers(headersBuilders.build());

    builder.request(request);

    return builder.build();
  }
}
