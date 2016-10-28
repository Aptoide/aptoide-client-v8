package cm.aptoide.pt.aptoidesdk.ads;

import android.content.Context;
import android.support.annotation.NonNull;
import cm.aptoide.pt.aptoidesdk.entities.App;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.logger.Logger;
import java.util.concurrent.Callable;
import rx.Observable;
import rx.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by neuro on 21-10-2016.
 */

public class Aptoide {

  private static Context context;

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
    return GetAppRequest.of(packageName, storeName, null).observe().map(App::fromGetApp);
  }

  public static App getApp(long appId) {
    return getAppObservable(appId).toBlocking().first();
  }

  private static Observable<App> getAppObservable(long appId) {
    return GetAppRequest.of(appId, null).observe().map(App::fromGetApp);
  }

  public static Context getContext() {
    if (context == null) {
      throw new RuntimeException(
          "Aptoide not integrated, did you forget to call Aptoide.integrate()?");
    }

    return context;
  }

  public static void integrate(Context context) {
    Aptoide.context = context;
  }
}
