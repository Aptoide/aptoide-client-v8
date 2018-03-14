package cm.aptoide.pt.dataprovider.ws.v7.home;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.model.v7.Type;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.AdsApplicationVersionCodeProvider;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.dataprovider.ws.v7.store.WidgetsArgs;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class GetHomeBundlesRequest extends V7<GetStoreWidgets, GetHomeBundlesRequest.Body> {

  private final WSWidgetsUtils widgetsUtils;
  private final BaseRequestWithStore.StoreCredentials storeCredentials;
  private final String clientUniqueId;
  private final boolean isGooglePlayServicesAvailable;
  private final String partnerId;
  private final boolean accountMature;
  private final String filters;
  private final SharedPreferences sharedPreferences;
  private final Resources resources;
  private final WindowManager windowManager;
  private final ConnectivityManager connectivityManager;
  private final AdsApplicationVersionCodeProvider versionCodeProvider;
  private boolean bypassServerCache;

  protected GetHomeBundlesRequest(Body body, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      WSWidgetsUtils widgetsUtils, BaseRequestWithStore.StoreCredentials storeCredentials,
      String clientUniqueId, boolean isGooglePlayServicesAvailable, String partnerId,
      boolean accountMature, String filters, Resources resources, WindowManager windowManager,
      ConnectivityManager connectivityManager,
      AdsApplicationVersionCodeProvider versionCodeProvider) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    this.widgetsUtils = widgetsUtils;
    this.storeCredentials = storeCredentials;
    this.clientUniqueId = clientUniqueId;
    this.isGooglePlayServicesAvailable = isGooglePlayServicesAvailable;
    this.partnerId = partnerId;
    this.accountMature = accountMature;
    this.filters = filters;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
    this.windowManager = windowManager;
    this.connectivityManager = connectivityManager;
    this.versionCodeProvider = versionCodeProvider;
  }

  public static GetHomeBundlesRequest of(int limit, int offset, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      WSWidgetsUtils widgetsUtils, BaseRequestWithStore.StoreCredentials storeCredentials,
      String clientUniqueId, boolean isGooglePlayServicesAvailable, String partnerId,
      boolean accountMature, String filters, Resources resources, WindowManager windowManager,
      ConnectivityManager connectivityManager,
      AdsApplicationVersionCodeProvider versionCodeProvider) {
    return new GetHomeBundlesRequest(
        new Body(limit, offset, WidgetsArgs.createDefault(resources, windowManager)), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator, sharedPreferences, widgetsUtils,
        storeCredentials, clientUniqueId, isGooglePlayServicesAvailable, partnerId, accountMature,
        filters, resources, windowManager, connectivityManager, versionCodeProvider);
  }

  private Observable<List<GetStoreWidgets.WSWidget>> loadAppsInBundles(
      GetStoreWidgets getStoreWidgets, boolean bypassCache) {
    return Observable.from(getStoreWidgets.getDataList()
        .getList())
        .observeOn(Schedulers.io())
        .flatMap(wsWidget -> widgetsUtils.loadWidgetNode(wsWidget, storeCredentials, bypassCache,
            clientUniqueId, isGooglePlayServicesAvailable, partnerId, accountMature,
            ((BodyInterceptor<BaseBody>) bodyInterceptor), getHttpClient(), converterFactory,
            filters, getTokenInvalidator(), sharedPreferences, resources, windowManager,
            connectivityManager, versionCodeProvider, bypassServerCache,
            Type.ADS.getPerLineCount(resources, windowManager) * 2))
        .toList()
        .flatMapIterable(wsWidgets -> getStoreWidgets.getDataList()
            .getList())
        .toList()
        .first();
  }

  @Override
  public Observable<GetStoreWidgets> observe(boolean bypassCache, boolean bypassServerCache) {
    this.bypassServerCache = bypassServerCache;
    return super.observe();
  }

  @Override protected Observable<GetStoreWidgets> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getHomeBundles(body, bypassCache)
        .flatMap(getStoreWidgets -> loadAppsInBundles(getStoreWidgets, bypassCache).map(
            wsWidgets -> getStoreWidgets));
  }

  public static class Body extends BaseBody implements Endless {

    private WidgetsArgs widgetsArgs;
    private StoreContext context;
    private Integer limit;
    private long storeId;
    private int offset;

    public Body(Integer limit, int offset, WidgetsArgs widgetsArgs) {
      this.limit = limit;
      this.offset = offset;
      this.widgetsArgs = widgetsArgs;
      this.context = StoreContext.home;
      this.storeId = 15;
    }

    public StoreContext getContext() {
      return context;
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

    public long getStoreId() {
      return storeId;
    }

    public void setStoreId(long storeId) {
      this.storeId = storeId;
    }

    public WidgetsArgs getWidgetsArgs() {
      return widgetsArgs;
    }

    public void setWidgetsArgs(WidgetsArgs widgetsArgs) {
      this.widgetsArgs = widgetsArgs;
    }
  }
}
