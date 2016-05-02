/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/04/2016.
 */

package cm.aptoide.pt.networkclient.okhttp;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import cm.aptoide.pt.networkclient.BuildConfig;
import cm.aptoide.pt.networkclient.okhttp.cache.RequestCache;
import okhttp3.Cache;
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

	public static OkHttpClient newClient() {
		return newClient(new File("/"));
	}

	public static OkHttpClient newClient(File cacheDirectory) {
		final long cacheSize = 10 * 1024 * 1024; // 10 MiB

		return newClient(cacheDirectory, cacheSize);
	}

	public static OkHttpClient newClient(File cacheDirectory, long cacheSize) {
		final Cache cache = new Cache(cacheDirectory, cacheSize);

//		return new OkHttpClient.Builder().cache(cache).addInterceptor(createCacheInterceptor()).build();
		return new OkHttpClient.Builder().cache(cache).build();
	}

	private static Interceptor createCacheInterceptor() {
		return new Interceptor() {

			private static final String TAG = "CacheInterceptor";

			private final RequestCache customCache = new RequestCache();

			@Override
			public Response intercept(Chain chain) throws IOException {
				Request request = chain.request();
				Response response = customCache.get(request);

				if (response != null) {

					if(BuildConfig.DEBUG) {
						Log.v(TAG, "cache hit");
					}

					return response;
				}

				response = chain.proceed(chain.request());
				long responseLength = response.body().contentLength();
				final Response resultResponse = customCache.put(request, response);

				if(resultResponse==null || resultResponse.body().contentLength()
						!=responseLength) {
					Log.e(TAG, "server response and cached response are different");
				}

				if(BuildConfig.DEBUG) {
					Log.v(TAG, "cache miss");
				}

				return resultResponse;
			}
		};
	}
}
