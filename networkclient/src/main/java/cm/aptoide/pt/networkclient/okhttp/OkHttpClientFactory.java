/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.networkclient.okhttp;

import cm.aptoide.pt.actions.GenerateClientId;
import cm.aptoide.pt.actions.UserData;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networkclient.BuildConfig;
import cm.aptoide.pt.networkclient.okhttp.cache.RequestCache;
import cm.aptoide.pt.utils.AptoideUtils;
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
      Interceptor interceptor, GenerateClientId generateClientId, UserData userData) {
    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

    //		if (BuildConfig.DEBUG) {
    //			clientBuilder.addNetworkInterceptor(new StethoInterceptor());
    //		}

    clientBuilder.cache(new Cache(cacheDirectory, cacheMaxSize)); // 10 MiB

    clientBuilder.addInterceptor(interceptor);

    if(generateClientId!=null){
      clientBuilder.addInterceptor(new UserAgentInterceptor(AptoideUtils.NetworkUtils.getDefaultUserAgent(generateClientId, userData)));
    }

    return clientBuilder.build();
  }

  public static OkHttpClient newClient(GenerateClientId generateClientId, UserData userData) {
    return new OkHttpClient.Builder().addInterceptor(
        new UserAgentInterceptor(AptoideUtils.NetworkUtils.getDefaultUserAgent(generateClientId, userData))).build();
  }

  /**
   *
   * @param generateClientId an entity that generates user unique ids to use in User-Agent HEADER or null
   * @return an {@link OkHttpClient} instance
   */
  public static OkHttpClient getSingletonClient(GenerateClientId generateClientId, UserData userData) {
    if (httpClientInstance == null) {
      httpClientInstance =
          newClient(new File("/"), 10 * 1024 * 1024, new AptoideCacheInterceptor(), generateClientId, userData);
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

        if (BuildConfig.DEBUG) {
          Logger.v(TAG, String.format("cache hit '%s'", httpUrl));
        }

        return response;
      }

      if (BuildConfig.DEBUG) {
        Logger.v(TAG, String.format("cache miss '%s'", httpUrl));
      }

      return customCache.put(request, chain.proceed(request));
    }
  }
}
