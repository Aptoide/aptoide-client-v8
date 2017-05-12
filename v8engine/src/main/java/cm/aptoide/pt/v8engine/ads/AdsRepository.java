/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.ads;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import java.util.LinkedList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by marcelobenites on 7/27/16.
 */
public class AdsRepository {

  private final IdsRepository idsRepository;
  private final AptoideAccountManager accountManager;
  private final GooglePlayServicesAvailabilityChecker googlePlayServicesAvailabilityChecker;
  private final PartnerIdProvider partnerIdProvider;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;

  public AdsRepository(IdsRepository idsRepository, AptoideAccountManager accountManager,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    this.idsRepository = idsRepository;
    this.accountManager = accountManager;
    this.googlePlayServicesAvailabilityChecker =
        (context) -> DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(context);
    this.partnerIdProvider = () -> DataProvider.getConfiguration()
        .getPartnerId();
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
  }

  public static boolean validAds(List<GetAdsResponse.Ad> ads) {
    return ads != null
        && !ads.isEmpty()
        && ads.get(0) != null
        && ads.get(0)
        .getPartner() != null
        && ads.get(0)
        .getPartner()
        .getData() != null;
  }

  public static boolean validAds(GetAdsResponse getAdsResponse) {
    return getAdsResponse != null && validAds(getAdsResponse.getAds());
  }

  public Observable<MinimalAd> getAdsFromAppView(String packageName, String storeName) {
    return mapToMinimalAd(
        GetAdsRequest.ofAppviewOrganic(packageName, storeName, idsRepository.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
            partnerIdProvider.getPartnerId(), accountManager.isAccountMature(), httpClient,
            converterFactory)
            .observe());
  }

  private Observable<MinimalAd> mapToMinimalAd(
      Observable<GetAdsResponse> getAdsResponseObservable) {
    return getAdsResponseObservable.map((getAdsResponse) -> getAdsResponse.getAds())
        .flatMap(ads -> {
          if (!validAds(ads)) {
            return Observable.error(new IllegalStateException("Invalid ads returned from server"));
          }
          return Observable.just(ads.get(0));
        })
        .map((ad) -> MinimalAd.from(ad));
  }

  public Observable<List<MinimalAd>> getAdsFromHomepageMore(boolean refresh) {
    return mapToMinimalAds(GetAdsRequest.ofHomepageMore(idsRepository.getUniqueIdentifier(),
        googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
        partnerIdProvider.getPartnerId(), accountManager.isAccountMature(), httpClient,
        converterFactory)
        .observe(refresh));
  }

  private Observable<List<MinimalAd>> mapToMinimalAds(
      Observable<GetAdsResponse> getAdsResponseObservable) {
    return getAdsResponseObservable.flatMap(ads -> {
      if (!validAds(ads)) {
        return Observable.error(new IllegalStateException("Invalid ads returned from server"));
      }
      return Observable.just(ads);
    })
        .map((getAdsResponse) -> getAdsResponse.getAds())
        .map(ads -> {
          List<MinimalAd> minimalAds = new LinkedList<>();
          for (GetAdsResponse.Ad ad : ads) {
            minimalAds.add(MinimalAd.from(ad));
          }
          return minimalAds;
        });
  }

  public Observable<List<MinimalAd>> getAdsFromAppviewSuggested(String packageName,
      List<String> keywords) {
    return mapToMinimalAds(
        GetAdsRequest.ofAppviewSuggested(keywords, idsRepository.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()), packageName,
            partnerIdProvider.getPartnerId(), accountManager.isAccountMature(), httpClient,
            converterFactory)
            .observe());
  }

  public Observable<MinimalAd> getAdsFromSearch(String query) {
    return mapToMinimalAd(GetAdsRequest.ofSearch(query, idsRepository.getUniqueIdentifier(),
        googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
        partnerIdProvider.getPartnerId(), accountManager.isAccountMature(), httpClient,
        converterFactory)
        .observe());
  }

  public Observable<MinimalAd> getAdsFromSecondInstall(String packageName) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMapObservable(account -> mapToMinimalAd(
            GetAdsRequest.ofSecondInstall(packageName, idsRepository.getUniqueIdentifier(),
                googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
                partnerIdProvider.getPartnerId(), account.isAdultContentEnabled(), httpClient,
                converterFactory)
                .observe()));
  }

  public Observable<MinimalAd> getAdsFromSecondTry(String packageName) {
    return mapToMinimalAd(
        GetAdsRequest.ofSecondTry(packageName, idsRepository.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
            partnerIdProvider.getPartnerId(), accountManager.isAccountMature(), httpClient,
            converterFactory)
            .observe());
  }
}
