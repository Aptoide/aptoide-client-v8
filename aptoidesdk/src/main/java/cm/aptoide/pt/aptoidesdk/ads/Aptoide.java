package cm.aptoide.pt.aptoidesdk.ads;

import android.content.Context;
import cm.aptoide.pt.aptoidesdk.Ad;
import cm.aptoide.pt.aptoidesdk.entities.App;
import cm.aptoide.pt.aptoidesdk.entities.SearchResult;
import java.util.List;

/**
 * Created by neuro on 21-10-2016.
 */
public class Aptoide {

  private Aptoide() {
  }

  /**
   * Get an app's info based on a provided ad.
   *
   * @param ad ad.
   * @return App info.
   */
  public static App getApp(Ad ad) {
    return RxAptoide.getApp((AptoideAd) ad).toBlocking().first();
  }

  /**
   * Get an app's info based on a provided searchResult.
   *
   * @param searchResult searchResult.
   * @return App info.
   */
  public static App getApp(SearchResult searchResult) {
    return RxAptoide.getApp(searchResult.getId()).toBlocking().first();
  }

  /**
   * Get an app's info based on a packageName and a storeName.
   *
   * @param packageName packageName.
   * @param storeName storeName.
   * @return App info.
   */
  public static App getApp(String packageName, String storeName) {
    return RxAptoide.getApp(packageName, storeName).toBlocking().first();
  }

  /**
   * Get an app's info based on a appId.
   *
   * @param appId appId.
   * @return App info.
   */
  public static App getApp(long appId) {
    return RxAptoide.getApp(appId).toBlocking().first();
  }

  /**
   * Integrate Aptoide Sdk. Should be called before any Sdk invocation.
   *
   * @param context Application Context.
   * @param oemid oem id which identifies the partner.
   */
  public static void integrate(Context context, String oemid) {
    RxAptoide.integrate(context, oemid);
  }

  /**
   * Request ads.
   *
   * @param limit maximum number of ads.
   * @return a list of ads.
   */
  public static List<Ad> getAds(int limit) {
    return RxAptoide.getAds(limit).toBlocking().first();
  }

  /**
   * Request ads.
   *
   * @param limit maximum number of ads.
   * @param mature true to get adult content, false otherwise.
   * @return a list of ads.
   */
  public static List<Ad> getAds(int limit, boolean mature) {
    return RxAptoide.getAds(limit, mature).toBlocking().first();
  }

  /**
   * Request ads.
   *
   * @param limit maximum number of ads.
   * @param keywords keywords based on which the ads should be filtered.
   * @return a list of ads.
   */
  public static List<Ad> getAds(int limit, List<String> keywords) {
    return RxAptoide.getAds(limit, keywords).toBlocking().first();
  }

  /**
   * Request ads.
   *
   * @param limit maximum number of ads.
   * @param keywords keywords based on which the ads should be filtered.
   * @param mature true to get adult content, false otherwise.
   * @return a list of ads.
   */
  public static List<Ad> getAds(int limit, List<String> keywords, boolean mature) {
    return RxAptoide.getAds(limit, keywords, mature).toBlocking().first();
  }

  /**
   * Search for apps.
   *
   * @param query search query.
   * @return a list of search results.
   */
  public static List<SearchResult> searchApps(String query) {
    return RxAptoide.searchApps(query).toBlocking().first();
  }

  /**
   * Search for apps.
   *
   * @param query search query.
   * @param storeName store name where the search should occur.
   * @return a list of search results.
   */
  public static List<SearchResult> searchApps(String query, String storeName) {
    return RxAptoide.searchApps(query, storeName).toBlocking().first();
  }
}
