package cm.aptoide.pt.aptoidesdk.ads;

import android.content.Context;
import android.support.annotation.NonNull;
import cm.aptoide.pt.aptoidesdk.Ad;
import cm.aptoide.pt.aptoidesdk.BuildConfig;
import cm.aptoide.pt.aptoidesdk.entities.App;
import cm.aptoide.pt.aptoidesdk.entities.EntitiesFactory;
import cm.aptoide.pt.aptoidesdk.entities.SearchResult;
import cm.aptoide.pt.aptoidesdk.entities.misc.Group;
import cm.aptoide.pt.aptoidesdk.misc.RemoteLogger;
import cm.aptoide.pt.aptoidesdk.parser.Parsers;
import cm.aptoide.pt.aptoidesdk.proxys.GetAdsProxy;
import cm.aptoide.pt.aptoidesdk.proxys.GetAppProxy;
import cm.aptoide.pt.aptoidesdk.proxys.ListSearchAppsProxy;
import cm.aptoide.pt.dataprovider.DatalistEndlessController;
import cm.aptoide.pt.dataprovider.interfaces.EndlessController;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static cm.aptoide.pt.aptoidesdk.ads.OemIds.INDUS;

/**
 * Created by neuro on 10-11-2016.
 */
public class RxAptoide {

  private static final String TAG = RxAptoide.class.getSimpleName();

  private static final GetAdsProxy getAdsProxy = new GetAdsProxy();
  private static final ListSearchAppsProxy listSearchAppsProxy = new ListSearchAppsProxy();
  private static final GetAppProxy getAppProxy = new GetAppProxy();
  @Getter private static boolean debug = false;

  private static String aptoideClientUUID;
  private static String oemid;
  private static String MISSED_INTEGRATION_MESSAGE =
      "Aptoide not integrated, did you forget to call Aptoide.integrate()?";

  private RxAptoide() {
  }

  public static void setDebug(boolean debug) {
    RxAptoide.debug = debug;

    Logger.setDBG(debug);
    RemoteLogger.setDebug(debug);
    WebService.setDebug(debug);
  }

  public static void integrate(Context context, String oemid) {
    if (context == null) {
      throw new IllegalArgumentException("Context cannot be null!");
    }

    if (oemid == null) {
      throw new IllegalArgumentException("Oemid cannot be null!");
    }

    setupEndpointsBasedOnOemId(oemid);
    setupForcedCountryBasedOnOemId(oemid);

    AptoideUtils.setContext(context);
    RxAptoide.oemid = oemid;

    Observable.defer(() -> {
      setUserAgent(oemid);
      RxAptoide.aptoideClientUUID =
          new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(context),
              context).getAptoideClientUUID();
      return null;
    }).subscribeOn(Schedulers.io()).subscribe(o -> {
    }, RemoteLogger.getInstance()::log);

    setDebug(debug);
  }

  private static void setupEndpointsBasedOnOemId(String oemid) {
    switch (oemid) {
      case INDUS:
        GetAdsRequest.setBaseUrl("http://indusos.aptoide.com/api/2/");
        break;
    }
  }

  private static void setupForcedCountryBasedOnOemId(String oemid) {
    switch (oemid) {
      case INDUS:
        GetAdsRequest.setForcedCountry("IN");
        break;
    }
  }

  private static void setUserAgent(String oemid) {
    SecurePreferences.setUserAgent(AptoideUtils.NetworkUtils.getDefaultUserAgent(
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(), getContext()),
        () -> null, "aptoidesdk-" + BuildConfig.VERSION_NAME, oemid));
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

  public static Observable<App> getApp(String packageName, String storeName) {
    handleOrganicAds(packageName);

    return getAppProxy.getApp(packageName, storeName, aptoideClientUUID)
        .map(EntitiesFactory::createApp).onErrorReturn(throwable -> {
          RemoteLogger.getInstance().log(throwable);
          return null;
        });
  }

  public static Observable<App> getApp(Ad ad) {
    handleAds(ad).subscribe(t -> {
    }, throwable -> {
      Logger.w(TAG, "Error extracting referrer.", throwable);
      RemoteLogger.getInstance().log(throwable);
    });

    return getAppProxy.getApp(ad.getAppId(), aptoideClientUUID)
        .map(EntitiesFactory::createApp).onErrorReturn(throwable -> {
          RemoteLogger.getInstance().log(throwable);
          return null;
        });
  }

  public static Observable<App> getApp(long appId) {
    return getAppProxy.getApp(appId, aptoideClientUUID)
        .map(EntitiesFactory::createApp).doOnNext(app -> handleOrganicAds(app.getPackageName()))
        .onErrorReturn(throwable -> {
          RemoteLogger.getInstance().log(throwable);
          return null;
        });
  }

  public static Observable<List<Ad>> getAds(int limit) {
    return getAdsProxy.getAds(limit, aptoideClientUUID)
        .map(ReferrerUtils::parse).onErrorReturn(throwable -> {
          RemoteLogger.getInstance().log(throwable);
          return new LinkedList<>();
        });
  }

  public static Observable<List<Ad>> getAds(int limit, boolean mature) {
    return getAdsProxy.getAds(limit, mature, aptoideClientUUID)
        .map(ReferrerUtils::parse).onErrorReturn(throwable -> {
          RemoteLogger.getInstance().log(throwable);
          return new LinkedList<>();
        });
  }

  public static Observable<List<Ad>> getAds(int limit, List<String> keyword) {
    return getAdsProxy.getAds(limit, aptoideClientUUID, keyword)
        .map(ReferrerUtils::parse).onErrorReturn(throwable -> {
          RemoteLogger.getInstance().log(throwable);
          return new LinkedList<>();
        });
  }

  public static Observable<List<Ad>> getAds(int limit, List<String> keyword, boolean mature) {
    return getAdsProxy.getAds(limit, mature, aptoideClientUUID, keyword)
        .map(ReferrerUtils::parse).onErrorReturn(throwable -> {
          RemoteLogger.getInstance().log(throwable);
          return new LinkedList<>();
        });
  }

  public static Observable<List<SearchResult>> searchApps(String query) {
    return listSearchAppsProxy.search(query, aptoideClientUUID)
        .map(Parsers::parse).onErrorReturn(throwable -> {
          RemoteLogger.getInstance().log(throwable);
          return new LinkedList<>();
        });
  }

  public static Observable<List<SearchResult>> searchApps(String query, String storeName) {
    return listSearchAppsProxy.search(query, storeName, aptoideClientUUID)
        .map(Parsers::parse).onErrorReturn(throwable -> {
          RemoteLogger.getInstance().log(throwable);
          return new LinkedList<>();
        });
  }

  public static EndlessController<App> listApps(Group group) {
    return new DatalistEndlessController<>(ListAppsRequest.of(group.getId()),
        EntitiesFactory::createApp);
  }

  private static Subscription handleOrganicAds(String packageName) {
    return getAdsProxy.getAds(packageName, aptoideClientUUID)
        .filter(ReferrerUtils::hasAds)
        .map(ReferrerUtils::parse).flatMap(ads -> handleAds(ads.get(0)))
        .onErrorReturn(throwable -> {
          RemoteLogger.getInstance().log(throwable);
          return new LinkedList<>();
        })
        .subscribe();
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
}
