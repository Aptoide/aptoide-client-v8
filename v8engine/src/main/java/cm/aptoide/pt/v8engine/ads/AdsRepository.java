/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.ads;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.AdsApplicationVersionCodeProvider;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import java.util.LinkedList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.schedulers.Schedulers;

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
  private final QManager qManager;
  private final SharedPreferences sharedPreferences;
  private final Context context;
  private final ConnectivityManager connectivityManager;
  private final Resources resources;
  private final AdsApplicationVersionCodeProvider versionCodeProvider;
  private final MinimalAdMapper adMapper;

  public AdsRepository(IdsRepository idsRepository, AptoideAccountManager accountManager,
      OkHttpClient httpClient, Converter.Factory converterFactory, QManager qManager,
      SharedPreferences sharedPreferences, Context applicationContext,
      ConnectivityManager connectivityManager, Resources resources,
      AdsApplicationVersionCodeProvider versionCodeProvider,
      GooglePlayServicesAvailabilityChecker googlePlayServicesAvailabilityChecker,
      PartnerIdProvider partnerIdProvider, MinimalAdMapper adMapper) {
    this.idsRepository = idsRepository;
    this.accountManager = accountManager;
    this.versionCodeProvider = versionCodeProvider;
    this.googlePlayServicesAvailabilityChecker = googlePlayServicesAvailabilityChecker;
    this.partnerIdProvider = partnerIdProvider;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.qManager = qManager;
    this.sharedPreferences = sharedPreferences;
    this.context = applicationContext;
    this.connectivityManager = connectivityManager;
    this.resources = resources;
    this.adMapper = adMapper;
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
            googlePlayServicesAvailabilityChecker.isAvailable(context),
            partnerIdProvider.getPartnerId(), accountManager.isAccountMature(), httpClient,
            converterFactory,
            qManager.getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)),
            sharedPreferences, connectivityManager, resources, versionCodeProvider)
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
        .map((ad) -> adMapper.map(ad));
  }

  public Observable<List<MinimalAd>> getAdsFromHomepageMore(boolean refresh) {
    return mapToMinimalAds(GetAdsRequest.ofHomepageMore(idsRepository.getUniqueIdentifier(),
        googlePlayServicesAvailabilityChecker.isAvailable(context),
        partnerIdProvider.getPartnerId(), accountManager.isAccountMature(), httpClient,
        converterFactory,
        qManager.getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)),
        sharedPreferences, connectivityManager, resources, versionCodeProvider)
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
            minimalAds.add(adMapper.map(ad));
          }
          return minimalAds;
        });
  }

  public Observable<List<MinimalAd>> getAdsFromAppviewSuggested(String packageName,
      List<String> keywords) {
    return mapToMinimalAds(
        GetAdsRequest.ofAppviewSuggested(keywords, idsRepository.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(context), packageName,
            partnerIdProvider.getPartnerId(), accountManager.isAccountMature(), httpClient,
            converterFactory,
            qManager.getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)),
            sharedPreferences, connectivityManager, resources, versionCodeProvider)
            .observe()).subscribeOn(Schedulers.io());
  }

  public Observable<MinimalAd> getAdsFromSearch(String query) {
    return mapToMinimalAd(GetAdsRequest.ofSearch(query, idsRepository.getUniqueIdentifier(),
        googlePlayServicesAvailabilityChecker.isAvailable(context),
        partnerIdProvider.getPartnerId(), accountManager.isAccountMature(), httpClient,
        converterFactory,
        qManager.getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)),
        sharedPreferences, connectivityManager, resources, versionCodeProvider)
        .observe());
  }

  public Observable<MinimalAd> getAdsFromSecondInstall(String packageName) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMapObservable(account -> mapToMinimalAd(
            GetAdsRequest.ofSecondInstall(packageName, idsRepository.getUniqueIdentifier(),
                googlePlayServicesAvailabilityChecker.isAvailable(context),
                partnerIdProvider.getPartnerId(), account.isAdultContentEnabled(), httpClient,
                converterFactory,
                qManager.getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)),
                sharedPreferences, connectivityManager, resources, versionCodeProvider)
                .observe()));
  }

  public Observable<MinimalAd> getAdsFromSecondTry(String packageName) {
    return mapToMinimalAd(
        GetAdsRequest.ofSecondTry(packageName, idsRepository.getUniqueIdentifier(),
            googlePlayServicesAvailabilityChecker.isAvailable(context),
            partnerIdProvider.getPartnerId(), accountManager.isAccountMature(), httpClient,
            converterFactory,
            qManager.getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)),
            sharedPreferences, connectivityManager, resources, versionCodeProvider)
            .observe());
  }
}
