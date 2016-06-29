/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.dataprovider.util.ToRetryThrowable;
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
import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.cache.RequestCache;
import cm.aptoide.pt.preferences.Application;
import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 19-04-2016.
 */
public abstract class V7<U, B extends BaseBody> extends WebService<V7.Interfaces, U> {

	public static final String BASE_HOST = "http://ws75.aptoide.com/api/7/";
	@Getter protected final B body;
	private final String INVALID_ACCESS_TOKEN_CODE = "AUTH-2";
	private boolean accessTokenRetry = false;

	protected V7(B body, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost) {
		super(Interfaces.class, httpClient, converterFactory, baseHost);
		this.body = body;
	}

	@Override
	public Observable<U> observe(boolean bypassCache) {
		return handleToken(retryOnTicket(super.observe(bypassCache)), bypassCache);
	}

	private Observable<U> retryOnTicket(Observable<U> observable) {
		return observable.subscribeOn(Schedulers.io()).flatMap(t -> {
			if (((BaseV7Response) t).getInfo().getStatus().equals(BaseV7Response.Info.Status.QUEUED)) {
				return Observable.error(new ToRetryThrowable());
			} else {
				return Observable.just(t);
			}
		}).retryWhen(observable1 -> observable1.zipWith(Observable.range(1, 3), (n, i) -> {
			if ((n instanceof ToRetryThrowable) && i < 3) {
				return i;
			} else {
				// Todo: quando nao é erro de net, isto induz em erro lol
				if (isNoNetworkException(n)) {
					// Don't retry
					throw new NoNetworkConnectionException(n);
				} else {
					try {
						if (n instanceof HttpException) {
							throw new AptoideWsV7Exception(n).setBaseResponse((BaseV7Response)
									converterFactory.responseBodyConverter(BaseV7Response.class, null, null)
											.convert(((HttpException) n).response().errorBody()));
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

					return Observable.error(n);
				}
			}
		}).delay(500, TimeUnit.MILLISECONDS));
	}

	private Observable<U> handleToken(Observable<U> observable, boolean bypassCache) {
		return observable.onErrorResumeNext(throwable -> {
			if (throwable instanceof AptoideWsV7Exception) {
				if (INVALID_ACCESS_TOKEN_CODE.equals(((AptoideWsV7Exception) throwable).getBaseResponse()
						.getError()
						.getCode())) {

					if (!accessTokenRetry) {
						accessTokenRetry = true;
						return AptoideAccountManager.invalidateAccessToken(Application.getContext()).flatMap(new Func1<String,Observable<? extends U>>() {
							@Override
							public Observable<? extends U> call(String s) {
								V7.this.body.setAccessToken(s);
								return V7.this.observe(bypassCache);
							}
						});
					}
				} else {
					return Observable.error(throwable);
				}
			}
			return Observable.error(throwable);
		});
	}

	public interface Interfaces {

		@POST("getApp")
		Observable<GetApp> getApp(@Body GetAppRequest.Body body, @Header(RequestCache.BYPASS_HEADER_KEY) boolean
				bypassCache);

		@POST("listApps{url}")
		Observable<ListApps> listApps(@Path(value = "url", encoded = true) String path, @Body ListAppsRequest.Body
				body, @Header(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("listAppsUpdates")
		Observable<ListAppsUpdates> listAppsUpdates(@Body ListAppsUpdatesRequest.Body body, @Header(RequestCache
				.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("listAppVersions")
		Observable<ListAppVersions> listAppVersions(@Body ListAppVersionsRequest.Body body, @Header(RequestCache
				.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("getStore{url}")
		Observable<GetStore> getStore(@Path(value = "url", encoded = true) String path, @Body GetStoreRequest.Body
				body, @Header(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("getStoreMeta")
		Observable<GetStoreMeta> getStoreMeta(@Body GetStoreMetaRequest.Body body, @Header(RequestCache
				.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("getStoreDisplays{url}")
		Observable<GetStoreDisplays> getStoreDisplays(@Path(value = "url", encoded = true) String path, @Body
		GetStoreDisplaysRequest.Body body, @Header(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("getStoreTabs")
		Observable<GetStoreTabs> getStoreTabs(@Body GetStoreTabsRequest.Body body, @Header(RequestCache
				.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("getStoreWidgets{url}")
		Observable<GetStoreWidgets> getStoreWidgets(@Path(value = "url", encoded = true) String path, @Body
		GetStoreWidgetsRequest.Body body, @Header(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("listStores{url}")
		Observable<ListStores> listStores(@Path(value = "url", encoded = true) String path, @Body ListStoresRequest
				.Body body, @Header(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("listSearchApps")
		Observable<ListSearchApps> listSearchApps(@Body ListSearchAppsRequest.Body body, @Header(RequestCache
				.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("getUserTimeline")
		Observable<GetUserTimeline> getUserTimeline(@Body GetUserTimelineRequest.Body body, @Header(RequestCache
				.BYPASS_HEADER_KEY) boolean bypassCache);
	}
}
