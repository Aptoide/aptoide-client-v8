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
import cm.aptoide.pt.dataprovider.ws.v7.GetRecommendedRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ListFullReviewsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetRecommendedStoresRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetUserRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.utils.AptoideUtils;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by neuro on 26-12-2016.
 * <p>
 * Deprecated since some injected variables such as accountMature are susceptible to change between
 * calls and
 */
@Deprecated public class RequestFactory {

  private final ListStoresRequestFactory listStoresRequestFactory;
  private final ListAppsRequestFactory listAppsRequestFactory;
  private final ListFullReviewsRequestFactory listFullReviewsRequestFactory;
  private final GetStoreRequestFactory getStoreRequestFactory;
  private final GetStoreWidgetsRequestFactory getStoreWidgetsRequestFactory;
  private final StoreCredentialsProvider storeCredentialsProvider;
  private final GetUserRequestFactory getUserRequestFactory;
  private final GetStoreRecommendedRequestFactory getStoreRecommendedRequestFactory;
  private final GetRecommendedRequestFactory getRecommendedRequestFactory;
  private final boolean googlePlayServicesAvailable;
  private final AppCoinsManager appCoinsManager;

  public RequestFactory(StoreCredentialsProvider storeCredentialsProvider,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, Resources resources, WindowManager windowManager,
      String clientUniqueId, String partnerId, AptoideAccountManager accountManager, String filters,
      ConnectivityManager connectivityManager,
      AdsApplicationVersionCodeProvider versionCodeProvider, boolean googlePlayServicesAvailable,
      AppCoinsManager appCoinsManager) {
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.googlePlayServicesAvailable = googlePlayServicesAvailable;
    this.appCoinsManager = appCoinsManager;
    listStoresRequestFactory =
        new ListStoresRequestFactory(bodyInterceptor, httpClient, converterFactory,
            tokenInvalidator, sharedPreferences);
    listAppsRequestFactory =
        new ListAppsRequestFactory(bodyInterceptor, storeCredentialsProvider, httpClient,
            converterFactory, tokenInvalidator, sharedPreferences, resources, windowManager);
    listFullReviewsRequestFactory =
        new ListFullReviewsRequestFactory(bodyInterceptor, httpClient, converterFactory,
            tokenInvalidator, sharedPreferences);
    getStoreRequestFactory =
        new GetStoreRequestFactory(storeCredentialsProvider, bodyInterceptor, httpClient,
            converterFactory, tokenInvalidator, sharedPreferences, resources, windowManager);
    getStoreWidgetsRequestFactory =
        new GetStoreWidgetsRequestFactory(storeCredentialsProvider, bodyInterceptor, httpClient,
            converterFactory, tokenInvalidator, sharedPreferences, resources, windowManager,
            clientUniqueId, partnerId, accountManager, filters, connectivityManager,
            versionCodeProvider, this.appCoinsManager);
    getUserRequestFactory =
        new GetUserRequestFactory(bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
            sharedPreferences, resources, windowManager, storeCredentialsProvider, clientUniqueId,
            partnerId, accountManager, filters, connectivityManager, versionCodeProvider, appCoinsManager);

    getStoreRecommendedRequestFactory =
        new GetStoreRecommendedRequestFactory(bodyInterceptor, httpClient, converterFactory,
            tokenInvalidator, sharedPreferences);
    getRecommendedRequestFactory =
        new GetRecommendedRequestFactory(bodyInterceptor, httpClient, converterFactory,
            tokenInvalidator, sharedPreferences);
  }

  @Deprecated public ListStoresRequest newListStoresRequest(int offset, int limit) {
    return this.listStoresRequestFactory.newListStoresRequest(offset, limit);
  }

  @Deprecated public ListStoresRequest newListStoresRequest(String url) {
    return this.listStoresRequestFactory.newListStoresRequest(url);
  }

  @Deprecated public ListAppsRequest newListAppsRequest(String url) {
    return this.listAppsRequestFactory.newListAppsRequest(url);
  }

  @Deprecated public ListFullReviewsRequest newListFullReviews(String url, boolean refresh) {
    return this.listFullReviewsRequestFactory.newListFullReviews(url, refresh,
        storeCredentialsProvider.fromUrl(url));
  }

  @Deprecated public GetStoreRequest newStore(String url) {
    return this.getStoreRequestFactory.newStore(url);
  }

  @Deprecated public GetStoreWidgetsRequest newStoreWidgets(String url) {
    return this.getStoreWidgetsRequestFactory.newStoreWidgets(url, googlePlayServicesAvailable);
  }

  @Deprecated public GetUserRequest newGetUser(String url) {
    return this.getUserRequestFactory.newGetUser(url, googlePlayServicesAvailable);
  }

  @Deprecated public GetRecommendedStoresRequest newGetRecommendedStores(String url) {
    return this.getStoreRecommendedRequestFactory.newRecommendedStore(url);
  }

  @Deprecated public GetRecommendedRequest newGetRecommendedRequest(int limit, String packageName) {
    return this.getRecommendedRequestFactory.newGetRecommendedRequest(limit, packageName);
  }
}
