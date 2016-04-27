/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.networkclient.okhttp;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cm.aptoide.pt.networkclient.BuildConfig;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

/**
 * @author Neurophobic Animal
 * @author SithEngineer
 */
public class AptoidePOSTRequestCache {

	public static final String BYPASS_HEADER_KEY = "Bypass-Cache";
	public static final String BYPASS_HEADER_VALUE = "true";

	private static final String TAG = AptoidePOSTRequestCache.class.getName();

	//
	// vars
	//

	private static final int BUCKET_COUNT = 2;
	private static final int DATA_BUCKET_INDEX = 0;
	private static final int TIMESTAMP_BUCKET_INDEX = 1;
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
	private static final String DISK_CACHE_SUBDIR = "posts";
	private final Object diskCacheLock = new Object();
	private DiskLruCache diskLruCache;

	//
	// ctors
	//

	public AptoidePOSTRequestCache() {
		try {
			diskLruCache = DiskLruCache.open(new File(Environment.getExternalStorageDirectory() + "/" + DISK_CACHE_SUBDIR), BuildConfig.VERSION_CODE, BUCKET_COUNT, DISK_CACHE_SIZE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//
	// methods
	//

	public void remove(@NonNull Request request) {
		synchronized (diskCacheLock) {
			try {
				diskLruCache.remove(getKeyFrom(request));
			} catch (Exception e) {
				Log.e(TAG, "", e);
			}
		}
	}

	@NonNull
	private String getKeyFrom(@NonNull Request request) throws IOException, NoSuchAlgorithmException {
		Buffer bodyBuffer = new Buffer();
		request.body().writeTo(bodyBuffer);
		String requestBody = bodyBuffer.readUtf8();

		MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
		messageDigest.update(requestBody.getBytes("UTF-8"));
		byte[] bytes = messageDigest.digest();
		StringBuilder buffer = new StringBuilder();
		for (byte b : bytes) {
			buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}
		return buffer.toString();
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

		DiskLruCache.Editor editor = null;
		try {
			final String reqKey = getKeyFrom(request);
			synchronized (diskCacheLock) {
				editor = diskLruCache.edit(reqKey);
				// create cache entry building from the previous response so that we don't modify it
				RequestCacheEntry cacheEntry = new RequestCacheEntry(response);
				editor.set(DATA_BUCKET_INDEX, cacheEntry.toString());
				editor.set(TIMESTAMP_BUCKET_INDEX, SimpleDateFormat.getInstance().format(new Date()));
				editor.commit();
				return response;
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

		return null;
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
				final String reqKey = getKeyFrom(request);
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
