/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.preferences.secure;

import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by neuro on 21-04-2016.
 */
public class SecurePreferences {

	public static String getAccessToken() {
		return SecurePreferencesImplementation.getInstance().getString(SecureKeys.ACCESS_TOKEN, null);
	}

	public static void setAccessToken(String accessToken) {
		SecurePreferencesImplementation.getInstance().edit().putString(SecureKeys.ACCESS_TOKEN, accessToken).apply();
	}

	public static String getAptoideClientUUID() {
		SharedPreferences sharedPreferences = SecurePreferencesImplementation.getInstance();
		if (!sharedPreferences.contains(SecureKeys.APTOIDE_CLIENT_UUID)) {
			sharedPreferences.edit().putString(SecureKeys.APTOIDE_CLIENT_UUID, UUID.randomUUID().toString()).apply();
		}

		return sharedPreferences.getString(SecureKeys.APTOIDE_CLIENT_UUID, null);
	}
}
