/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.networkclient.okhttp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import lombok.Data;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * @author SithEngineer
 */
public
@Data
class RequestCacheEntry {

	private static final String TAG = RequestCacheEntry.class.getName();

	// response data
	private int code;
	private String message;
	private String protocol;
	private String body;
	private String bodyMediaType;
	private Map<String, List<String>> headers;

	public RequestCacheEntry() {
	}

	public RequestCacheEntry(Response response) {
		this.code = response.code();
		this.message = response.message();
		this.protocol = response.protocol().toString();
		this.headers = response.headers().toMultimap();

		Buffer responseBodyClone = response.body().source().buffer().clone();
		this.body = responseBodyClone.readString(Charset.forName("UTF-8"));

		this.bodyMediaType = response.body().contentType().toString();
	}

	@NonNull
	public static RequestCacheEntry fromString(@NonNull String data) {
		try {
			return new ObjectMapper().readValue(data, RequestCacheEntry.class);
		} catch (IOException e) {
			Log.e(TAG, "", e);
		}
		return null;
	}

	@Override
	public String toString() {
		try {
			String data = new ObjectMapper().writeValueAsString(this);
			return data;
		} catch (JsonProcessingException e) {
			Log.e(TAG, "", e);
		}
		return null;
	}

	public Response getResponse(Request request) {
		Response.Builder builder = new Response.Builder();

		builder.code(code);
		builder.message(message);

		try {
			builder.protocol(Protocol.get(protocol));
		} catch (IOException e) {
			e.printStackTrace();
		}

		ResponseBody responseBody = ResponseBody.create(MediaType.parse(this.bodyMediaType), body);
		builder.body(responseBody);

		Headers.Builder headersBuilders = new Headers.Builder();
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			for (String entryValue : entry.getValue()) {
				headersBuilders.add(entry.getKey(), entryValue);
			}
		}
		builder.headers(headersBuilders.build());

		builder.request(request);

		return builder.build();
	}
}
