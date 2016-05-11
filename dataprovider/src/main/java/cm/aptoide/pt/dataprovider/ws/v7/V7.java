/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 11/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppVersionsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreDisplaysRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreTabsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
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
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by neuro on 19-04-2016.
 */
public abstract class V7<U> extends WebService<V7.Interfaces, U> {

	public static final String BASE_HOST = "http://ws75.aptoide.com/api/7/";

	protected V7(boolean bypassCache) {
		super(Interfaces.class, bypassCache);
	}

	@Override
	protected String getBaseHost() {
		return BASE_HOST;
	}

	public interface Interfaces {

		@POST("getApp")
		Observable<GetApp> getApp(@Body GetAppRequest.Body body, @Header(RequestCache
				.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("listApps{url}")
		Observable<ListApps> listApps(@Path(value = "url", encoded = true) String path, @Body ListAppsRequest.Body body, @Header(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);

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
