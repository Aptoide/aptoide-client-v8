/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.networkclient.okhttp;

import android.support.annotation.NonNull;
import cm.aptoide.pt.networkclient.okhttp.cache.L2Cache;
import cm.aptoide.pt.networkclient.okhttp.cache.PostCacheInterceptor;
import cm.aptoide.pt.networkclient.okhttp.cache.PostCacheKeyAlgorithm;
import cm.aptoide.pt.utils.AptoideUtils;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Factory for OkHttp Clients creation.
 *
 * @author Neurophobic Animal
 * @author SithEngineer
 */
public class OkHttpClientFactory {

  private static final String CACHE_FILE_NAME = "aptoide.wscache";
  private static OkHttpClient httpClientInstance;
  private static L2Cache cache;

  public static OkHttpClient newClient(UserAgentGenerator userAgentGenerator) {
    return newClient(userAgentGenerator, false);
  }

  public static OkHttpClient newClient(UserAgentGenerator userAgentGenerator, boolean debug) {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();

    builder.addInterceptor(new UserAgentInterceptor(userAgentGenerator));

    if (debug) {
      HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
      httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

      builder.addInterceptor(httpLoggingInterceptor);
    }

    return builder.build();
  }

  /**
   * Deprecated method that uses a default cache file. Use instead the method with the same name
   * that allows for cache file specification.
   *
   * @return an {@link OkHttpClient} instance
   */
  @Deprecated
  public static OkHttpClient getSingletonClient(UserAgentGenerator userAgentGenerator,
      boolean debug) {
    return getSingletonClient(userAgentGenerator, debug,
        new File(AptoideUtils.getContext().getCacheDir(), CACHE_FILE_NAME));
  }

  /**
   * @return an {@link OkHttpClient} instance
   */
  public static OkHttpClient getSingletonClient(UserAgentGenerator userAgentGenerator,
      boolean debug, File cacheFile) {
    if (httpClientInstance == null) {

      cache = new L2Cache(new PostCacheKeyAlgorithm(), cacheFile);

      List<Interceptor> interceptors = new LinkedList<>();
      interceptors.add(new PostCacheInterceptor(cache));

      if (debug) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        interceptors.add(httpLoggingInterceptor);
      }

      httpClientInstance =
          newClient(new File("/"), 10 * 1024 * 1024, interceptors, userAgentGenerator);
    }
    return httpClientInstance;
  }

  static OkHttpClient newClient(File cacheDirectory, int cacheMaxSize,
      @NonNull List<Interceptor> interceptors, UserAgentGenerator userAgentGenerator) {

    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

    //		if (BuildConfig.DEBUG) {
    //			clientBuilder.addNetworkInterceptor(new StethoInterceptor());
    //		}
    clientBuilder.readTimeout(45, TimeUnit.SECONDS);
    clientBuilder.writeTimeout(45, TimeUnit.SECONDS);
    clientBuilder.cache(new Cache(cacheDirectory, cacheMaxSize)); // 10 MiB
    for (Interceptor interceptor : interceptors) {
      clientBuilder.addInterceptor(interceptor);
    }
    clientBuilder.addInterceptor(new UserAgentInterceptor(userAgentGenerator));

    return clientBuilder.build();
  }

  // FIXME: inject cache or cache cleaning policy instead of exposing a method like this
  public static void cleanInMemoryCache() {
    if (cache != null) {
      cache.clean();
    }
  }
}
