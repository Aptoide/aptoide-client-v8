/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

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

    partnerIdProvider = () -> DataProvider.getConfiguration()
        .getPartnerId();

    adultSwitchStatus = SecurePreferences::isAdultSwitchActive;
  }

  public Observable<MinimalAd> getAdsFromAppView(String packageName, String storeName) {
    return mapToMinimalAd(
        AdsOptimizer.optimizeAppViewAds(AptoideAccountManager.getAccessToken(), true, 1,
            aptoideClientUUID.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
            partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive(), packageName,
            storeName));
  }

  public Observable<MinimalAd> getAdsFromFirstInstall(String packageName, String storeName) {
    return mapToMinimalAd(
        AdsOptimizer.optimizeFirstInstallAds(AptoideAccountManager.getAccessToken(), true, 1,
            aptoideClientUUID.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
            partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive(), packageName,
            storeName));
  }

  private Observable<MinimalAd> mapToMinimalAd(
      Observable<GetAdsResponse> getAdsResponseObservable) {
    return getAdsResponseObservable
        .observeOn(Schedulers.io())
        .onErrorResumeNext(throwable -> Observable.empty())
        .map(GetAdsResponse::getDataList)
        .flatMap(ads -> {
          if (!validAds(ads.getList())) {
            return Observable.error(new IllegalStateException("Invalid ads returned from server"));
          }
          return Observable.just(ads.getList()
              .get(0));
        })
        .map(MinimalAd::from);
  }

  public static boolean validAds(List<GetAdsResponse.Ad> ads) {
    return ads != null
        && !ads.isEmpty()
        && ads.get(0) != null
        && ads.get(0)
        .getNetwork() != null;
  }

  public Observable<List<MinimalAd>> getAdsFromHomepageMore() {
    return mapToMinimalAds(GetAdsRequest.ofHomepageMore(AptoideAccountManager.getAccessToken(),
        aptoideClientUUID.getUniqueIdentifier(),
        googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
        partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive())
        .observe());
  }

  private Observable<List<MinimalAd>> mapToMinimalAds(
      Observable<GetAdsResponse> getAdsResponseObservable) {
    return getAdsResponseObservable
        .observeOn(Schedulers.io())
        .onErrorResumeNext(throwable -> Observable.empty())
        .flatMap(ads -> {
      if (!validAds(ads)) {
        return Observable.error(new IllegalStateException("Invalid ads returned from server"));
      }
      return Observable.just(ads);
    })
        .map(GetAdsResponse::getDataList)
        .map(ads -> {
          List<MinimalAd> minimalAds = new LinkedList<>();
          for (GetAdsResponse.Ad ad : ads.getList()) {
            minimalAds.add(MinimalAd.from(ad));
          }
          return minimalAds;
        });
  }

  public static boolean validAds(GetAdsResponse getAdsResponse) {
    return getAdsResponse != null && validAds(getAdsResponse.getDataList()
        .getList());
  }

  public Observable<List<MinimalAd>> getAdsFromAppviewSuggested(String packageName,
      List<String> keywords) {
    return mapToMinimalAds(
        AdsOptimizer.optimizeSuggestedAppViewAds(AptoideAccountManager.getAccessToken(), true, 1,
            aptoideClientUUID.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
            partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive(), packageName,
            keywords));
  }

  public Observable<MinimalAd> getAdsFromSearch(String query) {
    return mapToMinimalAd(
        AdsOptimizer.optimizeSearchAds(AptoideAccountManager.getAccessToken(), true, 1,
            aptoideClientUUID.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
            partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive(),
            new ArrayList<>(Arrays.asList(query.split(",")))));
  }

  public Observable<MinimalAd> getAdsFromSecondInstall(String packageName) {
    return mapToMinimalAd(
        AdsOptimizer.optimizeSecondInstallAds(AptoideAccountManager.getAccessToken(), true, 1,
            aptoideClientUUID.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
            partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive(),
            packageName));
  }

  public Observable<MinimalAd> getAdsFromSecondTry(String packageName) {
    return mapToMinimalAd(
        AdsOptimizer.optimizeSecondTryAds(AptoideAccountManager.getAccessToken(), true, 1,
            aptoideClientUUID.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(V8Engine.getContext()),
            partnerIdProvider.getPartnerId(), adultSwitchStatus.isAdultSwitchActive(),
            packageName));
  }
}
