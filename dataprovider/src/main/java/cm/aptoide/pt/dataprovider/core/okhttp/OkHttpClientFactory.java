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

			OkHttpCustomCache customCache = new OkHttpCustomCache();

			@Override
			public Response intercept(Chain chain) throws IOException {

				final String cache_key = getCacheKey(chain.request());
				if (cache_key != null) {
					Response response = customCache.get(cache_key);

					if (response != null) {
						return response;
					} else {
						return customCache.put(cache_key, chain.proceed(chain.request()));
					}
				}

				return chain.proceed(chain.request());
			}

			private String getCacheKey(Request request) {
				return request.header("cache_key");
			}
		};
	}
}
