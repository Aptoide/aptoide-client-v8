/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.dataprovider.repository;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * Created by neuro on 11-07-2016.
 */
public class IdsRepositoryImpl implements IdsRepository, AptoideClientUUID {

  private static final String APTOIDE_CLIENT_UUID = "aptoide_client_uuid";
  private static final String ADVERTISING_ID_CLIENT = "advertisingIdClient";
  private static final String ANDROID_ID_CLIENT = "androidId";
  private static final String GOOGLE_ADVERTISING_ID_CLIENT = "googleAdvertisingId";
  private static final String GOOGLE_ADVERTISING_ID_CLIENT_SET = "googleAdvertisingIdSet";

  private final SharedPreferences sharedPreferences;
  private final Context context;
  private final String deviceId;

  public IdsRepositoryImpl(SharedPreferences sharedPreferences, Context context, String deviceId) {
    this.sharedPreferences = sharedPreferences;
    this.context = context;
    this.deviceId = deviceId;
  }

  @Deprecated @Partners
  /**
   * Use the constructor were all the needed dependencies for this entity are injected.
   */ public IdsRepositoryImpl(SharedPreferences sharedPreferences, Context context) {
    this.sharedPreferences = sharedPreferences;
    this.context = context;
    deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
  }

  @Override public synchronized String getUniqueIdentifier() {
    String aptoideId = sharedPreferences.getString(APTOIDE_CLIENT_UUID, null);

    if (!TextUtils.isEmpty(aptoideId)) {
      // if we already have the aptoide client uuid, return it
      return aptoideId;
    }
    // else, we generate a aptoide client uuid and save it locally for further use

    // preferred UUID
    aptoideId = getGoogleAdvertisingId();

    // if preferred UUID is null or empty use android id
    //if (TextUtils.isEmpty(aptoideId)) {
    //  aptoideId = getAndroidId();
    //}
//
    //// if android id is null or empty use random generated UUID
    //if (TextUtils.isEmpty(aptoideId)) {
    //  aptoideId = UUID.randomUUID().toString();
    //}

    //sharedPreferences.edit().putString(APTOIDE_CLIENT_UUID, aptoideId).apply();

    return aptoideId;
  }



  @Override public synchronized String getGoogleAdvertisingId() {
    String googleAdvertisingId = sharedPreferences.getString(GOOGLE_ADVERTISING_ID_CLIENT, null);
    if (!TextUtils.isEmpty(googleAdvertisingId)) {
      return googleAdvertisingId;
    }

    //if (DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(context)) {
    //  try {
    //    googleAdvertisingId = AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
    //  } catch (Exception e) {
    //    CrashReport.getInstance().log(e);
    //  }
    //}

    //sharedPreferences.edit().putString(GOOGLE_ADVERTISING_ID_CLIENT, googleAdvertisingId).apply();
    //sharedPreferences.edit().putBoolean(GOOGLE_ADVERTISING_ID_CLIENT_SET, true).apply();


    //ADDED
    GoogleIDThread googleIDThread = new GoogleIDThread();
    Thread gIDThread = new Thread(googleIDThread);
    gIDThread.start();


    googleAdvertisingId = googleIDThread.getGoogleId();

    //if (TextUtils.isEmpty(googleAdvertisingId)) {
    //  googleAdvertisingId = getAndroidId();
    //}
//
    //// if android id is null or empty use random generated UUID
    //if (TextUtils.isEmpty(googleAdvertisingId)) {
    //  googleAdvertisingId = UUID.randomUUID().toString();
    //}
    //sharedPreferences.edit().putString(APTOIDE_CLIENT_UUID, googleAdvertisingId).apply();
    return googleAdvertisingId;
  }


  private class GoogleIDThread implements Runnable{
    private String googleId;

    @Override
    public void run(){
      try{
        AdvertisingIdClient.Info adInfo = null;
        if (DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(context)) {
          try {
            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
          } catch (IOException e) {
            CrashReport.getInstance().log(e);
          }catch (IllegalStateException e) {
            CrashReport.getInstance().log(e);
          }catch (GooglePlayServicesNotAvailableException e) {
            CrashReport.getInstance().log(e);
          }catch (GooglePlayServicesRepairableException e) {
            CrashReport.getInstance().log(e);
          }
          if(adInfo != null){
            // preferred UUID
            googleId = adInfo.getId();
            sharedPreferences.edit().putString(GOOGLE_ADVERTISING_ID_CLIENT, googleId).apply();
            sharedPreferences.edit().putBoolean(GOOGLE_ADVERTISING_ID_CLIENT_SET, true).apply();

            //if preferred UUID is null or empty use android id
            if (TextUtils.isEmpty(googleId)) {
              googleId = getAndroidId();
            }
            // if android id is null or empty use random generated UUID
            if (TextUtils.isEmpty(googleId)) {
              googleId = UUID.randomUUID().toString();
            }
            sharedPreferences.edit().putString(APTOIDE_CLIENT_UUID, googleId).apply();
          }
        }
      }catch(Exception e){
        CrashReport.getInstance().log(e);
      }
    }

    public String getGoogleId(){
      return googleId;
    }
  }


  @Override public synchronized String getAdvertisingId() {
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
    sharedPreferences.edit().putString(ADVERTISING_ID_CLIENT, advertisingId).apply();
    return advertisingId;
  }

  @Override public synchronized String getAndroidId() {
    String androidId = sharedPreferences.getString(ANDROID_ID_CLIENT, null);
    if (!TextUtils.isEmpty(androidId)) {
      // if we already have an android id, return it
      return androidId;
    }
    // else, generate a new android id using device id
    androidId = deviceId;
    if (sharedPreferences.getString(ANDROID_ID_CLIENT, null) != null) {
      throw new RuntimeException("Android ID already set!");
    }

    sharedPreferences.edit().putString(ANDROID_ID_CLIENT, androidId).apply();
    return androidId;
  }

  private String generateRandomAdvertisingId() {
    byte[] data = new byte[16];
    String androidId = deviceId;

    if (androidId == null) {
      androidId = UUID.randomUUID().toString();
    }

    SecureRandom secureRandom = new SecureRandom();
    secureRandom.setSeed(androidId.hashCode());
    secureRandom.nextBytes(data);
    return UUID.nameUUIDFromBytes(data).toString();
  }
}
