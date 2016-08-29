/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 22/06/2016.
 */

package cm.aptoide.pt.networkclient.okhttp.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networkclient.BuildConfig;
import cm.aptoide.pt.utils.AptoideUtils;
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

	private final KeyAlgorithm keyAlgorithm;

	private final Object diskCacheLock = new Object();
	private boolean initialized;
	private DiskLruCache diskLruCache;

	//
	// ctors
	//

	public RequestCache(KeyAlgorithm keyAlgorithm) {

		this.keyAlgorithm = keyAlgorithm;

		try {
			// FIXME move the cache directory to the app folder
			// TODO check if module update renders a new and fresh cache

			File cachePath = AptoideUtils.getContext().getCacheDir();

			boolean canRead = cachePath.canRead();
			boolean canWrite = cachePath.canWrite();

			if (!canRead || !canWrite) {
				throw new IllegalStateException(String.format("unable to read / write in the temporary cache directory" +
						" '%s' in the directory '%s'", cachePath
						.getName(), cachePath.getParentFile().getAbsolutePath()));
			} else {
				Logger.v(TAG, String.format("using temporary cache directory '%s' in the directory '%s'", cachePath
						.getName(), cachePath
						.getParentFile()
						.getAbsolutePath()));
			}

			if (BuildConfig.DEBUG && cachePath.exists() && cachePath.isDirectory()) {
				int deletedFiles = 0;
				File[] childFiles = cachePath.listFiles();
				if (childFiles != null && childFiles.length > 0) {
					for (File f : childFiles) {
						deletedFiles += f.delete() ? 1 : 0;
					}
					deletedFiles += cachePath.delete() ? 1 : 0;
				}
				Logger.w(TAG, String.format(Locale.ROOT, "cache running in debug mode : cleaned %d disk cache files", deletedFiles));
			}

			diskLruCache = DiskLruCache.open(
					cachePath,
					BuildConfig.VERSION_CODE,
					BUCKET_COUNT,
					DISK_CACHE_SIZE
			);

			this.initialized = true;
		} catch (Exception e) {
			Logger.e(TAG, "", e);
		}
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

		if (!initialized) {
			return response;
		}
		if ((response.code() / 100) != 2) return response;
//		String header = request.headers().get(BYPASS_HEADER_KEY);
//		if (header != null && header.equalsIgnoreCase(BYPASS_HEADER_VALUE)) {
//			return response;
//		}

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

				// return deep cloned response
				Response cachedResponse = cacheEntry.getResponse(request);
				return cachedResponse;
			}
		} catch (Exception ex) {
			Log.e(TAG, "", ex);
			if(editor!=null) {
				editor.abortUnlessCommitted();
			}
		}

		return response;
	}

	/**
	 * @param request
	 * @return Response for this request or null if non existing
	 * @throws IOException
	 */
	@Nullable
	public Response get(@NonNull Request request) {

		if (!initialized) {
			return null;
		}

		DiskLruCache.Snapshot snapshot = null;
		try {

			String header = request.headers().get(BYPASS_HEADER_KEY);
			if (header != null && header.equalsIgnoreCase(BYPASS_HEADER_VALUE)) {
				return null;
			}
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
		} finally {
			if(snapshot!=null) {
				snapshot.close();
			}
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
