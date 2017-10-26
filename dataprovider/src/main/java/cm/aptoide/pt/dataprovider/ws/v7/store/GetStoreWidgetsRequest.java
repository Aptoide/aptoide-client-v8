/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.AdsApplicationVersionCodeProvider;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 22-04-2016.
 */
public class GetStoreWidgetsRequest
    extends BaseRequestWithStore<GetStoreWidgets, GetStoreWidgetsRequest.Body> {

  private final String url;
  private final StoreCredentials storeCredentials;
  private final String clientUniqueId;
  private final boolean isGooglePlayServicesAvailable;
  private final String partnerId;
  private final boolean accountMature;
  private final String filters;
  private final Resources resources;
  private final WindowManager windowManager;
  private final ConnectivityManager connectivityManager;
  private final AdsApplicationVersionCodeProvider versionCodeProvider;
  private final SharedPreferences sharedPreferences;

  private GetStoreWidgetsRequest(String url, Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      StoreCredentials storeCredentials, String clientUniqueId,
      boolean isGooglePlayServicesAvailable, String partnerId, boolean accountMature,
      String filters, Resources resources, WindowManager windowManager,
      ConnectivityManager connectivityManager,
      AdsApplicationVersionCodeProvider versionCodeProvider) {
    super(body, httpClient, converterFactory, bodyInterceptor, tokenInvalidator, sharedPreferences);
    this.url = url;
    this.storeCredentials = storeCredentials;
    this.clientUniqueId = clientUniqueId;
    this.isGooglePlayServicesAvailable = isGooglePlayServicesAvailable;
    this.partnerId = partnerId;
    this.accountMature = accountMature;
    this.filters = filters;
    this.resources = resources;
    this.windowManager = windowManager;
    this.connectivityManager = connectivityManager;
    this.versionCodeProvider = versionCodeProvider;
    this.sharedPreferences = sharedPreferences;
  }

  public static GetStoreWidgetsRequest ofAction(String url, StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, Resources resources, WindowManager windowManager,
      String clientUniqueId, boolean isGooglePlayServicesAvailable, String partnerId,
      boolean accountMature, String filters, ConnectivityManager connectivityManager,
      AdsApplicationVersionCodeProvider versionCodeProvider) {

    final Body body =
        new Body(storeCredentials, WidgetsArgs.createDefault(resources, windowManager));

    return new GetStoreWidgetsRequest(new V7Url(url).remove("getStoreWidgets")
        .get(), body, bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences, storeCredentials, clientUniqueId, isGooglePlayServicesAvailable,
        partnerId, accountMature, filters, resources, windowManager, connectivityManager,
        versionCodeProvider);
  }

  public String getUrl() {
    return url;
  }

  @Override protected Observable<GetStoreWidgets> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getStoreWidgets(url, body, bypassCache)
        .flatMap(getStoreWidgets -> loadGetStoreWidgets(getStoreWidgets, bypassCache).map(
            wsWidgets -> getStoreWidgets));
  }

  private Observable<List<GetStoreWidgets.WSWidget>> loadGetStoreWidgets(
      GetStoreWidgets getStoreWidgets, boolean bypassCache) {
    return Observable.from(getStoreWidgets.getDataList()
        .getList())
        .observeOn(Schedulers.io())
        .flatMap(wsWidget -> WSWidgetsUtils.loadWidgetNode(wsWidget, storeCredentials, bypassCache,
            clientUniqueId, isGooglePlayServicesAvailable, partnerId, accountMature,
            ((BodyInterceptor<BaseBody>) bodyInterceptor), getHttpClient(), converterFactory, filters,
            getTokenInvalidator(), sharedPreferences, resources, windowManager, connectivityManager,
            versionCodeProvider))
        .toList()
        .flatMapIterable(wsWidgets -> getStoreWidgets.getDataList()
            .getList())
        .toList()
        .first();
  }

  public static class Body extends BaseBodyWithStore implements Endless {

    private WidgetsArgs widgetsArgs;
    private StoreContext context;
    private String storeName;
    private Integer limit;
    private int offset;

    public Body(StoreCredentials storeCredentials, WidgetsArgs widgetsArgs) {
      super(storeCredentials);
      this.widgetsArgs = widgetsArgs;
      this.limit = 5;
    }

    public Body(StoreCredentials storeCredentials, WidgetsArgs widgetsArgs,
        StoreContext storeContext, String storeName) {
      super(storeCredentials);
      this.widgetsArgs = widgetsArgs;
      this.context = storeContext;
      this.storeName = storeName;
    }

    @SuppressWarnings("unused") public WidgetsArgs getWidgetsArgs() {
      return widgetsArgs;
    }

    public StoreContext getContext() {
      return context;
    }

    @Override public String getStoreName() {
      return storeName;
    }

    @Override public int getOffset() {
      return offset;
    }

    @Override public void setOffset(int offset) {
      this.offset = offset;
    }

    @Override public Integer getLimit() {
      return limit;
    }
  }
}
