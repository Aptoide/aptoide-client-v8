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

	public static void removeAccessToken() {
		SecurePreferencesImplementation.getInstance().edit().remove(SecureKeys.ACCESS_TOKEN).apply();
	}

	public static String getRefreshToken() {
		return SecurePreferencesImplementation.getInstance().getString(SecureKeys.REFRESH_TOKEN, null);
	}

	public static void setRefreshToken(String accessToken) {
		SecurePreferencesImplementation.getInstance().edit().putString(SecureKeys.REFRESH_TOKEN, accessToken).apply();
	}

	public static void removeRefreshToken() {
		SecurePreferencesImplementation.getInstance().edit().remove(SecureKeys.REFRESH_TOKEN).apply();
	}
	public static String getUserName() {
		// TODO: 4/29/16 trinkes change to use the "wrapper"
		return SecurePreferencesImplementation.getInstance().getString(SecureKeys.USER_NAME, null);
	}

	public static void setUserName(String accessToken) {
		SecurePreferencesImplementation.getInstance().edit().putString(SecureKeys.USER_NAME, accessToken).apply();
	}

	public static void removeUserName() {
		SecurePreferencesImplementation.getInstance().edit().remove(SecureKeys.USER_NAME).apply();
	}

	public static String getAptoideClientUUID() {
		SharedPreferences sharedPreferences = SecurePreferencesImplementation.getInstance();
		if (!sharedPreferences.contains(SecureKeys.APTOIDE_CLIENT_UUID)) {
			sharedPreferences.edit().putString(SecureKeys.APTOIDE_CLIENT_UUID, UUID.randomUUID().toString()).apply();
		}

		return sharedPreferences.getString(SecureKeys.APTOIDE_CLIENT_UUID, null);
	}

    /**
     * DO NOT USE THIS METHOD
     * @param key
     * @param value
     */
    public static void putString(String key, String value) {
        SecurePreferencesImplementation.getInstance().edit().putString(key, value).apply();
    }

    /**
     * DO NOT USE THIS METHOD!!!!
     * @param key
     * @return
     */
    public static String getString(String key) {
        return SecurePreferencesImplementation.getInstance().getString(key, null);
    }

}
