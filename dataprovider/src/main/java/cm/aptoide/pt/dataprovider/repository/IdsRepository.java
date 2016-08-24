/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.dataprovider.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.security.SecureRandom;
import java.util.UUID;

import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import lombok.AllArgsConstructor;

/**
 * Created by neuro on 11-07-2016.
 */
@AllArgsConstructor
public class IdsRepository {

	private static final String APTOIDE_CLIENT_UUID = "aptoide_client_uuid";
	private static final String ADVERTISING_ID_CLIENT = "advertisingIdClient";
	private static final String ANDROID_ID_CLIENT = "androidId";
	private static final String GOOGLE_ADVERTISING_ID_CLIENT = "googleAdvertisingId";
	private static final String GOOGLE_ADVERTISING_ID_CLIENT_SET = "googleAdvertisingIdSet";

	private final SharedPreferences sharedPreferences;
	private final Context context;

	public String getAptoideClientUUID() {
		if (!sharedPreferences.contains(APTOIDE_CLIENT_UUID)) {
			generateAptoideId(sharedPreferences);
		}
		return sharedPreferences.getString(APTOIDE_CLIENT_UUID, null);
	}

	private void generateAptoideId(SharedPreferences sharedPreferences) {
		String aptoideId;
		if (getGoogleAdvertisingId() != null) {
			aptoideId = getGoogleAdvertisingId();
		} else if (getAndroidId() != null) {
			aptoideId = getAndroidId();
		} else {
			aptoideId = UUID.randomUUID().toString();
		}

		sharedPreferences.edit().putString(APTOIDE_CLIENT_UUID, aptoideId).apply();
	}

	public String getGoogleAdvertisingId() {
		if (!sharedPreferences.contains(GOOGLE_ADVERTISING_ID_CLIENT_SET)) {
			generateGAID();
		}

		return sharedPreferences.getString(GOOGLE_ADVERTISING_ID_CLIENT, null);
	}

	private void generateGAID() {

		String gaid = null;

		if (DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable()) {
			try {
				gaid = AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		sharedPreferences.edit().putString(GOOGLE_ADVERTISING_ID_CLIENT, gaid).apply();
		sharedPreferences.edit().putBoolean(GOOGLE_ADVERTISING_ID_CLIENT_SET, true).apply();
	}

	public String getAdvertisingId() {
		String advertisingId = sharedPreferences.getString(ADVERTISING_ID_CLIENT, null);

		if (advertisingId == null) {
			advertisingId = generateAdvertisingId();
		}

		return advertisingId;
	}

	private void setAdvertisingId(String aaid) {
		sharedPreferences.edit().putString(ADVERTISING_ID_CLIENT, aaid).apply();
	}

	private String generateAdvertisingId() {
		final String advertisingId;

		if (getGoogleAdvertisingId() != null) {
			setAdvertisingId(advertisingId = getGoogleAdvertisingId());
		} else {
			setAdvertisingId(advertisingId = generateRandomAdvertisingID());
		}

		return advertisingId;
	}

	public String getAndroidId() {
		return sharedPreferences.getString(ANDROID_ID_CLIENT, null);
	}

	private void setAndroidId(String android) {
		if (getAndroidId() != null) {
			throw new RuntimeException("Android ID already set!");
		}

		sharedPreferences.edit().putString(ANDROID_ID_CLIENT, android).apply();
	}

	private String generateRandomAdvertisingID() {
		byte[] data = new byte[16];
		String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		if (deviceId == null) {
			deviceId = UUID.randomUUID().toString();
		}

		SecureRandom secureRandom = new SecureRandom();
		secureRandom.setSeed(deviceId.hashCode());
		secureRandom.nextBytes(data);
		return UUID.nameUUIDFromBytes(data).toString();
	}
}
