/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.networking;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * Created by neuro on 11-07-2016.
 */
public class IdsRepository {

  private static final String APTOIDE_CLIENT_UUID = "aptoide_client_uuid";
  private static final String ADVERTISING_ID_CLIENT = "advertisingIdClient";
  private static final String ANDROID_ID_CLIENT = "androidId";
  private static final String GOOGLE_ADVERTISING_ID_CLIENT = "googleAdvertisingId";
  private static final String GOOGLE_ADVERTISING_ID_CLIENT_SET = "googleAdvertisingIdSet";

  private final SharedPreferences sharedPreferences;
  private final Context context;
  private final String androidId;

  /**
   * Use the constructor were all the needed dependencies for this entity are injected.
   */
  public IdsRepository(SharedPreferences sharedPreferences, Context context, String androidId) {
    this.sharedPreferences = sharedPreferences;
    this.context = context;
    this.androidId = androidId;
  }

  public synchronized String getUniqueIdentifier() {
    String aptoideId = sharedPreferences.getString(APTOIDE_CLIENT_UUID, null);
    if (!TextUtils.isEmpty(aptoideId)) {
      // if we already have the aptoide client uuid, return it
      return aptoideId;
    }
    // else, we generate a aptoide client uuid and save it locally for further use

    // preferred UUID
    aptoideId = getGoogleAdvertisingId();

    // if preferred UUID is null or empty use android id
    if (TextUtils.isEmpty(aptoideId)) {
      aptoideId = getAndroidId();
    }

    // if android id is null or empty use random generated UUID
    if (TextUtils.isEmpty(aptoideId)) {
      aptoideId = UUID.randomUUID()
          .toString();
    }

    sharedPreferences.edit()
        .putString(APTOIDE_CLIENT_UUID, aptoideId)
        .apply();
    return aptoideId;
  }

  @WorkerThread public synchronized String getGoogleAdvertisingId() {

    String googleAdvertisingId = sharedPreferences.getString(GOOGLE_ADVERTISING_ID_CLIENT, null);
    if (!TextUtils.isEmpty(googleAdvertisingId)) {
      return googleAdvertisingId;
    }

    if (AptoideUtils.ThreadU.isUiThread()) {
      throw new IllegalStateException("You cannot run this method from the main thread");
    }

    if (DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(context)) {
      try {
        googleAdvertisingId = AdvertisingIdClient.getAdvertisingIdInfo(context)
            .getId();
      } catch (Exception e) {
        CrashReport.getInstance()
            .log(e);
      }
    }

    sharedPreferences.edit()
        .putString(GOOGLE_ADVERTISING_ID_CLIENT, googleAdvertisingId)
        .apply();
    sharedPreferences.edit()
        .putBoolean(GOOGLE_ADVERTISING_ID_CLIENT_SET, true)
        .apply();

    return googleAdvertisingId;
  }

  public synchronized String getAdvertisingId() {
    String advertisingId = sharedPreferences.getString(ADVERTISING_ID_CLIENT, null);
    if (!TextUtils.isEmpty(advertisingId)) {
      // if we already have an advertising id, return it
      return advertisingId;
    }

    // else, generate using google advertising id
    advertisingId = getGoogleAdvertisingId();
    if (TextUtils.isEmpty(advertisingId)) {
      // if google advertising id is not available use a random id
      advertisingId = generateRandomAdvertisingId();
    }

    // save the generated advertising id for this user and return it
    sharedPreferences.edit()
        .putString(ADVERTISING_ID_CLIENT, advertisingId)
        .apply();
    return advertisingId;
  }

  public synchronized String getAndroidId() {
    String androidId = sharedPreferences.getString(ANDROID_ID_CLIENT, null);
    if (!TextUtils.isEmpty(androidId)) {
      // if we already have an android id, return it
      return androidId;
    }
    // else, generate a new android id using device id
    androidId = this.androidId;
    if (sharedPreferences.getString(ANDROID_ID_CLIENT, null) != null) {
      throw new RuntimeException("Android ID already set!");
    }

    sharedPreferences.edit()
        .putString(ANDROID_ID_CLIENT, androidId)
        .apply();
    return androidId;
  }

  private String generateRandomAdvertisingId() {
    byte[] data = new byte[16];
    String androidId = this.androidId;

    if (androidId == null) {
      androidId = UUID.randomUUID()
          .toString();
    }

    SecureRandom secureRandom = new SecureRandom();
    secureRandom.setSeed(androidId.hashCode());
    secureRandom.nextBytes(data);
    return UUID.nameUUIDFromBytes(data)
        .toString();
  }
}
