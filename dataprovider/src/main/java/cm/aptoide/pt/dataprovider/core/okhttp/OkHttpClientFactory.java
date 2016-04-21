/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/04/2016.
 */

package cm.aptoide.pt.dataprovider.core.okhttp;

import java.io.File;
import java.io.IOException;

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

		return new OkHttpClient.Builder().cache(cache).addInterceptor(createCacheInterceptor()).build();
	}

	private static Interceptor createCacheInterceptor() {
		return new Interceptor() {

			AptoidePOSTRequestCache customCache = new AptoidePOSTRequestCache();

			@Override
			public Response intercept(Chain chain) throws IOException {
				Request request = chain.request();
				Response response = customCache.get(request);
				if (response != null) {
					return response;
				} else {
					response = chain.proceed(chain.request());
					customCache.put(request, response);
				}

				return response;
			}
		};
	}
}
