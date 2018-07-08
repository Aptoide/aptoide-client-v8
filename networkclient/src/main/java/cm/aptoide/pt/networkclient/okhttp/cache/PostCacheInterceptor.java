package cm.aptoide.pt.networkclient.okhttp.cache;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import java.io.IOException;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class PostCacheInterceptor implements Interceptor {

  public static final String BYPASS_HEADER_KEY = "X-Bypass-Cache";
  public static final String BYPASS_HEADER_VALUE = "true";

  private final String TAG = PostCacheInterceptor.class.getSimpleName();

  private final Cache<Request, Response> cache;

  public PostCacheInterceptor(Cache<Request, Response> cache) {
    this.cache = cache;
  }

  @Override public Response intercept(Chain chain) {
    final Request request = chain.request();

    // we only intercept and cache POST requests
    if (!request.method().equalsIgnoreCase("POST")) {
      try {
        return chain.proceed(request);
      } catch (IOException e) {
        CrashReport.getInstance().log(e);
      }
    }

    // we shouldn't cache the response if the client explicitly asked us not to
    Headers headers = request.headers();
    if (headers.size() > 0) {
      for (String bypassCacheHeaderValue : headers.values(BYPASS_HEADER_KEY)) {
        if (bypassCacheHeaderValue.equalsIgnoreCase(BYPASS_HEADER_VALUE)) {
          try {
            return chain.proceed(request);
          } catch (IOException e) {
            CrashReport.getInstance().log(e);
          }
        }
      }
    }

    // hit the cache to get the response
    if (cache.contains(request)) {
      Response cachedResponse = cache.get(request);
      if (cachedResponse != null) {
        Logger.v(TAG, String.format("cache hit '%s'", request.url()));
        return cachedResponse;
      }

      Logger.v(TAG, String.format("cache hit but with null result '%s'", request.url()));
    }

    // in case of a cache miss, go to the network
    Logger.v(TAG, String.format("cache miss '%s'", request.url()));
    Response response = null;
    try {
      response = chain.proceed(request);
    } catch (IOException e) {
      CrashReport.getInstance().log(e);
    }

    // we only cache successful responses
    if (response != null && response.isSuccessful()) {
      cache.put(request, response);
    }
    return response;
  }
}
