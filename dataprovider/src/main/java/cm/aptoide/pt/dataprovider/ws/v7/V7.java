/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
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
import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.model.v7.store.GetStoreTabs;
import cm.aptoide.pt.model.v7.store.GetStoreWidgets;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.networkclient.WebService;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by neuro on 19-04-2016.
 */
public abstract class V7<U> extends WebService<V7.Interfaces, U> {

	public static final String BASE_HOST = "http://ws75.aptoide.com/api/7/";

	protected V7() {
		super(Interfaces.class);
	}

	@Override
	protected String getBaseHost() {
		return BASE_HOST;
	}

	public interface Interfaces {

		@POST("getApp")
		Observable<GetApp> getApp(@Body GetAppRequest.Body body);

		@POST("listApps")
		Observable<ListApps> listApps(@Body ListAppsRequest.Body body);

		@POST("listAppsUpdates")
		Observable<ListAppsUpdates> listAppsUpdates(@Body ListAppsUpdatesRequest.Body body);

		@POST("listAppVersions")
		Observable<ListAppVersions> listAppVersions(@Body ListAppVersionsRequest.Body body);

		@POST("getStore")
		Observable<GetStore> getStore(@Body GetStoreRequest.Body body);

		@POST("getStoreMeta")
		Observable<GetStoreMeta> getStoreMeta(@Body GetStoreMetaRequest.Body body);

		@POST("getStoreDisplays")
		Observable<GetStoreDisplays> getStoreDisplays(@Body GetStoreDisplaysRequest.Body body);

		@POST("getStoreTabs")
		Observable<GetStoreTabs> getStoreTabs(@Body GetStoreTabsRequest.Body body);

		@POST("getStoreWidgets")
		Observable<GetStoreWidgets> getStoreWidgets(@Body GetStoreWidgetsRequest.Body body);

		@POST("listStores")
		Observable<ListStores> listStores(@Body ListStoresRequest.Body body);

		@POST("listSearchApps")
		Observable<ListSearchApps> listSearchApps(@Body ListSearchAppsRequest.Body body);

		@GET
		Observable<ListApps> listApps(@Url String url);

		@GET
		Observable<ListStores> listStores(@Url String url);

		@GET
		Observable<GetStoreDisplays> getStoreDisplays(@Url String url);
	}
}
