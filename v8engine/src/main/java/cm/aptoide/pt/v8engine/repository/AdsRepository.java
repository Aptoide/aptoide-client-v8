/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.AdsOptimizer;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.interfaces.AdultSwitchStatus;
import cm.aptoide.pt.v8engine.interfaces.GooglePlayServicesAvailabilityChecker;
import cm.aptoide.pt.v8engine.interfaces.PartnerIdProvider;

import java.util.ArrayList;
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

  public Observable<MinimalAd> getAdsFromAppView(String packageName, String storeName) {
    return mapToMinimalAd(AdsOptimizer.optimizeAds(GetAdsRequest.Location.appview, true, 1,
            aptoideClientUUID.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
            partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive(),
            new ArrayList<>(), "__NULL__", packageName, storeName, null));
    //return mapToMinimalAd(GetAdsRequest.ofAppviewOrganic(packageName, storeName,
    //    aptoideClientUUID.getUniqueIdentifier(),
    //    googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
    //    partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive()).observe());
  }

  public Observable<MinimalAd> getAdsFromFirstInstall(String packageName, String storeName) {
    return mapToMinimalAd(AdsOptimizer.optimizeAds(GetAdsRequest.Location.firstinstall, true, 1,
            aptoideClientUUID.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
            partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive(),
            new ArrayList<>(), "__NULL__", packageName, storeName, null));

    //return mapToMinimalAd(GetAdsRequest.ofFirstInstallOrganic(packageName, storeName,
    //    aptoideClientUUID.getUniqueIdentifier(),
    //    googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
    //    partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive()).observe());
  }

  private Observable<MinimalAd> mapToMinimalAd(
      Observable<GetAdsResponse> getAdsResponseObservable) {
    return getAdsResponseObservable.map(GetAdsResponse::getAds).flatMap(ads -> {
      if (!validAds(ads)) {
        return Observable.error(new IllegalStateException("Invalid ads returned from server"));
      }
      return Observable.just(ads.get(0));
    }).map(MinimalAd::from);
  }

  public static boolean validAds(List<GetAdsResponse.Ad> ads) {
    return ads != null
        && !ads.isEmpty()
        && ads.get(0) != null
        && ads.get(0).getPartner() != null
        && ads.get(0).getPartner().getData() != null;
  }

  public Observable<List<MinimalAd>> getAdsFromHomepageMore() {
    //return mapToMinimalAds(AdsOptimizer.optimizeAds(GetAdsRequest.Location.homepage, true, 50,
    //        aptoideClientUUID.getUniqueIdentifier(),
    //        googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
    //        partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive(),
    //        new ArrayList<>(), "__NULL__", null, null, null));
    return mapToMinimalAds(GetAdsRequest.ofHomepageMore(aptoideClientUUID.getUniqueIdentifier(),
        googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
        partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive()).observe());
  }

  private Observable<List<MinimalAd>> mapToMinimalAds(
      Observable<GetAdsResponse> getAdsResponseObservable) {
    return getAdsResponseObservable.flatMap(ads -> {
      if (!validAds(ads)) {
        return Observable.error(new IllegalStateException("Invalid ads returned from server"));
      }
      return Observable.just(ads);
    }).map(GetAdsResponse::getAds).map(ads -> {
      List<MinimalAd> minimalAds = new LinkedList<>();
      for (GetAdsResponse.Ad ad : ads) {
        minimalAds.add(MinimalAd.from(ad));
      }
      return minimalAds;
    });
  }

  public static boolean validAds(GetAdsResponse getAdsResponse) {
    return getAdsResponse != null && validAds(getAdsResponse.getAds());
  }

  public Observable<List<MinimalAd>> getAdsFromAppviewSuggested(String packageName,
      List<String> keywords) {
    return mapToMinimalAds(AdsOptimizer.optimizeAds(GetAdsRequest.Location.search, true, 1,
            aptoideClientUUID.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
            partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive(),
            new ArrayList<>(), "__NULL__", packageName, null, keywords));
    //return mapToMinimalAds(
    //    GetAdsRequest.ofAppviewSuggested(keywords, aptoideClientUUID.getUniqueIdentifier(),
    //        googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()), packageName,
    //        partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive()).observe());
  }

  public Observable<MinimalAd> getAdsFromSearch(String query) {
    return mapToMinimalAd(AdsOptimizer.optimizeAds(GetAdsRequest.Location.search, true, 1,
            aptoideClientUUID.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
            partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive(),
            new ArrayList<>(), query, null, null, null));
    //return mapToMinimalAd(GetAdsRequest.ofSearch(query, aptoideClientUUID.getUniqueIdentifier(),
    //    googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
    //    partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive()).observe());
  }

  public Observable<MinimalAd> getAdsFromSecondInstall(String packageName) {
    return mapToMinimalAd(AdsOptimizer.optimizeAds(GetAdsRequest.Location.secondinstall, true, 1,
            aptoideClientUUID.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
            partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive(),
            new ArrayList<>(), "__NULL__", packageName, null, null));
    //return mapToMinimalAd(
    //    GetAdsRequest.ofSecondInstall(packageName, aptoideClientUUID.getUniqueIdentifier(),
    //        googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
    //        partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive()).observe());
  }

  public Observable<MinimalAd> getAdsFromSecondTry(String packageName) {
    return mapToMinimalAd(AdsOptimizer.optimizeAds(GetAdsRequest.Location.secondtry, true, 1,
            aptoideClientUUID.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
            partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive(),
            new ArrayList<>(), "__NULL__", packageName, null, null));
    //return mapToMinimalAd(
    //    GetAdsRequest.ofSecondTry(packageName, aptoideClientUUID.getUniqueIdentifier(),
    //        googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
    //        partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive()).observe());
  }
}
