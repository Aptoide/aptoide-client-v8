package cm.aptoide.pt.dataprovider.ws.v7.store;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.view.WindowManager;
import cm.aptoide.pt.AppCoinsManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.model.v7.Type;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.AdsApplicationVersionCodeProvider;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 27/02/2017.
 */

public class GetUserRequest extends V7<GetStore, GetUserRequest.Body> {

  private final OkHttpClient httpClient;
  private final TokenInvalidator tokenInvalidator;
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
  private final AppCoinsManager appCoinsManager;
  private String url;
  private boolean bypassServerCache;

  public GetUserRequest(String url, Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      BaseRequestWithStore.StoreCredentials storeCredentials, String clientUniqueId,
      boolean isGooglePlayServicesAvailable, String partnerId, boolean accountMature,
      String filters, SharedPreferences sharedPreferences1, Resources resources,
      WindowManager windowManager, ConnectivityManager connectivityManager,
      AdsApplicationVersionCodeProvider versionCodeProvider, AppCoinsManager appCoinsManager) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    this.url = url;
    this.httpClient = httpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.storeCredentials = storeCredentials;
    this.clientUniqueId = clientUniqueId;
    this.isGooglePlayServicesAvailable = isGooglePlayServicesAvailable;
    this.partnerId = partnerId;
    this.accountMature = accountMature;
    this.filters = filters;
    this.sharedPreferences = sharedPreferences1;
    this.resources = resources;
    this.windowManager = windowManager;
    this.connectivityManager = connectivityManager;
    this.versionCodeProvider = versionCodeProvider;
    this.appCoinsManager = appCoinsManager;
  }

  public static GetUserRequest of(String url,
      BaseRequestWithStore.StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, Resources resources, WindowManager windowManager,
      String clientUniqueId, boolean isGooglePlayServicesAvailable, String partnerId,
      boolean accountMature, String filters, ConnectivityManager connectivityManager,
      AdsApplicationVersionCodeProvider versionCodeProvider, AppCoinsManager appCoinsManager) {
    final GetUserRequest.Body body =
        new GetUserRequest.Body(WidgetsArgs.createDefault(resources, windowManager));
    return new GetUserRequest(new V7Url(url).remove("user/get")
        .get(), body, bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences, storeCredentials, clientUniqueId, isGooglePlayServicesAvailable,
        partnerId, accountMature, filters, sharedPreferences, resources, windowManager,
        connectivityManager, versionCodeProvider, appCoinsManager);
  }

  @Override
  protected Observable<GetStore> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getUser(url, body, bypassCache)
        .flatMap(getStore -> loadGetStoreWidgets(getStore).map(wsWidgets -> getStore));
  }

  @Override public Observable<GetStore> observe(boolean bypassCache, boolean bypassServerCache) {
    this.bypassServerCache = bypassServerCache;
    return super.observe(bypassCache, bypassServerCache);
  }

  protected Observable<List<GetStoreWidgets.WSWidget>> loadGetStoreWidgets(
      GetStore getStoreWidgets) {
    return Observable.from(getStoreWidgets.getNodes()
            .getWidgets()
            .getDataList()
            .getList())
        .observeOn(Schedulers.io())
        .flatMap(wsWidget -> {
          WSWidgetsUtils widgetsUtils = new WSWidgetsUtils();
          return widgetsUtils.loadWidgetNode(wsWidget, storeCredentials, false, clientUniqueId,
              isGooglePlayServicesAvailable, partnerId, accountMature,
              ((BodyInterceptor<BaseBody>) bodyInterceptor), httpClient, converterFactory, filters,
              tokenInvalidator, sharedPreferences, resources, windowManager, connectivityManager,
              versionCodeProvider, bypassServerCache,
              Type.ADS.getPerLineCount(resources, windowManager), Collections.emptyList(),
              appCoinsManager);
        })
        .toList()
        .flatMapIterable(wsWidgets -> getStoreWidgets.getNodes()
            .getWidgets()
            .getDataList()
            .getList())
        .toList()
        .first();
  }

  public static class Body extends BaseBody {
    private WidgetsArgs widgetsArgs;

    public Body(WidgetsArgs widgetsArgs) {
      this.widgetsArgs = widgetsArgs;
    }

    public WidgetsArgs getWidgetsArgs() {
      return widgetsArgs;
    }
  }
}
