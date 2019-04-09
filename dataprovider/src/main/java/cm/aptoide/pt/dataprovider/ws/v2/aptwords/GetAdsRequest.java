/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v2.aptwords;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.util.referrer.ReferrerUtils;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 08-06-2016.
 */
public class GetAdsRequest extends Aptwords<GetAdsResponse> {

  private static String forcedCountry = null;
  private final String clientUniqueId;
  private final boolean googlePlayServicesAvailable;
  private final String oemid;
  private final String q;
  private final SharedPreferences sharedPreferences;
  private final AdsApplicationVersionCodeProvider versionCodeProvider;
  private String excludedPackage;
  private Location location;
  private String keyword;
  private Integer limit;
  private String packageName;
  private String repo;
  private String categories;
  private String excludedNetworks;
  private boolean mature;
  private ConnectivityManager connectivityManager;
  private Resources resources;
  private String groupPackageName;

  private GetAdsRequest(String clientUniqueId, boolean googlePlayServicesAvailable, String oemid,
      boolean mature, Converter.Factory converterFactory, OkHttpClient httpClient, String q,
      SharedPreferences sharedPreferences, ConnectivityManager connectivityManager,
      Resources resources, AdsApplicationVersionCodeProvider versionCodeProvider) {
    super(httpClient, converterFactory, sharedPreferences);
    this.clientUniqueId = clientUniqueId;
    this.googlePlayServicesAvailable = googlePlayServicesAvailable;
    this.oemid = oemid;
    this.mature = mature;
    this.q = q;
    this.sharedPreferences = sharedPreferences;
    this.connectivityManager = connectivityManager;
    this.resources = resources;
    this.versionCodeProvider = versionCodeProvider;
  }

  public static String getForcedCountry() {
    return forcedCountry;
  }

  public static void setForcedCountry(String forcedCountry) {
    GetAdsRequest.forcedCountry = forcedCountry;
  }

  public static GetAdsRequest ofHomepage(String clientUniqueId, boolean googlePlayServicesAvailable,
      String oemid, boolean mature, OkHttpClient httpClient, Converter.Factory converterFactory,
      String q, SharedPreferences sharedPreferences, Resources resources,
      ConnectivityManager connectivityManager,
      AdsApplicationVersionCodeProvider versionCodeProvider, int limit) {
    // TODO: 09-06-2016 neuro limit based on max colums
    return of(Location.homepage, limit, clientUniqueId, googlePlayServicesAvailable, oemid, mature,
        httpClient, converterFactory, q, sharedPreferences, connectivityManager, resources,
        versionCodeProvider);
  }

  private static GetAdsRequest of(Location location, Integer limit, String clientUniqueId,
      boolean googlePlayServicesAvailable, String oemid, boolean mature, OkHttpClient httpClient,
      Converter.Factory converterFactory, String q, SharedPreferences sharedPreferences,
      ConnectivityManager connectivityManager, Resources resources,
      AdsApplicationVersionCodeProvider versionCodeProvider) {
    return of(location, "__NULL__", limit, clientUniqueId, googlePlayServicesAvailable, oemid,
        mature, httpClient, converterFactory, q, sharedPreferences, connectivityManager, resources,
        versionCodeProvider);
  }

  public static GetAdsRequest of(Location location, String keyword, Integer limit,
      String clientUniqueId, boolean googlePlayServicesAvailable, String oemid, boolean mature,
      OkHttpClient httpClient, Converter.Factory converterFactory, String q,
      SharedPreferences sharedPreferences, ConnectivityManager connectivityManager,
      Resources resources, AdsApplicationVersionCodeProvider versionCodeProvider) {
    GetAdsRequest adsRequest =
        new GetAdsRequest(clientUniqueId, googlePlayServicesAvailable, oemid, mature,
            converterFactory, httpClient, q, sharedPreferences, connectivityManager, resources,
            versionCodeProvider);
    adsRequest.setLocation(location);
    adsRequest.setKeyword(keyword);
    adsRequest.setLimit(limit);
    return adsRequest;
  }

  public static GetAdsRequest ofHomepageMore(String clientUniqueId,
      boolean googlePlayServicesAvailable, String oemid, boolean mature, OkHttpClient httpClient,
      Converter.Factory converterFactory, String q, SharedPreferences sharedPreferences,
      ConnectivityManager connectivityManager, Resources resources,
      AdsApplicationVersionCodeProvider versionCodeProvider) {
    // TODO: 09-06-2016 neuro limit based on max colums
    return of(Location.homepage, 50, clientUniqueId, googlePlayServicesAvailable, oemid, mature,
        httpClient, converterFactory, q, sharedPreferences, connectivityManager, resources,
        versionCodeProvider);
  }

  public static GetAdsRequest ofAppviewOrganic(String packageName, String storeName,
      String clientUniqueId, boolean googlePlayServicesAvailable, String oemid, boolean mature,
      OkHttpClient httpClient, Converter.Factory converterFactory, String q,
      SharedPreferences sharedPreferences, ConnectivityManager connectivityManager,
      Resources resources, AdsApplicationVersionCodeProvider versionCodeProvider) {

    GetAdsRequest getAdsRequest =
        ofPackageName(Location.appview, packageName, clientUniqueId, googlePlayServicesAvailable,
            oemid, mature, httpClient, converterFactory, q, sharedPreferences, connectivityManager,
            resources, versionCodeProvider);

    getAdsRequest.setRepo(storeName);
    getAdsRequest.setGroupPackageName(packageName);

    return getAdsRequest;
  }

  private static GetAdsRequest ofPackageName(Location location, String packageName,
      String clientUniqueId, boolean googlePlayServicesAvailable, String oemid, boolean mature,
      OkHttpClient httpClient, Converter.Factory converterFactory, String q,
      SharedPreferences sharedPreferences, ConnectivityManager connectivityManager,
      Resources resources, AdsApplicationVersionCodeProvider versionCodeProvider) {
    GetAdsRequest getAdsRequest =
        of(location, 1, clientUniqueId, googlePlayServicesAvailable, oemid, mature, httpClient,
            converterFactory, q, sharedPreferences, connectivityManager, resources,
            versionCodeProvider);
    getAdsRequest.setPackageName(packageName);

    // Add excluded networks
    if (ReferrerUtils.excludedNetworks.containsKey(packageName)) {
      getAdsRequest.excludedNetworks = AptoideUtils.StringU.commaSeparatedValues(
          ReferrerUtils.excludedNetworks.get(packageName));
    }

    return getAdsRequest;
  }

  public static GetAdsRequest ofAppviewSuggested(List<String> keywords, String clientUniqueId,
      boolean googlePlayServicesAvailable, String excludedPackage, String oemid, boolean mature,
      OkHttpClient httpClient, Converter.Factory converterFactory, String q,
      SharedPreferences sharedPreferences, ConnectivityManager connectivityManager,
      Resources resources, AdsApplicationVersionCodeProvider versionCodeProvider) {

    GetAdsRequest getAdsRequest =
        of(Location.middleappview, 1, clientUniqueId, googlePlayServicesAvailable, oemid, mature,
            httpClient, converterFactory, q, sharedPreferences, connectivityManager, resources,
            versionCodeProvider);

    getAdsRequest.setExcludedPackage(excludedPackage);
    getAdsRequest.setKeyword(AptoideUtils.StringU.join(keywords, ",") + "," + "__null__");
    getAdsRequest.setGroupPackageName(excludedPackage);

    return getAdsRequest;
  }

  public static GetAdsRequest ofSearch(String query, String clientUniqueId,
      boolean googlePlayServicesAvailable, String oemid, boolean mature, OkHttpClient httpClient,
      Converter.Factory converterFactory, String q, SharedPreferences sharedPreferences,
      ConnectivityManager connectivityManager, Resources resources,
      AdsApplicationVersionCodeProvider versionCodeProvider) {
    return of(Location.search, query, 1, clientUniqueId, googlePlayServicesAvailable, oemid, mature,
        httpClient, converterFactory, q, sharedPreferences, connectivityManager, resources,
        versionCodeProvider);
  }

  public static GetAdsRequest ofSecondInstall(String packageName, String clientUniqueId,
      boolean googlePlayServicesAvailable, String oemid, boolean mature, OkHttpClient httpClient,
      Converter.Factory converterFactory, String q, SharedPreferences sharedPreferences,
      ConnectivityManager connectivityManager, Resources resources,
      AdsApplicationVersionCodeProvider versionCodeProvider) {
    return ofPackageName(Location.secondinstall, packageName, clientUniqueId,
        googlePlayServicesAvailable, oemid, mature, httpClient, converterFactory, q,
        sharedPreferences, connectivityManager, resources, versionCodeProvider);
  }

  public static GetAdsRequest ofSecondTry(String packageName, String clientUniqueId,
      boolean googlePlayServicesAvailable, String oemid, boolean mature, OkHttpClient httpClient,
      Converter.Factory converterFactory, String q, SharedPreferences sharedPreferences,
      ConnectivityManager connectivityManager, Resources resources,
      AdsApplicationVersionCodeProvider versionCodeProvider) {
    return ofPackageName(Location.secondtry, packageName, clientUniqueId,
        googlePlayServicesAvailable, oemid, mature, httpClient, converterFactory, q,
        sharedPreferences, connectivityManager, resources, versionCodeProvider);
  }

  public static GetAdsRequest ofFirstInstall(String clientUniqueId,
      boolean googlePlayServicesAvailable, String oemid, int numberOfAds, boolean mature,
      OkHttpClient httpClient, Converter.Factory converterFactory, String q,
      SharedPreferences sharedPreferences, ConnectivityManager connectivityManager,
      Resources resources, AdsApplicationVersionCodeProvider versionCodeProvider) {
    return of(Location.firstinstall, numberOfAds, clientUniqueId, googlePlayServicesAvailable,
        oemid, mature, httpClient, converterFactory, q, sharedPreferences, connectivityManager,
        resources, versionCodeProvider);
  }

  public String getClientUniqueId() {
    return clientUniqueId;
  }

  public boolean isGooglePlayServicesAvailable() {
    return googlePlayServicesAvailable;
  }

  public String getOemid() {
    return oemid;
  }

  public String getQ() {
    return q;
  }

  public SharedPreferences getSharedPreferences() {
    return sharedPreferences;
  }

  public String getExcludedPackage() {
    return excludedPackage;
  }

  public void setExcludedPackage(String excludedPackage) {
    this.excludedPackage = excludedPackage;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getRepo() {
    return repo;
  }

  public void setRepo(String repo) {
    this.repo = repo;
  }

  public String getCategories() {
    return categories;
  }

  public void setCategories(String categories) {
    this.categories = categories;
  }

  public String getExcludedNetworks() {
    return excludedNetworks;
  }

  public void setExcludedNetworks(String excludedNetworks) {
    this.excludedNetworks = excludedNetworks;
  }

  public boolean isMature() {
    return mature;
  }

  public void setMature(boolean mature) {
    this.mature = mature;
  }

  public ConnectivityManager getConnectivityManager() {
    return connectivityManager;
  }

  public void setConnectivityManager(ConnectivityManager connectivityManager) {
    this.connectivityManager = connectivityManager;
  }

  public Resources getResources() {
    return resources;
  }

  public void setResources(Resources resources) {
    this.resources = resources;
  }

  private String getGroupPackageName() {
    return groupPackageName;
  }

  private void setGroupPackageName(String groupPackageName) {
    this.groupPackageName = groupPackageName;
  }

  public AdsApplicationVersionCodeProvider getVersionCodeProvider() {
    return versionCodeProvider;
  }

  @Override protected Observable<GetAdsResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {

    HashMapNotNull<String, String> parameters = new HashMapNotNull<>();

    parameters.put("q", q);
    parameters.put("lang", AptoideUtils.SystemU.getCountryCode(resources));
    parameters.put("cpuid", clientUniqueId);
    parameters.put("location", location.toString());
    parameters.put("type", "1-3");
    parameters.put("partners", "1-3,5-10");
    parameters.put("keywords", keyword);
    parameters.put("oem_id", oemid);
    parameters.put("country", forcedCountry);
    parameters.put("group_package_name", groupPackageName);

    String forceCountry = ToolboxManager.getForceCountry(sharedPreferences);
    if (!TextUtils.isEmpty(forceCountry)) {
      parameters.put("country", forceCountry);
    }
    if (googlePlayServicesAvailable) {
      parameters.put("flag", "gms");
    }

    parameters.put("excluded_pkg", getExcludedPackages());

    parameters.put("limit", String.valueOf(limit));
    parameters.put("get_mature", Integer.toString(mature ? 1 : 0));
    parameters.put("app_pkg", packageName);
    parameters.put("app_store", repo);
    parameters.put("filter_pkg", "true");
    parameters.put("conn_type", AptoideUtils.SystemU.getConnectionType(connectivityManager));

    parameters.put("excluded_partners", excludedNetworks);

    Observable<GetAdsResponse> result = versionCodeProvider.getApplicationVersionCode()
        .flatMapObservable(versionCode -> {

          parameters.put("aptvercode", String.valueOf(versionCode));

          return interfaces.getAds(parameters, bypassCache)
              .doOnNext(getAdsResponse -> {

                // Impression click for those networks who need it
                for (GetAdsResponse.Ad ad : getAdsResponse.getAds()) {
                  AdNetworkUtils.knockImpression(ad);
                }
              });
        });

    // TODO: 28-07-2016 Baikova getAds called.

    return result;
  }

  private String getExcludedPackages() {
    return excludedPackage;
  }
}
