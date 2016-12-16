/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.networkclient.okhttp;

import cm.aptoide.pt.networkclient.okhttp.cache.L2Cache;
import cm.aptoide.pt.networkclient.okhttp.cache.PostCacheInterceptor;
import cm.aptoide.pt.networkclient.okhttp.cache.PostCacheKeyAlgorithm;
import java.io.File;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Factory for OkHttp Clients creation.
 *
 * @author Neurophobic Animal
 * @author SithEngineer
 */
public class OkHttpClientFactory {

  private static final String TAG = OkHttpClientFactory.class.getName();
  private static OkHttpClient httpClientInstance;
  private static L2Cache cache;

  static OkHttpClient newClient(File cacheDirectory, int cacheMaxSize, Interceptor interceptor,
      UserAgentGenerator userAgentGenerator) {

    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

    //		if (BuildConfig.DEBUG) {
    //			clientBuilder.addNetworkInterceptor(new StethoInterceptor());
    //		}
    clientBuilder.connectTimeout(2, TimeUnit.MINUTES);
    clientBuilder.writeTimeout(2, TimeUnit.MINUTES);
    clientBuilder.readTimeout(2, TimeUnit.MINUTES);
    clientBuilder.cache(new Cache(cacheDirectory, cacheMaxSize)); // 10 MiB
    clientBuilder.addInterceptor(interceptor);
    clientBuilder.addInterceptor(new UserAgentInterceptor(userAgentGenerator));

    return clientBuilder.build();
  }

  public static OkHttpClient newClient(UserAgentGenerator userAgentGenerator) {
    return new OkHttpClient.Builder().addInterceptor(new UserAgentInterceptor(userAgentGenerator))
        .build();
  }

  /**
   * @return an {@link OkHttpClient} instance
   */
  public static OkHttpClient getSingletonClient(UserAgentGenerator userAgentGenerator) {
    if (httpClientInstance == null) {
      cache = new L2Cache(new PostCacheKeyAlgorithm());
      httpClientInstance =
          newClient(new File("/"), 10 * 1024 * 1024, new PostCacheInterceptor(cache),
              userAgentGenerator);
    }
    return httpClientInstance;
  }

  // FIXME: inject cache or cache cleaning policy instead of exposing a method like this
  public static void cleanInMemoryCache() {
    if(cache!=null) {
      cache.clean();
    }
  }
}
