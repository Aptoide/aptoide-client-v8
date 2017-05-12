package cm.aptoide.pt.networkclient.okhttp.cache;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networkclient.WebService;
import java.io.IOException;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class POSTCacheInterceptor implements Interceptor {

  private final String TAG = POSTCacheInterceptor.class.getSimpleName();

  private final Cache<Request, Response> cache;

  public POSTCacheInterceptor(Cache<Request, Response> cache) {
    this.cache = cache;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    final Request request = chain.request();

    // we only intercept and cache POST requests
    if (!request.method()
        .equalsIgnoreCase("POST")) {
      return chain.proceed(request);
    }

    // we shouldn't cache the response if the client explicitly asked us not to
    Headers headers = request.headers();
    if (headers.size() > 0) {
      for (String bypassCacheHeaderValue : headers.values(WebService.BYPASS_HEADER_KEY)) {
        if (bypassCacheHeaderValue.equalsIgnoreCase(WebService.BYPASS_HEADER_VALUE)) {
          return chain.proceed(request);
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
    Response response = chain.proceed(request);

    // we only cache successful responses
    if (response.isSuccessful()) {
      cache.put(request, response);
    }
    return response;
  }
}
