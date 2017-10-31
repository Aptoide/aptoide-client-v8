package cm.aptoide.pt.dataprovider;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import rx.Observable;

/**
 * Created by danielchen on 24/07/17.
 */

public class AdsOptimizer {

  private static final String EXCLUDED_PACKAGE_KEY = "excPck";

  private static int count = 0;

  /**
   * This method checks if the ads returned in the GetAdsResponse are valid.
   * To be valid, its required that at least more than half of them are NOT installed already
   *
   * @param ads - list of ads from GetAdsResponse
   * @param excludedPackages - list of excluded packages, that include the ones in the original
   * request and saved in the sharedPreferences of the device
   * @param numberOfAdsToShow - original number of ads asked by the request
   *
   * @return true if it's valid, false if not
   */
  private static boolean checkIfAdsListIsValid(List<GetAdsResponse.Ad> ads,
      List<String> excludedPackages, Integer numberOfAdsToShow) {

    boolean valid = true;
    if (excludedPackages == null) {
      excludedPackages = new ArrayList<>();
    }
    List<GetAdsResponse.Ad> newAdsList = new ArrayList<>();
    for (GetAdsResponse.Ad ad : ads) {
      if (AptoideUtils.SystemU.isAppInstalled(ad.getData()
          .getApp()
          .getPackageName())) {
        excludedPackages.add(ad.getData()
            .getApp()
            .getPackageName());
      } else {
        newAdsList.add(ad);
      }
    }
    if(count == 3){
      count = 0;
      adjustAdsListSize(ads, numberOfAdsToShow);
      return true;
    }
    if (newAdsList.size() < ads.size() / 2) {
      count++;
      valid = false;
    } else {
      //Return only the number of ads that are supposed to be shown
      if (numberOfAdsToShow <= newAdsList.size()) {
        newAdsList.subList(numberOfAdsToShow, newAdsList.size())
            .clear();
        ads.clear();
        ads.addAll(newAdsList);
      }
    }
    addToSharedPreferences(excludedPackages);
    return valid;
  }

  public static Observable<GetAdsResponse> optimizeHomepageAds(String accessToken, boolean refresh,
      Integer adsLimit, String aptoideClientUUID, boolean googlePlayServicesAvailable, String oemid,
      boolean mature) {

    List<String> excludedPck = new ArrayList<>();
    getFromSharedPreferencesInto(excludedPck);

    final Integer requestAds = adsLimit * 2;

    GetAdsRequest request =
        GetAdsRequest.ofHomepage(accessToken, aptoideClientUUID, googlePlayServicesAvailable, oemid,
            mature, requestAds, AptoideUtils.StringU.commaSeparatedValues(excludedPck));

    Observable<GetAdsResponse> response = request.observe(refresh);

    return response.flatMap(ads -> {
      if (!checkIfAdsListIsValid(ads.getDataList()
          .getList(), excludedPck, adsLimit)) {
          return optimizeHomepageAds(accessToken, refresh, adsLimit, aptoideClientUUID,
              googlePlayServicesAvailable, oemid, mature);
      }
      return Observable.just(ads);
    });
  }

  private static void adjustAdsListSize(List<GetAdsResponse.Ad> ads, int limit){
    ads.subList(limit, ads.size()).clear();
  }

  public static Observable<GetAdsResponse> optimizeNotificationAds(String accessToken,
      boolean refresh, Integer adsLimit, String aptoideClientUUID,
      boolean googlePlayServicesAvailable, String oemid, boolean mature, List<String> excludedPck) {

    getFromSharedPreferencesInto(excludedPck);

    final Integer requestAds = adsLimit * 2;

    GetAdsRequest request =
        GetAdsRequest.ofNotification(accessToken, aptoideClientUUID, googlePlayServicesAvailable,
            oemid, requestAds, mature, AptoideUtils.StringU.commaSeparatedValues(excludedPck));

    Observable<GetAdsResponse> response = request.observe(refresh);

    return response.flatMap(ads -> {
      if (!checkIfAdsListIsValid(ads.getDataList()
          .getList(), excludedPck, adsLimit)) {
        return optimizeNotificationAds(accessToken, refresh, adsLimit, aptoideClientUUID,
            googlePlayServicesAvailable, oemid, mature, excludedPck);
      }
      return Observable.just(ads);
    });
  }

  public static Observable<GetAdsResponse> optimizeFirstInstallAds(String accessToken,
      boolean refresh, Integer adsLimit, String aptoideClientUUID,
      boolean googlePlayServicesAvailable, String oemid, boolean mature, List<String> excludedPck) {

    getFromSharedPreferencesInto(excludedPck);

    final Integer requestAds = adsLimit * 2;

    GetAdsRequest request =
        GetAdsRequest.ofFirstInstall(accessToken, aptoideClientUUID, googlePlayServicesAvailable,
            oemid, requestAds, mature, AptoideUtils.StringU.commaSeparatedValues(excludedPck));

    Observable<GetAdsResponse> response = request.observe(refresh);

    return response.flatMap(ads -> {
      if (!checkIfAdsListIsValid(ads.getDataList()
          .getList(), excludedPck, adsLimit)) {
        return optimizeFirstInstallAds(accessToken, refresh, adsLimit, aptoideClientUUID,
            googlePlayServicesAvailable, oemid, mature, excludedPck);
      }
      return Observable.just(ads);
    });
  }

  public static Observable<GetAdsResponse> optimizeAppViewAds(String accessToken, boolean refresh,
      Integer adsLimit, String aptoideClientUUID, boolean googlePlayServicesAvailable, String oemid,
      boolean mature, String packageName, String storeName) {

    List<String> excludedPck = new ArrayList<>();
    getFromSharedPreferencesInto(excludedPck);

    final Integer requestAds = adsLimit * 2;

    GetAdsRequest request =
        GetAdsRequest.ofAppviewOrganic(accessToken, packageName, storeName, aptoideClientUUID,
            googlePlayServicesAvailable, oemid, mature, requestAds);

    Observable<GetAdsResponse> response = request.observe(refresh);

    return response.flatMap(ads -> {
      if (!checkIfAdsListIsValid(ads.getDataList()
          .getList(), excludedPck, adsLimit)) {
        return optimizeAppViewAds(accessToken, refresh, adsLimit, aptoideClientUUID,
            googlePlayServicesAvailable, oemid, mature, packageName, storeName);
      }
      return Observable.just(ads);
    });
  }

  public static Observable<GetAdsResponse> optimizeFirstInstallAds(String accessToken,
      boolean refresh, Integer adsLimit, String aptoideClientUUID,
      boolean googlePlayServicesAvailable, String oemid, boolean mature, String packageName,
      String storeName) {

    List<String> excludedPck = new ArrayList<>();
    getFromSharedPreferencesInto(excludedPck);

    final Integer requestAds = adsLimit * 2;

    GetAdsRequest request =
        GetAdsRequest.ofFirstInstallOrganic(accessToken, packageName, storeName, aptoideClientUUID,
            googlePlayServicesAvailable, oemid, mature, requestAds);

    Observable<GetAdsResponse> response = request.observe(refresh);

    return response.flatMap(ads -> {
      if (!checkIfAdsListIsValid(ads.getDataList()
          .getList(), excludedPck, adsLimit)) {
        return optimizeFirstInstallAds(accessToken, refresh, adsLimit, aptoideClientUUID,
            googlePlayServicesAvailable, oemid, mature, packageName, storeName);
      }
      return Observable.just(ads);
    });
  }

  public static Observable<GetAdsResponse> optimizeSuggestedAppViewAds(String accessToken,
      boolean refresh, Integer adsLimit, String aptoideClientUUID,
      boolean googlePlayServicesAvailable, String oemid, boolean mature, String packageName,
      List<String> keywords) {

    List<String> excludedPck = new ArrayList<>();
    excludedPck.add(packageName);
    getFromSharedPreferencesInto(excludedPck);

    final Integer requestAds = adsLimit * 2;

    GetAdsRequest request =
        GetAdsRequest.ofAppviewSuggested(accessToken, keywords, aptoideClientUUID,
            googlePlayServicesAvailable, packageName, oemid, mature, requestAds);

    Observable<GetAdsResponse> response = request.observe(refresh);

    return response.flatMap(ads -> {
      if (!checkIfAdsListIsValid(ads.getDataList()
          .getList(), excludedPck, adsLimit)) {
        return optimizeSuggestedAppViewAds(accessToken, refresh, adsLimit, aptoideClientUUID,
            googlePlayServicesAvailable, oemid, mature, packageName, keywords);
      }
      return Observable.just(ads);
    });
  }

  public static Observable<GetAdsResponse> optimizeSearchAds(String accessToken, boolean refresh,
      Integer adsLimit, String aptoideClientUUID, boolean googlePlayServicesAvailable, String oemid,
      boolean mature, List<String> query) {

    List<String> excludedPck = new ArrayList<>();
    getFromSharedPreferencesInto(excludedPck);

    final Integer requestAds = adsLimit * 2;

    GetAdsRequest request =
        GetAdsRequest.ofSearch(accessToken, query, aptoideClientUUID, googlePlayServicesAvailable,
            oemid, mature, requestAds);

    Observable<GetAdsResponse> response = request.observe(refresh);

    return response.flatMap(ads -> {
      if (!checkIfAdsListIsValid(ads.getDataList()
          .getList(), excludedPck, adsLimit)) {
        return optimizeSearchAds(accessToken, refresh, adsLimit, aptoideClientUUID,
            googlePlayServicesAvailable, oemid, mature, query);
      }
      return Observable.just(ads);
    });
  }

  public static Observable<GetAdsResponse> optimizeSecondInstallAds(String accessToken,
      boolean refresh, Integer adsLimit, String aptoideClientUUID,
      boolean googlePlayServicesAvailable, String oemid, boolean mature, String packageName) {

    List<String> excludedPck = new ArrayList<>();
    getFromSharedPreferencesInto(excludedPck);

    final Integer requestAds = adsLimit * 2;

    GetAdsRequest request =
        GetAdsRequest.ofSecondInstall(accessToken, packageName, aptoideClientUUID,
            googlePlayServicesAvailable, oemid, mature, requestAds);

    Observable<GetAdsResponse> response = request.observe(refresh);

    return response.flatMap(ads -> {
      if (!checkIfAdsListIsValid(ads.getDataList()
          .getList(), excludedPck, adsLimit)) {
        return optimizeSecondInstallAds(accessToken, refresh, adsLimit, aptoideClientUUID,
            googlePlayServicesAvailable, oemid, mature, packageName);
      }
      return Observable.just(ads);
    });
  }

  public static Observable<GetAdsResponse> optimizeSecondTryAds(String accessToken, boolean refresh,
      Integer adsLimit, String aptoideClientUUID, boolean googlePlayServicesAvailable, String oemid,
      boolean mature, String packageName) {

    List<String> excludedPck = new ArrayList<>();
    getFromSharedPreferencesInto(excludedPck);

    final Integer requestAds = adsLimit * 2;

    GetAdsRequest request = GetAdsRequest.ofSecondTry(accessToken, packageName, aptoideClientUUID,
        googlePlayServicesAvailable, oemid, mature, requestAds);

    Observable<GetAdsResponse> response = request.observe(refresh);

    return response.flatMap(ads -> {
      if (!checkIfAdsListIsValid(ads.getDataList()
          .getList(), excludedPck, adsLimit)) {
        return optimizeSecondTryAds(accessToken, refresh, adsLimit, aptoideClientUUID,
            googlePlayServicesAvailable, oemid, mature, packageName);
      }
      return Observable.just(ads);
    });
  }

  /**
   * This method allows new package's name to be added to the sharedPreferences of the device.
   *
   * @param excludedPackages - list of packages to add
   */
  private static void addToSharedPreferences(List<String> excludedPackages) {
    Set<String> set = new HashSet<>();
    set.addAll(excludedPackages);
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Application.getContext());
    SharedPreferences.Editor editor = sp.edit();
    editor.putStringSet(EXCLUDED_PACKAGE_KEY, set);
    editor.apply();
  }

  /**
   * This method allows the use of the packages saved in the sharedPreferences of the
   * device, in order to increase the number of excludedPackages used if required.
   * If a previously installed ad was uninstalled, it is removed from the
   * sharedPreferences.
   *
   * @param excludedPackages - list in which the saved package's names are to be added.
   */
  private static void getFromSharedPreferencesInto(List<String> excludedPackages) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Application.getContext());
    Set<String> savedExcPck = sp.getStringSet(EXCLUDED_PACKAGE_KEY, null);

    Set<String> set = new HashSet<>();
    set.addAll(excludedPackages);
    if (savedExcPck != null) {
      removeFromSharedPreferenceIfNeeded(savedExcPck);
      set.addAll(savedExcPck);
    }
    excludedPackages.clear();
    excludedPackages.addAll(set);
  }

  /**
   * This method is used to remove previously installed ads, that were uninstalled in the
   * meanwhile.
   *
   * @param savedExcPcks - set of packages to be excluded
   */
  private static void removeFromSharedPreferenceIfNeeded(Set<String> savedExcPcks) {
    for (Iterator<String> iterator = savedExcPcks.iterator(); iterator.hasNext(); ) {
      String saved = iterator.next();
      if (!AptoideUtils.SystemU.isAppInstalled(saved)) {
        iterator.remove();
      }
    }
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Application.getContext());
    SharedPreferences.Editor editor = sp.edit();
    editor.remove(EXCLUDED_PACKAGE_KEY);
    editor.putStringSet(EXCLUDED_PACKAGE_KEY, savedExcPcks);
    editor.apply();
  }
}
