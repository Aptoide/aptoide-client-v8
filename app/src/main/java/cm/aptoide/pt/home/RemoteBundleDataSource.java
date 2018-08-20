package cm.aptoide.pt.home;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.AdsApplicationVersionCodeProvider;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.dataprovider.ws.v7.home.GetHomeBundlesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.WidgetsArgs;
import cm.aptoide.pt.install.PackageRepository;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class RemoteBundleDataSource implements BundleDataSource {
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient okHttpClient;
  private final Converter.Factory converterFactory;
  private final BundlesResponseMapper mapper;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final WSWidgetsUtils widgetsUtils;
  private final StoreCredentialsProvider storeCredentialsProvider;
  private final String clientUniqueId;
  private final boolean isGooglePlayServicesAvailable;
  private final String partnerId;
  private final AptoideAccountManager accountManager;
  private final String filters;
  private final Resources resources;
  private final WindowManager windowManager;
  private final ConnectivityManager connectivityManager;
  private final AdsApplicationVersionCodeProvider versionCodeProvider;
  private final int limit;
  private final PackageRepository packageRepository;
  private final int latestPackagesCount;
  private final int randomPackagesCount;
  private Map<String, Integer> total;
  private Map<String, Boolean> loading;

  public RemoteBundleDataSource(int limit, Map<String, Integer> initialTotal,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient okHttpClient,
      Converter.Factory converterFactory, BundlesResponseMapper mapper,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      WSWidgetsUtils widgetsUtils, StoreCredentialsProvider storeCredentialsProvider,
      String clientUniqueId, boolean isGooglePlayServicesAvailable, String partnerId,
      AptoideAccountManager accountManager, String filters, Resources resources,
      WindowManager windowManager, ConnectivityManager connectivityManager,
      AdsApplicationVersionCodeProvider versionCodeProvider, PackageRepository packageRepository,
      int latestPackagesCount, int randomPackagesCount) {
    this.limit = limit;
    this.total = initialTotal;
    this.bodyInterceptor = bodyInterceptor;
    this.okHttpClient = okHttpClient;
    this.converterFactory = converterFactory;
    this.mapper = mapper;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.widgetsUtils = widgetsUtils;
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.clientUniqueId = clientUniqueId;
    this.isGooglePlayServicesAvailable = isGooglePlayServicesAvailable;
    this.partnerId = partnerId;
    this.accountManager = accountManager;
    this.filters = filters;
    this.resources = resources;
    this.windowManager = windowManager;
    this.connectivityManager = connectivityManager;
    this.versionCodeProvider = versionCodeProvider;
    this.packageRepository = packageRepository;
    this.latestPackagesCount = latestPackagesCount;
    this.randomPackagesCount = randomPackagesCount;
    loading = new HashMap<>();
  }

  private Single<HomeBundlesModel> getHomeBundles(int offset, int limit,
      boolean invalidateHttpCache, String key) {
    if (isLoading(key)) {
      return Single.just(new HomeBundlesModel(true));
    }
    final boolean adultContentEnabled = accountManager.enabled()
        .first()
        .toSingle()
        .toBlocking()
        .value();

    return getPackages().flatMap(
        packageNames -> GetHomeBundlesRequest.of(limit, offset, okHttpClient, converterFactory,
            bodyInterceptor, tokenInvalidator, sharedPreferences, widgetsUtils,
            storeCredentialsProvider.fromUrl(""), clientUniqueId, isGooglePlayServicesAvailable,
            partnerId, adultContentEnabled, filters, resources, windowManager, connectivityManager,
            versionCodeProvider, packageNames)
            .observe(invalidateHttpCache, false)
            .doOnSubscribe(() -> loading.put(key, true))
            .doOnUnsubscribe(() -> loading.put(key, false))
            .doOnTerminate(() -> loading.put(key, false))
            .flatMap(homeResponse -> mapHomeResponse(homeResponse, key))
            .toSingle()
            .onErrorReturn(this::createErrorAppsList));
  }

  public GetStoreWidgetsRequest getMoreBundlesRequest(String url, int offset, int limit) {
    final boolean adultContentEnabled = accountManager.enabled()
        .first()
        .toSingle()
        .toBlocking()
        .value();
    BaseRequestWithStore.StoreCredentials storeCredentials = storeCredentialsProvider.fromUrl(url);

    GetStoreWidgetsRequest.Body body = new GetStoreWidgetsRequest.Body(storeCredentials,
        WidgetsArgs.createWithLineMultiplier(resources, windowManager, 3), limit);
    body.setOffset(offset);
    return new GetStoreWidgetsRequest(new V7Url(url).remove("getStoreWidgets")
        .get(), body, bodyInterceptor, okHttpClient, converterFactory, tokenInvalidator,
        sharedPreferences, storeCredentials, clientUniqueId, isGooglePlayServicesAvailable,
        partnerId, adultContentEnabled, filters, resources, windowManager, connectivityManager,
        versionCodeProvider, new WSWidgetsUtils());
  }

  private Single<List<String>> getPackages() {
    return Observable.concat(packageRepository.getLatestInstalledPackages(latestPackagesCount),
        packageRepository.getRandomInstalledPackages(randomPackagesCount))
        .toList()
        .toSingle();
  }

  @NonNull private HomeBundlesModel createErrorAppsList(Throwable throwable) {
    if (throwable instanceof NoNetworkConnectionException) {
      return new HomeBundlesModel(HomeBundlesModel.Error.NETWORK);
    } else {
      return new HomeBundlesModel(HomeBundlesModel.Error.GENERIC);
    }
  }

  @NonNull
  private Observable<HomeBundlesModel> mapHomeResponse(GetStoreWidgets homeResponse, String key) {
    if (homeResponse.isOk()) {
      List<HomeBundle> homeBundles = mapper.fromWidgetsToBundles(homeResponse.getDataList()
          .getList());
      homeBundles = removeEmptyBundles(homeBundles);
      int responseBundletotal = homeResponse.getDataList()
          .getTotal();
      total.put(key, responseBundletotal);
      return Observable.just(new HomeBundlesModel(homeBundles, false, homeResponse.getDataList()
          .getNext()));
    }
    return Observable.error(
        new IllegalStateException("Could not obtain home bundles from server."));
  }

  @Override public Single<HomeBundlesModel> loadFreshHomeBundles(String key) {
    return getHomeBundles(0, limit, true, key);
  }

  @Override public Single<HomeBundlesModel> loadNextHomeBundles(int offset, int limit, String key) {
    return getHomeBundles(offset, limit, false, key);
  }

  @Override public boolean hasMore(Integer offset, String key) {
    return offset < getTotal(key) && !isLoading(key);
  }

  @Override public Single<HomeBundlesModel> loadFreshBundleForEvent(String url, String key) {
    return getEventBundles(url, true, key, 0, limit);
  }

  @Override
  public Single<HomeBundlesModel> loadNextBundleForEvent(String url, int offset, String key,
      int limit) {
    return getEventBundles(url, false, key, offset, limit);
  }

  private Single<HomeBundlesModel> getEventBundles(String url, boolean invalidateHttpCache,
      String key, int offset, int limit) {
    if (isLoading(key)) {
      return Single.just(new HomeBundlesModel(true));
    }
    String newUrl = url.replace(V7.getHost(sharedPreferences), "");
    return getMoreBundlesRequest(newUrl, offset, limit).observe(invalidateHttpCache, false)
        .doOnSubscribe(() -> loading.put(key, true))
        .doOnUnsubscribe(() -> loading.put(key, false))
        .doOnTerminate(() -> loading.put(key, false))
        .flatMap(homeResponse -> mapHomeResponse(homeResponse, key))
        .toSingle()
        .onErrorReturn(this::createErrorAppsList);
  }

  private boolean isLoading(String key) {
    return (loading.containsKey(key) && loading.get(key));
  }

  private int getTotal(String key) {
    if (total.containsKey(key)) {
      return total.get(key);
    } else {
      return Integer.MAX_VALUE;
    }
  }

  private List<HomeBundle> removeEmptyBundles(List<HomeBundle> homeBundles) {
    List<HomeBundle> newHomeBundleList = new ArrayList<>();
    for (HomeBundle homeBundle : homeBundles) {
      if (homeBundle.getType()
          .isApp() && !homeBundle.getContent()
          .isEmpty()) {
        newHomeBundleList.add(homeBundle);
      } else if (!homeBundle.getType()
          .isApp()) {
        newHomeBundleList.add(homeBundle);
      }
    }
    return newHomeBundleList;
  }
}
