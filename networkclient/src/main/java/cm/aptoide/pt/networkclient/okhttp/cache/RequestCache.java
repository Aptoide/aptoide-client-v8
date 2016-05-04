/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.networkclient.okhttp.cache;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cm.aptoide.pt.networkclient.BuildConfig;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Neurophobic Animal
 * @author SithEngineer
 */
public class RequestCache {

	public static final String BYPASS_HEADER_KEY = "Bypass-Cache";
	public static final String BYPASS_HEADER_VALUE = "true";

	private static final String TAG = RequestCache.class.getName();

	//
	// vars
	//

	private static final int BUCKET_COUNT = 2;
	private static final int DATA_BUCKET_INDEX = 0;
	private static final int TIMESTAMP_BUCKET_INDEX = 1;
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
	private static final String DISK_CACHE_SUBDIR = "request_cache";

	private final KeyAlgorithm keyAlgorithm;

	private final Object diskCacheLock = new Object();
	private DiskLruCache diskLruCache;

	//
	// ctors
	//

	public RequestCache(KeyAlgorithm keyAlgorithm) {
		try {
			// FIXME move the cache directory to the app folder
			// TODO check if module update renders a new and fresh cache
			File cachePath = new File(Environment.getExternalStorageDirectory(), DISK_CACHE_SUBDIR);

			if( BuildConfig.DEBUG && cachePath.exists() ) {
				int deletedFiles = 0;
				for(File f : cachePath.listFiles()) {
					deletedFiles += f.delete() ? 1 : 0;
				}
				deletedFiles += cachePath.delete() ? 1 : 0;
				Log.w(TAG, String.format("cache running in debug mode : cleaned %d disk cache " +
						"files",
						deletedFiles));
			}

			diskLruCache = DiskLruCache.open(
					cachePath,
					BuildConfig.VERSION_CODE,
					BUCKET_COUNT,
					DISK_CACHE_SIZE
			);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.keyAlgorithm = keyAlgorithm;
	}

	public RequestCache() {
		this(new Sha1KeyAlgorithm());
	}

	//
	// methods
	//

	public void remove(@NonNull Request request) {
		synchronized (diskCacheLock) {
			try {
				diskLruCache.remove(keyAlgorithm.getKeyFrom(request));
			} catch (Exception e) {
				Log.e(TAG, "", e);
			}
		}
	}

	/**
	 * Cache response for a specific request
	 *
	 * @param request  original request
	 * @param response server response
	 * @return null if response.code != 2xx or if something goes wrong. if everything goes well the original request is returned
	 */
	@Nullable
	public Response put(@NonNull Request request, @NonNull Response response) {

		if ((response.code() / 100) != 2) return null;

		final Response clonedResponse = response.newBuilder().build();
		DiskLruCache.Editor editor = null;
		try {
			final String reqKey = keyAlgorithm.getKeyFrom(request);
			synchronized (diskCacheLock) {
				editor = diskLruCache.edit(reqKey);
				// create cache entry building from the previous response so that we don't modify it
				RequestCacheEntry cacheEntry = new RequestCacheEntry(response);
				editor.set(DATA_BUCKET_INDEX, cacheEntry.toString());
				editor.set(TIMESTAMP_BUCKET_INDEX, SimpleDateFormat.getInstance().format(new Date()));
				editor.commit();
				return clonedResponse;
			}
		} catch (Exception ex) {
			Log.e(TAG, "", ex);
		}

		try {
			if (editor != null) {
				editor.abort();
			}
		} catch (Exception ex) {
			Log.e(TAG, "aborting transaction to disk cache", ex);
		}

		return clonedResponse;
	}

	/**
	 * @param request
	 * @return Response for this request or null if non existing
	 * @throws IOException
	 */
	@Nullable
	public Response get(@NonNull Request request) {
		try {

			String header = request.headers().get(BYPASS_HEADER_KEY);
			if (header != null && header.equalsIgnoreCase(BYPASS_HEADER_VALUE)) {
				return null;
			}

			DiskLruCache.Snapshot snapshot;
			synchronized (diskCacheLock) {
				final String reqKey = keyAlgorithm.getKeyFrom(request);
				if (reqKey == null) {
					Log.w(TAG, "Key algorithm returned a null key for request");
					return null;
				}

				snapshot = diskLruCache.get(reqKey);
			}

			// if snapshot entry doesn't exist return null
			if (snapshot == null) return null;

			String data = snapshot.getString(DATA_BUCKET_INDEX);
			RequestCacheEntry cacheEntry = RequestCacheEntry.fromString(data);
			// create response using the previous cloned request so that we don't modify it
			Response response = cacheEntry.getResponse(request);

			Calendar cacheMaxTime = Calendar.getInstance();
			cacheMaxTime.setTime(SimpleDateFormat.getInstance().parse(snapshot.getString(TIMESTAMP_BUCKET_INDEX)));
			int maxSeconds = response.cacheControl().maxAgeSeconds();
			cacheMaxTime.add(Calendar.SECOND, maxSeconds);

			Calendar current = Calendar.getInstance();
			if (current.after(cacheMaxTime)) {
				return null;
			}

			return response;
		} catch (Exception ex) {
			Log.e(TAG, "", ex);
		}
		return null;
	}

	public void destroy() {
		if (diskLruCache != null) {
			try {
				diskLruCache.delete();
			} catch (IOException ex) {
				Log.e(TAG, "", ex);
			}
		}
	}
}
