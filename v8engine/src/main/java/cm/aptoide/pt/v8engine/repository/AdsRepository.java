/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.actions.AptoideClientUUID;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.interfaces.AdultSwitchStatus;
import cm.aptoide.pt.v8engine.interfaces.GooglePlayServicesAvailabilityChecker;
import cm.aptoide.pt.v8engine.interfaces.PartnerIdProvider;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 7/27/16.
 */
public class AdsRepository {

  private AptoideClientUUID aptoideClientUUID;
  private GooglePlayServicesAvailabilityChecker googlePlayServicesAvailabilityChecker;
  private PartnerIdProvider partnerIdProvider;
  private AdultSwitchStatus adultSwitchStatus;

  public AdsRepository() {
    aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext());

    googlePlayServicesAvailabilityChecker =
        DataproviderUtils.AdNetworksUtils::isGooglePlayServicesAvailable;

    partnerIdProvider = () -> DataProvider.getConfiguration().getPartnerId();

    adultSwitchStatus = SecurePreferences::isAdultSwitchActive;
  }

  public static boolean validAds(List<GetAdsResponse.Ad> ads) {
    return ads != null
        && !ads.isEmpty()
        && ads.get(0) != null
        && ads.get(0).getPartner() != null
        && ads.get(0).getPartner().getData() != null;
  }

  public static boolean validAds(GetAdsResponse getAdsResponse) {
    return getAdsResponse != null && validAds(getAdsResponse.getAds());
  }

  private Observable<List<MinimalAd>> mapToMinimalAds(
      Observable<GetAdsResponse> getAdsResponseObservable) {
    return getAdsResponseObservable.filter(AdsRepository::validAds)
        .map(GetAdsResponse::getAds)
        .map(ads -> {
          List<MinimalAd> minimalAds = new LinkedList<>();
          for (GetAdsResponse.Ad ad : ads) {
            minimalAds.add(MinimalAd.from(ad));
          }
          return minimalAds;
        });
  }

  public Observable<MinimalAd> getAdsFromAppView(String packageName, String storeName) {
    return GetAdsRequest.ofAppviewOrganic(packageName, storeName,
        aptoideClientUUID.getAptoideClientUUID(),
        googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
        partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive())
        .observe()
        .map(response -> response.getAds())
        .flatMap(ads -> {
          if (!validAds(ads)) {
            return Observable.error(new IllegalStateException("Invalid ads returned from server"));
          }
          return Observable.just(ads.get(0));
        })
        .map(ad -> MinimalAd.from(ad));
  }

  public Observable<List<MinimalAd>> getAdsFromHomepageMore() {
    return mapToMinimalAds(GetAdsRequest.ofHomepageMore(aptoideClientUUID.getAptoideClientUUID(),
        googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
        partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive()).observe());
  }
}