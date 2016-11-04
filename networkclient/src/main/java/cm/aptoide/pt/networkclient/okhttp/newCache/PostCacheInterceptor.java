package cm.aptoide.pt.networkclient.okhttp.newCache;

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

  private final L2Cache cache;

  public PostCacheInterceptor() {
    cache = new L2Cache(new PostCacheKeyAlgorithm());
  }

  @Override public Response intercept(Chain chain) throws IOException {
    final Request originalRequest = chain.request();
    final Request toReturnRequest = originalRequest.newBuilder()
        .method(originalRequest.method(), originalRequest.body())
        .build();
    final Request toKeepRequest = originalRequest.newBuilder()
        .method(originalRequest.method(), originalRequest.body())
        .build();

    // we only intercept and cache POST requests
    if (!originalRequest.method().equalsIgnoreCase("POST")) {
      return chain.proceed(toReturnRequest);
    }

    // we shouldn't cache the response if the client explicitly asked us not to
    Headers headers = originalRequest.headers();
    if (headers.size() > 0) {
      for (String bypassCacheHeaderValue : headers.values(BYPASS_HEADER_KEY)) {
        if (bypassCacheHeaderValue.equalsIgnoreCase(BYPASS_HEADER_VALUE)) {
          return chain.proceed(toReturnRequest);
        }
      }
    }

    // try to hit the cache to get the response
    if (cache.contains(originalRequest)) {
      Response cachedResponse = cache.get(toKeepRequest);
      if (cachedResponse != null) {
        Logger.v(TAG, String.format("cache hit '%s'", originalRequest.url()));
        return cachedResponse;
      }

      Logger.v(TAG, String.format("cache hit but with null result '%s'", originalRequest.url()));
    }

    // in case of a cache miss, go to the network
    Logger.v(TAG, String.format("cache miss '%s'", originalRequest.url()));
    Response response = chain.proceed(toReturnRequest);
    cache.put(originalRequest,
        response.newBuilder().headers(response.headers()).body(response.body())
            .build()
    );
    return response;
  }
}
