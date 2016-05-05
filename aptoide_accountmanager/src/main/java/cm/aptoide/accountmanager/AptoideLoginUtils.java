package cm.aptoide.accountmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import cm.aptoide.accountmanager.ws.LoginMode;

/**
 * Created by trinkes on 4/26/16.
 */
class AptoideLoginUtils {

	public static final String APTOIDE_LOGIN_USER_NAME_KEY = "aptoide_login_user_name";
	public static final String APTOIDE_LOGIN_PASSWORD_KEY = "aptoide_login_password";
	public static final String APTOIDE_LOGIN_REFRESH_TOKEN_KEY = "aptoide_login_refresh_token";
	public static final String APTOIDE_LOGIN_ACCESS_TOKEN_KEY = "aptoide_login_access_token";
	static int REQ_SIGNUP = 8;

	static void setupAptoideLogin(Activity activity, Button loginButton, Button registerButton) {

		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = AptoideAccountManager.getInstance().getIntroducedUserName();
				String password = AptoideAccountManager.getInstance().getIntroducedPassword();

				if (username == null || password == null || (username.length() == 0 || password
						.length() == 0)) {
					Toast.makeText(v.getContext(), R.string.fields_cannot_empty, Toast.LENGTH_LONG)
							.show();
					return;
				}

				AptoideAccountManager.loginUserCredentials(LoginMode.APTOIDE, username, password,
						null);
			}
		});

		final WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent signup = new Intent(v.getContext(), SignUpActivity.class);
				Activity activity = activityWeakReference.get();
				if (activity != null) {
					activity.startActivityForResult(signup, REQ_SIGNUP);
				}
			}
		});
	}

	public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent
			data) {
		if (requestCode == REQ_SIGNUP && resultCode == Activity.RESULT_OK) {
			Bundle bundle = data.getExtras();
			String userName = (String) bundle.get(APTOIDE_LOGIN_USER_NAME_KEY);
			String password = (String) bundle.get(APTOIDE_LOGIN_PASSWORD_KEY);
			String refreshToken = (String) bundle.get(APTOIDE_LOGIN_REFRESH_TOKEN_KEY);
			String accessToken = (String) bundle.get(APTOIDE_LOGIN_ACCESS_TOKEN_KEY);
			AccountManagerPreferences.setAccessToken(accessToken);
			AptoideAccountManager.getInstance()
					.addLocalUserAccount(userName, password, null, refreshToken, accessToken);
			AptoideAccountManager.getInstance().onLoginSuccess();
			activity.finish();
		}
	}
}
