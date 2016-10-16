/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.networkclient;

import cm.aptoide.pt.actions.GenerateClientId;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.networkclient.okhttp.UserAgentInterceptor;
import cm.aptoide.pt.utils.AptoideUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class UserAgentTest {

  @Test public void userAgentIsSetInRequestHeader() throws Exception {
    MockWebServer server = new MockWebServer();
    server.enqueue(new MockResponse().setBody("OK"));
    server.start();
    String url = server.url("/").toString();

    OkHttpClient client = new OkHttpClient();
    client.networkInterceptors().add(new UserAgentInterceptor("foo/bar"));
    Request testRequest = new Request.Builder().url(url).build();
    String result = client.newCall(testRequest).execute().body().string();
    assertEquals("OK", result);

    RecordedRequest request = server.takeRequest();
    assertEquals("foo/bar", request.getHeader("User-Agent"));
  }

  @Test public void currentUserAgentForSingletonClient() throws Exception {

    GenerateClientId generateClientId = new GenerateClientId() {
      @Override public String getClientId() {
        return "dummy client id";
      }
    };

    String userData = "user@aptoide.com";

    final String expectedUserAgent =
        AptoideUtils.NetworkUtils.getDefaultUserAgent(generateClientId, userData);

    MockWebServer server = new MockWebServer();
    server.enqueue(new MockResponse().setBody("OK"));
    server.start();
    String url = server.url("/").toString();

    Request testRequest = new Request.Builder().url(url).build();
    String result = OkHttpClientFactory.getSingletonClient(generateClientId, userData)
        .newCall(testRequest)
        .execute()
        .body()
        .string();
    assertEquals("OK", result);

    RecordedRequest request = server.takeRequest();
    assertEquals(expectedUserAgent, request.getHeader("User-Agent"));
  }

  @Test public void currentUserAgentForNewClient() throws Exception {

    GenerateClientId generateClientId = new GenerateClientId() {
      @Override public String getClientId() {
        return "dummy client id";
      }
    };

    String userData = "user@aptoide.com";

    final String expectedUserAgent =
        AptoideUtils.NetworkUtils.getDefaultUserAgent(generateClientId, userData);

    MockWebServer server = new MockWebServer();
    server.enqueue(new MockResponse().setBody("OK"));
    server.start();
    String url = server.url("/").toString();

    Request testRequest = new Request.Builder().url(url).build();
    String result = OkHttpClientFactory.newClient(generateClientId, userData)
        .newCall(testRequest)
        .execute()
        .body()
        .string();
    assertEquals("OK", result);

    RecordedRequest request = server.takeRequest();
    assertEquals(expectedUserAgent, request.getHeader("User-Agent"));
  }
}

