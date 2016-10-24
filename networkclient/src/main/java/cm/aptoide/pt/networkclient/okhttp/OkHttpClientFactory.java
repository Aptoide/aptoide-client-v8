/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.networkclient.okhttp;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networkclient.okhttp.cache.RequestCache;
import java.io.File;
import java.io.IOException;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Factory for OkHttp Clients creation.
 *
 * @author Neurophobic Animal
 * @author SithEngineer
 */
public class OkHttpClientFactory {

  private static final String TAG = OkHttpClientFactory.class.getName();
  private static OkHttpClient httpClientInstance;

  public static OkHttpClient newClient(File cacheDirectory, int cacheMaxSize,
      Interceptor interceptor, UserAgentGenerator userAgentGenerator) {
    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

    //		if (BuildConfig.DEBUG) {
    //			clientBuilder.addNetworkInterceptor(new StethoInterceptor());
    //		}

    clientBuilder.cache(new Cache(cacheDirectory, cacheMaxSize)); // 10 MiB

    clientBuilder.addInterceptor(interceptor);

    clientBuilder.addInterceptor(new UserAgentInterceptor(userAgentGenerator));

    return clientBuilder.build();
  }

  public static OkHttpClient newClient(UserAgentGenerator userAgentGenerator) {
    return new OkHttpClient.Builder().addInterceptor(new UserAgentInterceptor(userAgentGenerator)).build();
  }

  /**
   * @return an {@link OkHttpClient} instance
   */
  public static OkHttpClient getSingletonClient(UserAgentGenerator userAgentGenerator) {
    if (httpClientInstance == null) {
      httpClientInstance = newClient(new File("/"), 10 * 1024 * 1024, new AptoideCacheInterceptor(),
          userAgentGenerator);
    }
    return httpClientInstance;
  }

  private static final class AptoideCacheInterceptor implements Interceptor {

    private final String TAG =
        OkHttpClientFactory.TAG + "." + AptoideCacheInterceptor.class.getSimpleName();

    private final RequestCache customCache = new RequestCache();

    @Override public Response intercept(Chain chain) throws IOException {
      Request request = chain.request();
      Response response = customCache.get(request);

      HttpUrl httpUrl = request.url();
      if (response != null) {

        Logger.v(TAG, String.format("cache hit '%s'", httpUrl));
        return response;
      } else {

        Logger.v(TAG, String.format("cache miss '%s'", httpUrl));
        return customCache.put(request, chain.proceed(request));
      }
    }
  }
}
