/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 15/06/2016.
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
	 *
	 * @param key
	 * @param value
	 */
	public static void putString(String key, String value) {
		SecurePreferencesImplementation.getInstance().edit().putString(key, value).apply();
	}

	/**
	 * DO NOT USE THIS METHOD!!!!
	 *
	 * @param key
	 *
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
	 *
	 * @return
	 */
	public static boolean getBoolean(String key) {
		return SecurePreferencesImplementation.getInstance().getBoolean(key, false);
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

	public static int getAdultContentPin() {
		return SecurePreferencesImplementation.getInstance().getInt(SecureKeys.ADULT_CONTENT_PIN, 0);
	}

	public static void setAdultContentPin(int pin) {
		SecurePreferencesImplementation.getInstance().edit().putInt(SecureKeys.ADULT_CONTENT_PIN, pin).apply();
	}

	public static boolean isTimelineActive() {
		return SecurePreferencesImplementation.getInstance().getBoolean(SecureKeys.IS_TIMELINE_ACTIVE, false);
	}

	public static int getMatureSwitch() {
		return isAdultSwitchActive() ? 1 : 0;
	}

	public static void setAdultSwitch(boolean active) {
		SecurePreferencesImplementation.getInstance()
				.edit().putBoolean(SecureKeys.ADULT_CONTENT_SWITCH, active)
				.apply();
	}

	public static boolean isAdultSwitchActive() {
		return SecurePreferencesImplementation.getInstance().getBoolean(SecureKeys.ADULT_CONTENT_SWITCH, false);
	}

	public static String getAdvertisingId() {
		return SecurePreferencesImplementation.getInstance().getString(SecureKeys.ADVERTISING_ID_CLIENT, null);
	}

	public static void setAdvertisingId(String aaid) {
		if (getAdvertisingId() != null) {
			throw new RuntimeException("Advertising ID already set!");
		}

		SecurePreferencesImplementation.getInstance().edit().putString(SecureKeys.ADVERTISING_ID_CLIENT, aaid).apply();
	}
}
