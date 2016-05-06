/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
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

	private static final String TAG = OkHttpClientFactory.class.getName();

	public static OkHttpClient newClient() {
		return newClient(new File("/"));
	}

	public static OkHttpClient newClient(File cacheDirectory) {
		final long cacheSize = 10 * 1024 * 1024; // 10 MiB

		return newClient(cacheDirectory, cacheSize);
	}

	public static OkHttpClient newClient(File cacheDirectory, long cacheSize) {

		return new OkHttpClient.Builder()
									.cache(new Cache(cacheDirectory, cacheSize))
									.addInterceptor(new AptoideCacheInterceptor())
									.build();

		//return new OkHttpClient.Builder().build();
	}

	private static final class AptoideCacheInterceptor implements Interceptor {

		private final String TAG = OkHttpClientFactory.TAG + "." + AptoideCacheInterceptor.class
				.getSimpleName();

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

			if(BuildConfig.DEBUG) {
				Log.v(TAG, "cache miss");
			}

			Response cachedResponse = customCache.put(request, chain.proceed(request));
			return cachedResponse;
		}
	}
}
