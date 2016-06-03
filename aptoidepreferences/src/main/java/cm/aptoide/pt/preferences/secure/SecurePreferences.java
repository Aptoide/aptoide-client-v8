/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.preferences.secure;

import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by neuro on 21-04-2016.
 */
public class SecurePreferences {

	public static String getAptoideClientUUID() {
		SharedPreferences sharedPreferences = SecurePreferencesImplementation.getInstance();
		if (!sharedPreferences.contains(SecureKeys.APTOIDE_CLIENT_UUID)) {
			sharedPreferences.edit().putString(SecureKeys.APTOIDE_CLIENT_UUID, UUID.randomUUID().toString()).apply();
		}

		return sharedPreferences.getString(SecureKeys.APTOIDE_CLIENT_UUID, null);
	}

	public static boolean isUserDataLoaded() {
		return SecurePreferencesImplementation.getInstance().getBoolean(SecureKeys.USER_DATA_LOADED, false);
	}

	public static void setUserDataLoaded() {
		SecurePreferencesImplementation.getInstance().edit().putBoolean(SecureKeys.USER_DATA_LOADED, true).apply();
	}

	public static boolean isFirstRun() {
		return SecurePreferencesImplementation.getInstance().getBoolean(SecureKeys.FIRST_RUN, true);
	}

	public static void setFirstRun() {
		SecurePreferencesImplementation.getInstance().edit().putBoolean(SecureKeys.FIRST_RUN, true).apply();
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

	public static void remove(String key) {
		SecurePreferencesImplementation.getInstance().edit().remove(key).apply();
	}

	/**
	 * DO NOT USE THIS METHOD
	 *
	 * @param key
	 * @param value
	 */
	public static void putBoolean(String key, boolean value) {
		SecurePreferencesImplementation.getInstance().edit().putBoolean(key, value).apply();
	}

	/**
	 * DO NOT USE THIS METHOD!!!!
	 *
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(String key) {
		return SecurePreferencesImplementation.getInstance().getBoolean(key, false);
	}
}
