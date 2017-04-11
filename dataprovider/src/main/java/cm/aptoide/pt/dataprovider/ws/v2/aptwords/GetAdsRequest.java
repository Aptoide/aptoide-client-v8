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
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
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
  private final String aptoideClientUUID;
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

  private GetAdsRequest(String aptoideClientUUID, boolean googlePlayServicesAvailable, String oemid,
      boolean mature, Converter.Factory converterFactory, OkHttpClient httpClient) {
    super(httpClient, converterFactory);
    this.aptoideClientUUID = aptoideClientUUID;
    this.googlePlayServicesAvailable = googlePlayServicesAvailable;
    this.oemid = oemid;
    this.mature = mature;
  }

  public static GetAdsRequest ofHomepage(String aptoideClientUUID,
      boolean googlePlayServicesAvailable, String oemid, boolean mature) {
    // TODO: 09-06-2016 neuro limit based on max colums
    return of(Location.homepage, Type.ADS.getPerLineCount(), aptoideClientUUID,
        googlePlayServicesAvailable, oemid, mature);
  }

  private static GetAdsRequest of(Location location, Integer limit, String aptoideClientUUID,
      boolean googlePlayServicesAvailable, String oemid, boolean mature) {
    return of(location, "__NULL__", limit, aptoideClientUUID, googlePlayServicesAvailable, oemid,
        mature);
  }

  public static GetAdsRequest of(Location location, String keyword, Integer limit,
      String aptoideClientUUID, boolean googlePlayServicesAvailable, String oemid, boolean mature) {
    return new GetAdsRequest(aptoideClientUUID, googlePlayServicesAvailable, oemid, mature,
        WebService.getDefaultConverter(),
        OkHttpClientFactory.getSingletonClient(SecurePreferences::getUserAgent, false)).setLocation(
        location).setKeyword(keyword).setLimit(limit);
  }

  public static GetAdsRequest ofHomepageMore(String aptoideClientUUID,
      boolean googlePlayServicesAvailable, String oemid, boolean mature) {
    // TODO: 09-06-2016 neuro limit based on max colums
    return of(Location.homepage, 50, aptoideClientUUID, googlePlayServicesAvailable, oemid, mature);
  }

  public static GetAdsRequest ofAppviewOrganic(String packageName, String storeName,
      String aptoideClientUUID, boolean googlePlayServicesAvailable, String oemid, boolean mature) {

    GetAdsRequest getAdsRequest =
        ofPackageName(Location.appview, packageName, aptoideClientUUID, googlePlayServicesAvailable,
            oemid, mature);

    getAdsRequest.setRepo(storeName);

    return getAdsRequest;
  }

  private static GetAdsRequest ofPackageName(Location location, String packageName,
      String aptoideClientUUID, boolean googlePlayServicesAvailable, String oemid, boolean mature) {
    GetAdsRequest getAdsRequest =
        of(location, 1, aptoideClientUUID, googlePlayServicesAvailable, oemid,
            mature).setPackageName(packageName);

    // Add excluded networks
    if (ReferrerUtils.excludedNetworks.containsKey(packageName)) {
      getAdsRequest.excludedNetworks = AptoideUtils.StringU.commaSeparatedValues(
          ReferrerUtils.excludedNetworks.get(packageName));
    }

    return getAdsRequest;
  }

  public static GetAdsRequest ofAppviewSuggested(List<String> keywords, String aptoideClientUUID,
      boolean googlePlayServicesAvailable, String excludedPackage, String oemid, boolean mature) {

    GetAdsRequest getAdsRequest =
        of(Location.middleappview, 3, aptoideClientUUID, googlePlayServicesAvailable, oemid,
            mature);

    getAdsRequest.setExcludedPackage(excludedPackage)
        .setKeyword(AptoideUtils.StringU.join(keywords, ",") + "," + "__null__");

    return getAdsRequest;
  }

  public static GetAdsRequest ofSearch(String query, String aptoideClientUUID,
      boolean googlePlayServicesAvailable, String oemid, boolean mature) {
    return of(Location.search, query, 1, aptoideClientUUID, googlePlayServicesAvailable, oemid,
        mature);
  }

  public static GetAdsRequest ofSecondInstall(String packageName, String aptoideClientUUID,
      boolean googlePlayServicesAvailable, String oemid, boolean mature) {
    return ofPackageName(Location.secondinstall, packageName, aptoideClientUUID,
        googlePlayServicesAvailable, oemid, mature);
  }

  public static GetAdsRequest ofSecondTry(String packageName, String aptoideClientUUID,
      boolean googlePlayServicesAvailable, String oemid, boolean mature) {
    return ofPackageName(Location.secondtry, packageName, aptoideClientUUID,
        googlePlayServicesAvailable, oemid, mature);
  }

  @Partners public static GetAdsRequest ofFirstInstall(String aptoideClientUUID,
      boolean googlePlayServicesAvailable, String oemid, int numberOfAds, boolean mature) {
    return of(Location.firstinstall, numberOfAds, aptoideClientUUID, googlePlayServicesAvailable,
        oemid, mature);
  }

  @Override protected Observable<GetAdsResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {

    HashMapNotNull<String, String> parameters = new HashMapNotNull<>();

    parameters.put("q", Api.Q);
    parameters.put("lang", Api.LANG);
    parameters.put("cpuid", aptoideClientUUID);
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

    Observable<GetAdsResponse> result =
        interfaces.getAds(parameters, bypassCache).doOnNext(getAdsResponse -> {

          // Impression click for those networks who need it
          for (GetAdsResponse.Ad ad : getAdsResponse.getAds()) {
            DataproviderUtils.AdNetworksUtils.knockImpression(ad);
          }
        });

    // TODO: 28-07-2016 Baikova getAds called.

    return result;
  }

  private String getExcludedPackages() {
    // TODO: 09-06-2016 neuro excluded, not implemented until v8 getAds

    //		@Cleanup Realm realm = Database.get();
    //		RealmResults<ExcludedAd> excludedAdsRealm = Database.ExcludedAdsQ.getAll(realm);
    //
    //		final ArrayList<String> excludedAds = new ArrayList<>();
    //		for (ExcludedAd excludedAd : excludedAdsRealm) {
    //			excludedAds.add(excludedAd.getPackageName());
    //		}

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
