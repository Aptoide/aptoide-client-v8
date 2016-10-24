package cm.aptoide.pt.aptoidesdk;

import cm.aptoide.pt.aptoidesdk.entities.Ad;
import cm.aptoide.pt.aptoidesdk.entities.AdController;
import cm.aptoide.pt.aptoidesdk.entities.App;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import rx.Observable;

/**
 * Created by neuro on 21-10-2016.
 */

public class Aptoide {
  public static App getApp(Ad ad) {
    return getAppObservable(ad).toBlocking().first();
  }

  public static Observable<App> getAppObservable(Ad ad) {
    AdController.clickCpc(ad);
    return getAppObservable(ad.getId());
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
}
