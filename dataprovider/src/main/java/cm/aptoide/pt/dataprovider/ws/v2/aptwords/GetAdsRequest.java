/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v2.aptwords;

import android.text.TextUtils;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.util.referrer.ReferrerUtils;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 08-06-2016.
 */
@Data @Accessors(chain = true) @EqualsAndHashCode(callSuper = true) public class GetAdsRequest
    extends Aptwords<GetAdsResponse> {

  @Getter @Setter private static String forcedCountry = null;
  private final String clientUniqueId;
  private final boolean googlePlayServicesAvailable;
  private final String oemid;
  private String excludedPackage;
  private Location location;
  private String keyword;
  private Integer limit;
  private String packageName;
  private String repo;
  private String categories;
  private String excludedNetworks;
  private boolean mature;

  private GetAdsRequest(String clientUniqueId, boolean googlePlayServicesAvailable, String oemid,
      boolean mature, Converter.Factory converterFactory, OkHttpClient httpClient) {
    super(httpClient, converterFactory);
    this.clientUniqueId = clientUniqueId;
    this.googlePlayServicesAvailable = googlePlayServicesAvailable;
    this.oemid = oemid;
    this.mature = mature;
  }

  public static GetAdsRequest ofHomepage(String clientUniqueId, boolean googlePlayServicesAvailable,
      String oemid, boolean mature, OkHttpClient httpClient, Converter.Factory converterFactory) {
    // TODO: 09-06-2016 neuro limit based on max colums
    return of(Location.homepage, Type.ADS.getPerLineCount(), clientUniqueId,
        googlePlayServicesAvailable, oemid, mature, httpClient, converterFactory);
  }

  private static GetAdsRequest of(Location location, Integer limit, String clientUniqueId,
      boolean googlePlayServicesAvailable, String oemid, boolean mature, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    return of(location, "__NULL__", limit, clientUniqueId, googlePlayServicesAvailable, oemid,
        mature, httpClient, converterFactory);
  }

  public static GetAdsRequest of(Location location, String keyword, Integer limit,
      String clientUniqueId, boolean googlePlayServicesAvailable, String oemid, boolean mature,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    return new GetAdsRequest(clientUniqueId, googlePlayServicesAvailable, oemid, mature,
        converterFactory, httpClient).setLocation(location)
        .setKeyword(keyword)
        .setLimit(limit);
  }

  public static GetAdsRequest ofHomepageMore(String clientUniqueId,
      boolean googlePlayServicesAvailable, String oemid, boolean mature, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    // TODO: 09-06-2016 neuro limit based on max colums
    return of(Location.homepage, 50, clientUniqueId, googlePlayServicesAvailable, oemid, mature,
        httpClient, converterFactory);
  }

  public static GetAdsRequest ofAppviewOrganic(String packageName, String storeName,
      String clientUniqueId, boolean googlePlayServicesAvailable, String oemid, boolean mature,
      OkHttpClient httpClient, Converter.Factory converterFactory) {

    GetAdsRequest getAdsRequest =
        ofPackageName(Location.appview, packageName, clientUniqueId, googlePlayServicesAvailable,
            oemid, mature, httpClient, converterFactory);

    getAdsRequest.setRepo(storeName);

    return getAdsRequest;
  }

  private static GetAdsRequest ofPackageName(Location location, String packageName,
      String clientUniqueId, boolean googlePlayServicesAvailable, String oemid, boolean mature,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    GetAdsRequest getAdsRequest =
        of(location, 1, clientUniqueId, googlePlayServicesAvailable, oemid, mature, httpClient,
            converterFactory).setPackageName(packageName);

    // Add excluded networks
    if (ReferrerUtils.excludedNetworks.containsKey(packageName)) {
      getAdsRequest.excludedNetworks = AptoideUtils.StringU.commaSeparatedValues(
          ReferrerUtils.excludedNetworks.get(packageName));
    }

    return getAdsRequest;
  }

  public static GetAdsRequest ofAppviewSuggested(List<String> keywords, String clientUniqueId,
      boolean googlePlayServicesAvailable, String excludedPackage, String oemid, boolean mature,
      OkHttpClient httpClient, Converter.Factory converterFactory) {

    GetAdsRequest getAdsRequest =
        of(Location.middleappview, 3, clientUniqueId, googlePlayServicesAvailable, oemid, mature,
            httpClient, converterFactory);

    getAdsRequest.setExcludedPackage(excludedPackage)
        .setKeyword(AptoideUtils.StringU.join(keywords, ",") + "," + "__null__");

    return getAdsRequest;
  }

  public static GetAdsRequest ofSearch(String query, String clientUniqueId,
      boolean googlePlayServicesAvailable, String oemid, boolean mature, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    return of(Location.search, query, 1, clientUniqueId, googlePlayServicesAvailable, oemid, mature,
        httpClient, converterFactory);
  }

  public static GetAdsRequest ofSecondInstall(String packageName, String clientUniqueId,
      boolean googlePlayServicesAvailable, String oemid, boolean mature, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    return ofPackageName(Location.secondinstall, packageName, clientUniqueId,
        googlePlayServicesAvailable, oemid, mature, httpClient, converterFactory);
  }

  public static GetAdsRequest ofSecondTry(String packageName, String clientUniqueId,
      boolean googlePlayServicesAvailable, String oemid, boolean mature, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    return ofPackageName(Location.secondtry, packageName, clientUniqueId,
        googlePlayServicesAvailable, oemid, mature, httpClient, converterFactory);
  }

  @Partners public static GetAdsRequest ofFirstInstall(String clientUniqueId,
      boolean googlePlayServicesAvailable, String oemid, int numberOfAds, boolean mature,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    return of(Location.firstinstall, numberOfAds, clientUniqueId, googlePlayServicesAvailable,
        oemid, mature, httpClient, converterFactory);
  }

  @Override protected Observable<GetAdsResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {

    HashMapNotNull<String, String> parameters = new HashMapNotNull<>();

    parameters.put("q", Api.Q);
    parameters.put("lang", Api.LANG);
    parameters.put("cpuid", clientUniqueId);
    parameters.put("aptvercode", Integer.toString(AptoideUtils.Core.getVerCode()));
    parameters.put("location", location.toString());
    parameters.put("type", "1-3");
    parameters.put("partners", "1-3,5-10");
    parameters.put("keywords", keyword);
    parameters.put("oem_id", oemid);
    parameters.put("country", forcedCountry);

    if (ManagerPreferences.isDebug()) {
      String forceCountry = ManagerPreferences.getForceCountry();
      if (!TextUtils.isEmpty(forceCountry)) {
        parameters.put("country", forceCountry);
      }
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
    parameters.put("conn_type", AptoideUtils.SystemU.getConnectionType());

    parameters.put("excluded_partners", excludedNetworks);

    Observable<GetAdsResponse> result = interfaces.getAds(parameters, bypassCache)
        .doOnNext(getAdsResponse -> {

          // Impression click for those networks who need it
          for (GetAdsResponse.Ad ad : getAdsResponse.getAds()) {
            DataproviderUtils.AdNetworksUtils.knockImpression(ad);
          }
        });

    // TODO: 28-07-2016 Baikova getAds called.

    return result;
  }

  private String getExcludedPackages() {
    return excludedPackage;
  }

  public enum Location {
    homepage("native-aptoide:homepage"), appview("native-aptoide:appview"), middleappview(
        "native-aptoide:middleappview"), search("native-aptoide:search"), secondinstall(
        "native-aptoide:secondinstall"), secondtry("native-aptoide:secondtry"), aptoidesdk(
        "sdk-aptoide:generic"), firstinstall("native-aptoide:first-install");

    private final String value;

    Location(String value) {
      this.value = value;
    }

    @Override public String toString() {
      return value;
    }
  }
}
