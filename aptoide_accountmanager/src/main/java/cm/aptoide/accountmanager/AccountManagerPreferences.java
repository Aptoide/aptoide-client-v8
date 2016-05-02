package cm.aptoide.accountmanager;

import cm.aptoide.pt.preferences.secure.SecurePreferences;

/**
 * Created by trinkes on 5/2/16.
 */
class AccountManagerPreferences {

	static String getAccessToken() {
		return SecurePreferences.getString(SecureKeys.ACCESS_TOKEN);
	}

	static void setAccessToken(String accessToken) {
		SecurePreferences.putString(SecureKeys.ACCESS_TOKEN, accessToken);
	}

	static void removeAccessToken() {
		SecurePreferences.remove(SecureKeys.ACCESS_TOKEN);
	}

	static String getRefreshToken() {
		return SecurePreferences.getString(SecureKeys.REFRESH_TOKEN);
	}

	static void setRefreshToken(String refreshToken) {
		SecurePreferences.putString(SecureKeys.REFRESH_TOKEN, refreshToken);
	}

	static void removeRefreshToken() {
		SecurePreferences.remove(SecureKeys.REFRESH_TOKEN);
	}

	static String getUserName() {
		return SecurePreferences.getString(SecureKeys.USER_NAME);
	}

	static void setUserName(String userName) {
		SecurePreferences.putString(SecureKeys.USER_NAME, userName);
	}

	static void removeUserName() {
		SecurePreferences.remove(SecureKeys.USER_NAME);
	}
}
