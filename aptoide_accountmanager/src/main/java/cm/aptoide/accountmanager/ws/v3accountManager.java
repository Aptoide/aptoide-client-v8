/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.accountmanager.ws;

import java.util.HashMap;

import cm.aptoide.accountmanager.ws.responses.ChangeUserSettingsResponse;
import cm.aptoide.accountmanager.ws.responses.CheckUserCredentialsJson;
import cm.aptoide.accountmanager.ws.responses.OAuth;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.cache.RequestCache;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by neuro on 25-04-2016.
 */
public abstract class v3accountManager<U> extends WebService<v3accountManager.Interfaces, U> {

	protected v3accountManager() {
		super(Interfaces.class);
	}

	@Override
	protected String getBaseHost() {
		return "https://webservices.aptoide.com/webservices/";
	}

	interface Interfaces {

		@FormUrlEncoded
		@POST("3/oauth2Authentication")
		@Headers({RequestCache.BYPASS_HEADER_KEY + ":" + RequestCache.BYPASS_HEADER_VALUE})
		Observable<OAuth> oauth2Authentication(@FieldMap HashMap<String, String> args);

		@FormUrlEncoded
		@POST("3/getUserInfo")
		@Headers({RequestCache.BYPASS_HEADER_KEY + ":" + RequestCache.BYPASS_HEADER_VALUE})
		Observable<CheckUserCredentialsJson> getUserInfo(@FieldMap HashMap<String, String> args);

		@POST("3/createUser")
		@FormUrlEncoded
		@Headers({RequestCache.BYPASS_HEADER_KEY + ":" + RequestCache.BYPASS_HEADER_VALUE})
		Observable<OAuth> createUser(@FieldMap HashMap<String, String> args);

		@POST("3/changeUserSettings")
		@FormUrlEncoded
		@Headers({RequestCache.BYPASS_HEADER_KEY + ":" + RequestCache.BYPASS_HEADER_VALUE})
		Observable<ChangeUserSettingsResponse> changeUserSettings(@FieldMap HashMap<String,
				String> args);
	}
}
