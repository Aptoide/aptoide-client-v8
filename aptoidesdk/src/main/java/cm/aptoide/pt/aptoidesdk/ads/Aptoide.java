package cm.aptoide.pt.aptoidesdk.ads;

import android.content.Context;
import cm.aptoide.pt.aptoidesdk.entities.App;
import cm.aptoide.pt.aptoidesdk.entities.SearchResult;
import java.util.List;

/**
 * Created by neuro on 21-10-2016.
 */

public class Aptoide {

  private Aptoide() {
  }

  public static App getApp(Ad ad) {
    return RxAptoide.getApp(ad).toBlocking().first();
  }

  public static App getApp(SearchResult searchResult) {
    return RxAptoide.getApp(searchResult.getId()).toBlocking().first();
  }

  public static App getApp(String packageName, String storeName) {
    return RxAptoide.getApp(packageName, storeName).toBlocking().first();
  }

  public static App getApp(long appId) {
    return RxAptoide.getApp(appId).toBlocking().first();
  }

  public static void integrate(Context context, String oemid) {
    RxAptoide.integrate(context, oemid);
  }

  public static List<Ad> getAds(int limit) {
    return RxAptoide.getAds(limit).toBlocking().first();
  }

  public static List<Ad> getAds(int limit, boolean mature) {
    return RxAptoide.getAds(limit, mature).toBlocking().first();
  }

  public static List<Ad> getAds(int limit, List<String> keywords) {
    return RxAptoide.getAds(limit, keywords).toBlocking().first();
  }

  public static List<Ad> getAds(int limit, List<String> keywords, boolean mature) {
    return RxAptoide.getAds(limit, keywords, mature).toBlocking().first();
  }

  public static List<SearchResult> searchApps(String query) {
    return RxAptoide.searchApps(query).toBlocking().first();
  }

  public static List<SearchResult> searchApps(String query, String storeName) {
    return RxAptoide.searchApps(query, storeName).toBlocking().first();
  }
}
