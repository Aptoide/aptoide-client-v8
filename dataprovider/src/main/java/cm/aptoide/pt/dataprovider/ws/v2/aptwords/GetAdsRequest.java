/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v2.aptwords;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.util.referrer.ReferrerUtils;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.networkclient.okhttp.UserAgentInterceptor;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import rx.Observable;

/**
 * Created by neuro on 08-06-2016.
 */
@Data @Accessors(chain = true) @EqualsAndHashCode(callSuper = true) public class GetAdsRequest
    extends Aptwords<GetAdsResponse> {

  private static IdsRepository idsRepository =
      new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());

  private static OkHttpClient client = new OkHttpClient.Builder().readTimeout(2, TimeUnit.SECONDS)
      .addInterceptor(new UserAgentInterceptor(
          AptoideUtils.NetworkUtils.getDefaultUserAgent(idsRepository,
              AptoideAccountManager.getUserEmail())))
      .connectTimeout(2, TimeUnit.SECONDS)
      .build();

  private Location location;
  private String keyword;
  private Integer limit;
  private String packageName;
  private String repo;
  private String categories;
  private String excludedNetworks;

  private GetAdsRequest() {
    super(client);
  }

  private static GetAdsRequest of(Location location, String keyword, Integer limit) {
    return new GetAdsRequest().setLocation(location).setKeyword(keyword).setLimit(limit);
  }

  private static GetAdsRequest of(Location location, Integer limit) {
    return of(location, "__NULL__", limit);
  }

  private static GetAdsRequest ofPackageName(Location location, String packageName) {
    GetAdsRequest getAdsRequest = of(location, 1).setPackageName(packageName);

    // Add excluded networks
    if (ReferrerUtils.excludedNetworks.containsKey(packageName)) {
      getAdsRequest.excludedNetworks = AptoideUtils.StringU.commaSeparatedValues(
          ReferrerUtils.excludedNetworks.get(packageName));
    }

    return getAdsRequest;
  }

  public static GetAdsRequest ofHomepage() {
    // TODO: 09-06-2016 neuro limit based on max colums
    return of(Location.homepage, Type.ADS.getPerLineCount());
  }

  public static GetAdsRequest ofHomepageMore() {
    // TODO: 09-06-2016 neuro limit based on max colums
    return of(Location.homepage, 50);
  }

  public static GetAdsRequest ofAppviewOrganic(String packageName, String storeName) {

    GetAdsRequest getAdsRequest = ofPackageName(Location.appview, packageName);

    getAdsRequest.setRepo(storeName);

    return getAdsRequest;
  }

  public static GetAdsRequest ofAppviewSuggested(List<String> keywords) {

    GetAdsRequest getAdsRequest = of(Location.middleappview, 3);

    getAdsRequest.setKeyword(AptoideUtils.StringU.join(keywords, ",") + "," + "__null__");

    return getAdsRequest;
  }

  public static GetAdsRequest ofSearch(String query) {
    return of(Location.search, query, 1);
  }

  public static GetAdsRequest ofSecondInstall(String packageName) {
    return ofPackageName(Location.secondinstall, packageName);
  }

  public static GetAdsRequest ofSecondTry(String packageName) {
    return ofPackageName(Location.secondtry, packageName);
  }

  @Override protected Observable<GetAdsResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {

    HashMapNotNull<String, String> parameters = new HashMapNotNull<>();

    parameters.put("q", Api.Q);
    parameters.put("lang", Api.LANG);
    parameters.put("cpuid", idsRepository.getAptoideClientUUID());
    parameters.put("aptvercode", Integer.toString(AptoideUtils.Core.getVerCode()));
    parameters.put("location", "native-aptoide:" + location);
    parameters.put("type", "1-3");
    parameters.put("partners", "1-3,5-10");
    parameters.put("keywords", keyword);
    parameters.put("oemid", DataProvider.getConfiguration().getPartnerId());

    if (DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable()) {
      parameters.put("flag", "gms");
    }

    parameters.put("excluded_pkg", getExcludedPackages());

    parameters.put("limit", String.valueOf(limit));
    parameters.put("get_mature", Integer.toString(SecurePreferences.getMatureSwitch()));
    parameters.put("app_pkg", packageName);
    parameters.put("app_store", repo);
    parameters.put("filter_pkg", "true");
    parameters.put("conn_type", AptoideUtils.SystemU.getConnectionType());

    parameters.put("excluded_partners", excludedNetworks);

    Observable<GetAdsResponse> result = interfaces.getAds(parameters).doOnNext(getAdsResponse -> {

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

    return null;
  }

  private enum Location {
    homepage,
    appview,
    middleappview,
    search,
    secondinstall,
    secondtry
  }
}
