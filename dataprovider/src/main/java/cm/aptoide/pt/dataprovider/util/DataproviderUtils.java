/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.dataprovider.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.model.MinimalAd;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.CrashReports;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import java.io.IOException;
import java.util.Date;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by neuro on 20-04-2016.
 */
public class DataproviderUtils {

  private static final String TAG = DataproviderUtils.class.getName();

  public static void checkUpdates() {
    checkUpdates(null);
  }

  public static void checkUpdates(
      @Nullable SuccessRequestListener<ListAppsUpdates> successRequestListener) {

    StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);

    storeAccessor.getAll().subscribe(listUpdtes -> {
      if (listUpdtes == null || listUpdtes.size() == 0) {
        return;
      }
    }, err -> {
      CrashReports.logException(err);
      Logger.e(TAG, err);
    });

    // v1
    //ListAppsUpdatesRequest.of().execute(listAppsUpdates -> {
    //  @Cleanup Realm realm = DeprecatedDatabase.get();
    //  for (App app : listAppsUpdates.getList()) {
    //    Update update = DeprecatedDatabase.UpdatesQ.get(app.getPackageName(), realm);
    //    if (update == null || !update.isExcluded()) {
    //      DeprecatedDatabase.save(new Update(app), realm);
    //    }
    //  }
    //
    //  if (successRequestListener != null) {
    //    successRequestListener.call(listAppsUpdates);
    //  }
    //}, Throwable::printStackTrace, true);

    // v2
    ListAppsUpdatesRequest.of().observe().subscribe(listAppsUpdates -> {

      UpdateAccessor updateAccessor = AccessorFactory.getAccessorFor(Update.class);

      for (App app : listAppsUpdates.getList()) {
        updateAccessor.get(app.getPackageName()).subscribe(currentAppUpdate -> {
          if (currentAppUpdate == null || !currentAppUpdate.isExcluded()) {
            updateAccessor.insert(new Update(app));
          }
        }, err -> {
          Logger.e(TAG, err);
          CrashReports.logException(err);
        });
      }
    });

    // TODO: 07/10/16 sithengineer version 3 of the previous code.
  }

  /**
   * Execute a simple request (knock at the door) to the given URL.
   */
  public static void knock(String url) {
    if (url == null) {
      return;
    }

    OkHttpClient client = new OkHttpClient();

    Request click = new Request.Builder().url(url).build();

    client.newCall(click).enqueue(new Callback() {
      @Override public void onFailure(Call call, IOException e) {

      }

      @Override public void onResponse(Call call, Response response) throws IOException {
        response.body().close();
      }
    });
  }

  public static class AdNetworksUtils {

    public static String parseMacros(@NonNull String clickUrl) {

      IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(),
          DataProvider.getContext());

      if (idsRepository.getAndroidId() != null) {
        clickUrl = clickUrl.replace("[USER_ANDROID_ID]", idsRepository.getAndroidId());
      }
      clickUrl = clickUrl.replace("[USER_UDID]", idsRepository.getAptoideClientUUID());
      clickUrl = clickUrl.replace("[USER_AAID]", idsRepository.getAdvertisingId());
      clickUrl = clickUrl.replace("[TIME_STAMP]", String.valueOf(new Date().getTime()));

      return clickUrl;
    }

    public static boolean isGooglePlayServicesAvailable() {
      return GoogleApiAvailability.getInstance()
          .isGooglePlayServicesAvailable(DataProvider.getContext()) == ConnectionResult.SUCCESS;
    }

    public static void knockCpc(MinimalAd minimalAd) {
      // TODO: 28-07-2016 Baikova clicked on ad.
      knock(minimalAd.getCpcUrl());
    }

    public static void knockCpd(MinimalAd minimalAd) {
      // TODO: 28-07-2016 Baikova clicked on download button.
      knock(minimalAd.getCpdUrl());
    }

    public static void knockCpi(StoredMinimalAd minimalAd) {
      // TODO: 28-07-2016 Baikova ad installed.
      knock(minimalAd.getCpiUrl());
    }

    // FIXME: 29-07-2016 neuro so wrong...
    public static void knockImpression(GetAdsResponse.Ad ad) {
      if (isImpressionUrlPresent(ad)) {
        knock(ad.getPartner().getData().getImpressionUrl());
      }
    }

    private static boolean isImpressionUrlPresent(GetAdsResponse.Ad ad) {
      return ad != null
          && ad.getPartner() != null
          && ad.getPartner().getData() != null
          && ad.getPartner().getData().getImpressionUrl() != null;
    }
  }
}
