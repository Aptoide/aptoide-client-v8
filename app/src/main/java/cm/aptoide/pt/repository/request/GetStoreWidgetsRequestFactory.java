package cm.aptoide.pt.repository.request;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AppCoinsManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.AdsApplicationVersionCodeProvider;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by neuro on 03-01-2017.
 */
class GetStoreWidgetsRequestFactory {

  private final StoreCredentialsProvider storeCredentialsProvider;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final Resources resources;
  private final WindowManager windowManager;
  private final String clientUniqueId;
  private final String partnerId;
  private final AptoideAccountManager accountManager;
  private final String filters;
  private final ConnectivityManager systemService;
  private final AdsApplicationVersionCodeProvider versionCodeProvider;
  private final AppCoinsManager appCoinsManager;

  public GetStoreWidgetsRequestFactory(StoreCredentialsProvider storeCredentialsProvider,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, Resources resources, WindowManager windowManager,
      String clientUniqueId, String partnerId, AptoideAccountManager accountManager, String filters,
      ConnectivityManager systemService, AdsApplicationVersionCodeProvider versionCodeProvider,
      AppCoinsManager appCoinsManager) {
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
    this.windowManager = windowManager;
    this.clientUniqueId = clientUniqueId;
    this.partnerId = partnerId;
    this.accountManager = accountManager;
    this.filters = filters;
    this.systemService = systemService;
    this.versionCodeProvider = versionCodeProvider;
    this.appCoinsManager = appCoinsManager;
  }

  public GetStoreWidgetsRequest newStoreWidgets(String url, boolean googlePlayServicesAvailable) {
    final boolean adultContentEnabled = accountManager.hasMatureContentEnabled()
        .first()
        .toSingle()
        .toBlocking()
        .value();
    return GetStoreWidgetsRequest.ofAction(url, storeCredentialsProvider.fromUrl(url),
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences,
        resources, windowManager, clientUniqueId, googlePlayServicesAvailable, partnerId,
        adultContentEnabled, filters, systemService, versionCodeProvider, appCoinsManager);
  }
}
