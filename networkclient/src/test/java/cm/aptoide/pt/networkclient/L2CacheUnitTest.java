package cm.aptoide.pt.networkclient;

import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.networkclient.okhttp.cache.L2Cache;
import cm.aptoide.pt.networkclient.okhttp.cache.Sha1KeyAlgorithm;
import java.io.IOException;
import java.nio.charset.Charset;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class L2CacheUnitTest {

  private L2Cache cache;
  private Request request;
  private Response response;

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Before public void init() throws IOException {
    cache = new L2Cache(new Sha1KeyAlgorithm(), temporaryFolder.newFile());
    final String requestData = "limit=1";

    Request.Builder requestBuilder = new Request.Builder();
    requestBuilder.addHeader("Cache-Control", "max-age=1800, public");
    requestBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded");
    requestBuilder.addHeader("Content-Length", Integer.toString(requestData.getBytes().length));
    requestBuilder.post(
        RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), requestData));
    requestBuilder.url("http://ws75.aptoide.com/api/7/listStores/limit/3");

    request = requestBuilder.build();

    final String responseData = "{response data}";

    Response.Builder responseBuilder = new Response.Builder();
    responseBuilder.code(200);
    responseBuilder.addHeader("Content-Type", "application/json");
    responseBuilder.addHeader("Cache-Control", "max-age=1800, public");
    responseBuilder.addHeader("Content-Length", Integer.toString(responseData.getBytes().length));
    responseBuilder.request(request.newBuilder().build());
    responseBuilder.protocol(Protocol.HTTP_1_1);
    responseBuilder.body(ResponseBody.create(MediaType.parse("application/json"), responseData));

    response = responseBuilder.build();
  }

  @Test(timeout = 300) public void putShouldNotBeNull() {
    cache.put(request, response);
    Response resp1 = cache.get(request);

    Charset charset = Charset.forName("UTF-8");
    try {
      assertEquals("stored response body after put() is not the same",
          response.body().source().readString(charset), resp1.body().source().readString(charset));
    } catch (Exception e) {
      CrashReport.getInstance().log(e);
      fail();
    }
  }

  @Test(timeout = 300) public void simpleGet() throws IOException {
    cache.put(request, response);
    Response cachedResponse = cache.get(request);

    String expectedResponseBodyData =
        cachedResponse.body().source().readString(Charset.forName("UTF-8"));
    try {
      Response resp2 = cache.get(request);
      assertNotNull(resp2);

      String currentData = resp2.body().source().readString(Charset.forName("UTF-8"));

      assertEquals("response body content after get() is not the same", expectedResponseBodyData,
          currentData);
    } catch (Exception e) {
      CrashReport.getInstance().log(e);
      fail();
    }
  }

  @Test(timeout = 300) public void cacheControlInvalidatedResponse() throws InterruptedException {
    Response response2 = response.newBuilder().header("Cache-Control", "max-age=0").build();

    cache.put(request, response2);

    // let cache entry rotten...

    Response resp2 = cache.get(request);
    assertNull("stored response after put() should be null due to cache control", resp2);
  }

  // TODO: 3/4/2017 move this code to a test for cache interceptor
  // the following test it's not working now since it is the interceptor that filters what is
  // cached by request headers
  /*
  @Test(timeout = 300)
  public void cacheControlBypassCache() {
    Request request2 = request.newBuilder()
        .header(PostCacheInterceptor.BYPASS_HEADER_KEY, PostCacheInterceptor.BYPASS_HEADER_VALUE)
        .build();

    usedRequests.add(request2);

    cache.put(request2, response);

    Response resp2 = cache.get(request2);
    assertNull("stored response after put() should be null due to cache bypass", resp2);
  }
  */

  // the following test it's not working now since it is the interceptor that filters what is
  // cached by response status code
  /*
  @Test(timeout = 300)
  public void doNotCacheErrorResponse() {
    Response response2 = response.newBuilder().code(501).build();

    usedRequests.add(request);

    cache.put(request, response2);

    Response responseFromCache = cache.get(request);
    assertNull("stored response after put() should be null due to cache control",
        responseFromCache);
  }
  */
}
