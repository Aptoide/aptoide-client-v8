package cm.aptoide.pt.aptoidesdk.ads;

import android.content.Context;
import android.support.annotation.NonNull;
import cm.aptoide.pt.aptoidesdk.BuildConfig;
import cm.aptoide.pt.aptoidesdk.entities.App;
import cm.aptoide.pt.aptoidesdk.entities.SearchResult;
import cm.aptoide.pt.aptoidesdk.parser.Parsers;
import cm.aptoide.pt.aptoidesdk.proxys.GetAdsProxy;
import cm.aptoide.pt.aptoidesdk.proxys.ListSearchAppsProxy;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by neuro on 21-10-2016.
 */

public class Aptoide {

  private static final GetAdsProxy getAdsProxy = new GetAdsProxy();
  private static final ListSearchAppsProxy listSearchAppsProxy = new ListSearchAppsProxy();
  private static String aptoideClientUUID;
  private static String oemid;
  private static String MISSED_INTEGRATION_MESSAGE =
      "Aptoide not integrated, did you forget to call Aptoide.integrate()?";

  private Aptoide() {
  }

  public static App getApp(Ad ad) {
    return getAppObservable(ad).toBlocking().first();
  }

  public static App getApp(SearchResult searchResult) {
    return getAppObservable(searchResult.getId()).toBlocking().first();
  }

  public static Observable<App> getAppObservable(Ad ad) {
    return getAppObservable(ad.data.appId).map(app -> {
      handleAds(ad).subscribe(t -> {
      }, throwable -> Logger.w(TAG, "Error extracting referrer.", throwable));
      return app;
    });
  }

  @NonNull private static Observable<Object> handleAds(Ad ad) {
    return Observable.fromCallable(() -> {
      ReferrerUtils.knockCpc(ad);
      return new Object();
    })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(o -> ReferrerUtils.extractReferrer(ad, ReferrerUtils.RETRIES, false));
  }

  public static App getApp(String packageName, String storeName) {
    return getAppObservable(packageName, storeName).toBlocking().first();
  }

  private static Observable<App> getAppObservable(String packageName, String storeName) {
    return GetAppRequest.of(packageName, storeName, null, aptoideClientUUID)
        .observe()
        .map(App::fromGetApp);
  }

  public static App getApp(long appId) {
    return getAppObservable(appId).toBlocking().first();
  }

  private static Observable<App> getAppObservable(long appId) {
    return GetAppRequest.of(appId, null, aptoideClientUUID).observe().map(App::fromGetApp);
  }

  public static Context getContext() {
    if (AptoideUtils.getContext() == null) {
      throw new RuntimeException(MISSED_INTEGRATION_MESSAGE);
    }

    return AptoideUtils.getContext();
  }

  public static String getOemid() {
    if (AptoideUtils.getContext() == null) {
      throw new RuntimeException(MISSED_INTEGRATION_MESSAGE);
    }

    return oemid;
  }

  public static void integrate(Context context, String oemid) {
    AptoideUtils.setContext(context);
    setUserAgent();

    Aptoide.oemid = oemid;
    Aptoide.aptoideClientUUID =
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(context),
            context).getAptoideClientUUID();

    Logger.setDBG(BuildConfig.DEBUG);
  }

  private static void setUserAgent() {
    SecurePreferences.setUserAgent(AptoideUtils.NetworkUtils.getDefaultUserAgent(
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(), getContext()),
        () -> null));
  }

  public static List<Ad> getAds(int limit) {
    return ReferrerUtils.parseAds(getAdsProxy.getAds(limit, aptoideClientUUID))
        .toBlocking()
        .first();
  }

  public static List<Ad> getAds(int limit, boolean mature) {
    return ReferrerUtils.parseAds(getAdsProxy.getAds(limit, mature, aptoideClientUUID))
        .toBlocking()
        .first();
  }

  public static List<Ad> getAds(int limit, List<String> keyword) {
    return ReferrerUtils.parseAds(getAdsProxy.getAds(limit, aptoideClientUUID, keyword))
        .toBlocking()
        .first();
  }

  public static List<Ad> getAds(int limit, List<String> keyword, boolean mature) {
    return ReferrerUtils.parseAds(getAdsProxy.getAds(limit, mature, aptoideClientUUID, keyword))
        .toBlocking()
        .first();
  }

  public static List<SearchResult> searchApps(String query) {
    return listSearchAppsProxy.search(query, aptoideClientUUID)
        .map(Parsers::parse)
        .onErrorReturn(throwable -> new LinkedList<>())
        .toBlocking()
        .first();
  }

  public static List<SearchResult> searchApps(String query, String storeName) {
    return listSearchAppsProxy.search(query, storeName, aptoideClientUUID)
        .map(Parsers::parse)
        .onErrorReturn(throwable -> new LinkedList<>())
        .toBlocking()
        .first();
  }
}
