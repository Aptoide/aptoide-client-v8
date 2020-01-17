/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.networking;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import androidx.annotation.WorkerThread;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import java.security.SecureRandom;
import java.util.UUID;
import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 11-07-2016.
 */
public class IdsRepository {

  private static final String TAG = IdsRepository.class.getSimpleName();

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

  public synchronized Single<String> getUniqueIdentifier() {
    String aptoideId = sharedPreferences.getString(APTOIDE_CLIENT_UUID, null);

    if (!TextUtils.isEmpty(aptoideId)) {
      // if we already have the aptoide client uuid, return it
      Logger.getInstance()
          .v(TAG, "getUniqueIdentifier: in sharedPreferences: " + aptoideId);
      return Single.just(aptoideId);
    }

    return getGoogleAdvertisingId().map(id -> {
      if (!TextUtils.isEmpty(id)) {
        return id;
      } else {
        return "";
      }
    })
        .map(id -> {
          if (!TextUtils.isEmpty(id)) {
            return id;
          } else {
            return UUID.randomUUID()
                .toString();
          }
        })
        .doOnSuccess(id -> sharedPreferences.edit()
            .putString(APTOIDE_CLIENT_UUID, id)
            .apply());
  }

  @WorkerThread public synchronized Single<String> getGoogleAdvertisingId() {

    return Single.just(sharedPreferences.getString(GOOGLE_ADVERTISING_ID_CLIENT, null))
        .map(id -> {
          if (!TextUtils.isEmpty(id)) {
            return id;
          } else if (AptoideUtils.ThreadU.isUiThread()) {
            throw new IllegalStateException("You cannot run this method from the main thread");
          } else if (!AdNetworkUtils.isGooglePlayServicesAvailable(context)) {
            return "";
          } else {
            try {
              return AdvertisingIdClient.getAdvertisingIdInfo(context)
                  .getId();
            } catch (Exception e) {
              throw new IllegalStateException(e);
            }
          }
        })
        .doOnSuccess(id -> {
          if (!id.equals("")) {
            sharedPreferences.edit()
                .putString(GOOGLE_ADVERTISING_ID_CLIENT, id)
                .apply();
            sharedPreferences.edit()
                .putBoolean(GOOGLE_ADVERTISING_ID_CLIENT_SET, true)
                .apply();
          }
        })
        .subscribeOn(Schedulers.newThread());
  }

  public synchronized Single<String> getAdvertisingId() {
    String advertisingId = sharedPreferences.getString(ADVERTISING_ID_CLIENT, null);
    if (!TextUtils.isEmpty(advertisingId)) {
      // if we already have an advertising id, return it
      return Single.just(advertisingId);
    }

    return getGoogleAdvertisingId().map(id -> {
      if (!TextUtils.isEmpty(id)) {
        return id;
      } else {
        return "";
      }
    })
        .map(id -> {
          if (!TextUtils.isEmpty(id)) {
            return id;
          } else {
            return generateRandomAdvertisingId();
          }
        })
        .doOnSuccess(id -> sharedPreferences.edit()
            .putString(ADVERTISING_ID_CLIENT, advertisingId)
            .apply());
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
