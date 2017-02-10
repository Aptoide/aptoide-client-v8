/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/06/2016.
 */

package cm.aptoide.accountmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import cm.aptoide.accountmanager.ws.LoginMode;
import cm.aptoide.pt.utils.design.ShowMessage;
import java.lang.ref.WeakReference;

/**
 * Created by trinkes on 4/26/16.
 */
public class AptoideLoginUtils {

  public static final String IS_FACEBOOK_OR_GOOGLE = "facebook_google";
  public static final String APTOIDE_LOGIN_USER_NAME_KEY = "aptoide_login_user_name";
  public static final String APTOIDE_LOGIN_PASSWORD_KEY = "aptoide_login_password";
  static final String APTOIDE_LOGIN_REFRESH_TOKEN_KEY = "aptoide_login_refresh_token";
  public static final String APTOIDE_LOGIN_ACCESS_TOKEN_KEY = "aptoide_login_access_token";
  public static final String APTOIDE_LOGIN_FROM = "aptoide_login_from";
  private static int REQ_SIGNUP = 8;

  public static void onActivityResult(Activity activity, int requestCode, int resultCode,
      Intent data, AptoideAccountManager accountManager) {
    if (requestCode == REQ_SIGNUP && resultCode == Activity.RESULT_OK) {
      Bundle bundle = data.getExtras();
      String userName = (String) bundle.get(APTOIDE_LOGIN_USER_NAME_KEY);
      String password = (String) bundle.get(APTOIDE_LOGIN_PASSWORD_KEY);
      String refreshToken = (String) bundle.get(APTOIDE_LOGIN_REFRESH_TOKEN_KEY);
      String accessToken = (String) bundle.get(APTOIDE_LOGIN_ACCESS_TOKEN_KEY);
      String loginOrigin = (String) bundle.get(APTOIDE_LOGIN_FROM);
      AccountManagerPreferences.setAccessToken(accessToken);
      accountManager
          .createAccount(userName, password, null, refreshToken, accessToken);
      accountManager.setAccessTokenOnLocalAccount(accessToken, null,
          SecureKeys.ACCESS_TOKEN);
      accountManager
          .onLoginSuccess(LoginMode.APTOIDE, loginOrigin, userName, password);
      activity.finish();
    }
  }
}
