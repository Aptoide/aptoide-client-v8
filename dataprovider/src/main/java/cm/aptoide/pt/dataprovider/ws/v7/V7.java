/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 14/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import java.io.IOException;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppVersionsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreDisplaysRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreTabsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.model.v7.store.GetStoreTabs;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.cache.RequestCache;
import cm.aptoide.pt.preferences.Application;
import lombok.Getter;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 19-04-2016.
 */
public abstract class V7<U, B extends BaseBody> extends WebService<V7.Interfaces, U> {

	public static final String BASE_HOST = "http://ws75.aptoide.com/api/7/";
	@Getter protected final B body;
	private final String INVALID_ACCESS_TOKEN_CODE = "AUTH-2";
	private boolean accessTokenRetry = false;

	protected V7(boolean bypassCache, B body) {
		super(Interfaces.class, bypassCache);
		this.body = body;
	}

	@Override
	protected String getBaseHost() {
		return BASE_HOST;
	}

	@Override
	public Observable<U> observe() {
		return super.observe().subscribeOn(Schedulers.io()).onErrorResumeNext(throwable -> {
			if (throwable instanceof HttpException) {
				try {
					BaseV7Response baseV7Response = objectMapper.readValue(((HttpException)
							throwable)

							.response().errorBody().string(), BaseV7Response.class);

					if (INVALID_ACCESS_TOKEN_CODE.equals(baseV7Response.getErrors()
							.get(0)
							.getCode())) {

						if (!accessTokenRetry) {
							accessTokenRetry = true;
							return AptoideAccountManager.invalidateAccessToken(Application
									.getContext())
									.flatMap(s -> {
										this.body.setAccess_token(s);
										return V7.this.observe();
									});
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return Observable.error(throwable);
		}).observeOn(AndroidSchedulers.mainThread());
	}

	public interface Interfaces {

		@POST("getApp")
		Observable<GetApp> getApp(@Body GetAppRequest.Body body, @Header(RequestCache
				.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("listApps{url}")
		Observable<ListApps> listApps(@Path(value = "url", encoded = true) String path, @Body
		ListAppsRequest.Body body, @Header(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("listAppsUpdates")
		Observable<ListAppsUpdates> listAppsUpdates(@Body ListAppsUpdatesRequest.Body body,
													@Header(RequestCache.BYPASS_HEADER_KEY)
													boolean bypassCache);

		@POST("listAppVersions")
		Observable<ListAppVersions> listAppVersions(@Body ListAppVersionsRequest.Body body,
													@Header(RequestCache.BYPASS_HEADER_KEY)
													boolean bypassCache);

		@POST("getStore{url}")
		Observable<GetStore> getStore(@Path(value = "url", encoded = true) String path, @Body
		GetStoreRequest.Body body, @Header(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("getStoreMeta")
		Observable<GetStoreMeta> getStoreMeta(@Body GetStoreMetaRequest.Body body, @Header
				(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("getStoreDisplays{url}")
		Observable<GetStoreDisplays> getStoreDisplays(@Path(value = "url", encoded = true) String
															  path, @Body GetStoreDisplaysRequest
				.Body body, @Header(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("getStoreTabs")
		Observable<GetStoreTabs> getStoreTabs(@Body GetStoreTabsRequest.Body body, @Header
				(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("getStoreWidgets{url}")
		Observable<GetStoreWidgets> getStoreWidgets(@Path(value = "url", encoded = true) String
															path, @Body GetStoreWidgetsRequest
				.Body body, @Header(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("listStores{url}")
		Observable<ListStores> listStores(@Path(value = "url", encoded = true) String path, @Body
		ListStoresRequest.Body body, @Header(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("listSearchApps")
		Observable<ListSearchApps> listSearchApps(@Body ListSearchAppsRequest.Body body, @Header
				(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);
	}
}
