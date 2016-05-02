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

	public static void remove(String key) {
		SecurePreferencesImplementation.getInstance().edit().remove(key).apply();
	}

}
