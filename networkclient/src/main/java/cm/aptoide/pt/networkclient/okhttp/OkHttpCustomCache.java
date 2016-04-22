/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.networkclient.okhttp;

import java.io.File;

import okhttp3.Response;

/**
 * Created by neuro on 23-03-2016.
 */
public class OkHttpCustomCache {

	private static final long MAX_SIZE = 10 * 1024 * 1024;
	private final File directory;
	private final long maxSize;

	public OkHttpCustomCache(File directory, long maxSize) {
		this.directory = directory;
		this.maxSize = maxSize;
	}

	public OkHttpCustomCache(long maxSize) {
		// TODO
		this(null, maxSize);
	}

	public OkHttpCustomCache() {
		// TODO
		this(null, MAX_SIZE);
	}

	public Response put(String key, Response response) {
		return response;
	}

	public Response get(String key) {
		return null;
	}
}
