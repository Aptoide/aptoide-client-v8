package cm.aptoide.pt.aptoidesdk.ads;

import android.content.Context;
import android.support.annotation.NonNull;
import cm.aptoide.pt.aptoidesdk.entities.App;
import cm.aptoide.pt.aptoidesdk.entities.SearchResult;
import cm.aptoide.pt.aptoidesdk.proxys.GetAdsProxy;
import cm.aptoide.pt.aptoidesdk.proxys.ListSearchAppsProxy;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.List;
import java.util.concurrent.Callable;
import rx.Observable;
import rx.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by neuro on 21-10-2016.
 */

public class Aptoide {

  private static final GetAdsProxy getAdsProxy = new GetAdsProxy();
  private static final ListSearchAppsProxy listSearchAppsProxy = new ListSearchAppsProxy();
  private static String aptoideClientUUID;

  public static App getApp(Ad ad) {
    return getAppObservable(ad).toBlocking().first();
  }

  public static Observable<App> getAppObservable(Ad ad) {
    return getAppObservable(ad.data.appId).map(app -> {
      Observable.fromCallable(handleAds(ad)).subscribeOn(Schedulers.io()).subscribe(t -> {
      }, throwable -> Logger.w(TAG, "Error extracting referrer.", throwable));
      return app;
    });
  }

  @NonNull private static Callable<Object> handleAds(Ad ad) {
    return () -> {
      ReferrerUtils.knockCpc(ad);
      ReferrerUtils.extractReferrer(ad, ReferrerUtils.RETRIES, false);
      return null;
    };
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
      throw new RuntimeException(
          "Aptoide not integrated, did you forget to call Aptoide.integrate()?");
    }

    return AptoideUtils.getContext();
  }

  public static void integrate(Context context) {
    AptoideUtils.setContext(context);
    setUserAgent();

    aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(context),
        context).getAptoideClientUUID();
  }

  private static void setUserAgent() {
    SecurePreferences.setUserAgent(AptoideUtils.NetworkUtils.getDefaultUserAgent(
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(), getContext()),
        () -> null));
  }

  public static List<Ad> getAds(int limit, boolean mature) {
    getAdsProxy.getAds(limit, mature, aptoideClientUUID);
    return null;
  }

  public static List<Ad> getAds(int limit, List<String> keyword) {
    getAdsProxy.getAds(limit, aptoideClientUUID, keyword);
    return null;
  }

  public static List<Ad> getAds(int limit, boolean mature, List<String> keyword) {
    getAdsProxy.getAds(limit, mature, aptoideClientUUID, keyword);
    return null;
  }

  public static List<Ad> getAds(int limit) {
    getAdsProxy.getAds(limit, aptoideClientUUID);
    return null;
  }

  public static List<SearchResult> search(String query) {
    return listSearchAppsProxy.search(query, aptoideClientUUID);
  }
}
