/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.networkclient;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Vector;

import cm.aptoide.pt.networkclient.okhttp.cache.RequestCache;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.DiskLruCache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author SithEngineer
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 16)
public class DiskLruUnitTest {

	private static final String TAG = DiskLruCache.class.getName();

	private static RequestCache cache;
	private static Request request;
	private static Response response;

	private static Vector<Request> usedRequests;

	@BeforeClass
	public static void init() {
		cache = new RequestCache();
		usedRequests = new Vector<>(2);

		final String requestData = "limit=1";

		Request.Builder requestBuilder = new Request.Builder();
		requestBuilder.addHeader("Cache-Control", "max-age=1800, public");
		requestBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded");
		requestBuilder.addHeader("Content-Length", Integer.toString(requestData.getBytes().length));
		requestBuilder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), requestData));
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

	@AfterClass
	public static void destroy() {
		cache.destroy();
		usedRequests.clear();
		usedRequests = null;
		cache = null;
		request = null;
		response = null;
	}

	@Before
	public void emptyCacheBeforeEachTest() {
		for (Request req : usedRequests) {
			cache.remove(req);
		}
	}

	@Test
	public void putShouldNotBeNull() {
		usedRequests.add(request);
		Response resp1 = cache.put(request, response);

		Charset charset = Charset.forName("UTF-8");
		try {
			assertEquals(
					"stored response body after put() is not the same",
					response.body().source().readString(charset),
					resp1.body().source().readString(charset)
			);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void simpleGet() throws IOException {
		usedRequests.add(request);
		Response cachedResponse = cache.put(request, response);

		String expectedResponseBodyData = cachedResponse.body().source().readString(Charset.forName("UTF-8"));
		try {
			Response resp2 = cache.get(request);
			assertNotNull(resp2);

			String currentData = resp2.body().source().readString(Charset.forName("UTF-8"));

			assertEquals("response body content after get() is not the same", expectedResponseBodyData, currentData);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void cacheControlInvalidatedResponse() throws InterruptedException {
		Response response2 = response.newBuilder().header("Cache-Control", "max-age=0").build();

		usedRequests.add(request);

		cache.put(request, response2);

		// let cache entry rotten...

		Response resp2 = cache.get(request);
		assertNull("stored response after put() should be null due to cache control", resp2);
	}

	@Test
	public void cacheControlBypassCache() {
		Request request2 = request.newBuilder().header(RequestCache.BYPASS_HEADER_KEY, RequestCache.BYPASS_HEADER_VALUE).build();

		usedRequests.add(request2);

		cache.put(request2, response);

		Response resp2 = cache.get(request2);
		assertNull("stored response after put() should be null due to cache bypass", resp2);
	}

	@Test
	public void dontCacheErrorResponse() {
		Response response2 = response.newBuilder().code(501).build();

		usedRequests.add(request);

		cache.put(request, response2);

		Response responseFromCache = cache.get(request);
		assertNull("stored response after put() should be null due to cache control", responseFromCache);
	}
}
