/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.networkclient;

import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.networkclient.okhttp.UserAgentGenerator;
import cm.aptoide.pt.utils.AptoideUtils;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class UserAgentTest {

  private static final String VERSION_NAME = "testing";
  private static final String TERMINAL_INFO = "aptoide mobile";
  private static final String USER_EMAIL = "user@aptoide.com";
  private static final String OEM_ID = "test oem";

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test public void currentUserAgentForSingletonClient() throws Exception {

    AptoideClientUUID aptoideClientUUID = new AptoideClientUUID() {
      @Override public String getUniqueIdentifier() {
        return USER_EMAIL;
      }
    };

    final String expectedUserAgent =
        AptoideUtils.NetworkUtils.getDefaultUserAgent(aptoideClientUUID, () -> USER_EMAIL,
            VERSION_NAME, OEM_ID, TERMINAL_INFO);

    MockWebServer server = new MockWebServer();
    server.enqueue(new MockResponse().setBody("OK"));
    server.start();
    String url = server.url("/").toString();

    final UserAgentGenerator userAgentGenerator = new UserAgentGenerator() {
      @Override public String generateUserAgent() {
        return AptoideUtils.NetworkUtils.getDefaultUserAgent(aptoideClientUUID, () -> USER_EMAIL,
            VERSION_NAME, OEM_ID, TERMINAL_INFO);
      }
    };
    Request testRequest = new Request.Builder().url(url).build();
    String result =
        OkHttpClientFactory.getSingletonClient(userAgentGenerator, false, temporaryFolder.newFile())
            .newCall(testRequest)
            .execute()
            .body()
            .string();
    assertEquals("OK", result);

    RecordedRequest request = server.takeRequest();
    assertEquals(expectedUserAgent, request.getHeader("User-Agent"));
  }

  @Test public void currentUserAgentForNewClient() throws Exception {

    AptoideClientUUID aptoideClientUUID = new AptoideClientUUID() {
      @Override public String getUniqueIdentifier() {
        return USER_EMAIL;
      }
    };

    final String expectedUserAgent =
        AptoideUtils.NetworkUtils.getDefaultUserAgent(aptoideClientUUID, () -> USER_EMAIL,
            VERSION_NAME, OEM_ID, TERMINAL_INFO);

    MockWebServer server = new MockWebServer();
    server.enqueue(new MockResponse().setBody("OK"));
    server.start();
    String url = server.url("/").toString();

    final UserAgentGenerator userAgentGenerator = new UserAgentGenerator() {
      @Override public String generateUserAgent() {
        return AptoideUtils.NetworkUtils.getDefaultUserAgent(aptoideClientUUID, () -> USER_EMAIL,
            VERSION_NAME, OEM_ID, TERMINAL_INFO);
      }
    };
    Request testRequest = new Request.Builder().url(url).build();
    String result = OkHttpClientFactory.newClient(userAgentGenerator)
        .newCall(testRequest)
        .execute()
        .body()
        .string();
    assertEquals("OK", result);

    RecordedRequest request = server.takeRequest();
    assertEquals(expectedUserAgent, request.getHeader("User-Agent"));
  }
}

