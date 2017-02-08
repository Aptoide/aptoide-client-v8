/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.accountmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cm.aptoide.accountmanager.ws.LoginMode;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.GenericDialogs;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by trinkes on 4/19/16.
 */
class FacebookLoginUtils {

  public static final String FIRST_NAME = "first_name";
  public static final String LAST_NAME = "last_name";
  public static final String EMAIL = "email";
  public static final String GENDER = "gender";
  public static final String BIRTHDAY = "birthday";
  public static final String LOCATION = "location";
  public static final String PROFILE_PIC = "profile_pic";
  public static final String FACEBOOK_ID = "idFacebook";
  public static final String USER_FRIENDS = "user_friends";
  private static final String TAG = FacebookLoginUtils.class.getSimpleName();
  static CallbackManager callbackManager;

  static protected void setupFacebook(Activity activity, LoginButton mFacebookLoginButton,
      AptoideAccountManager accountManager) {
    FacebookSdk.sdkInitialize(mFacebookLoginButton.getContext().getApplicationContext());
    callbackManager = CallbackManager.Factory.create();
    mFacebookLoginButton.setReadPermissions(Arrays.asList(EMAIL, USER_FRIENDS));
    LoginManager.getInstance()
        .registerCallback(callbackManager,
            new FacebookLoginCallback(new WeakReference<Activity>(activity), accountManager));
  }

  public static void onActivityResult(int requestCode, int resultCode, Intent data) {
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }

  protected static Bundle getFacebookData(JSONObject object) {

    Bundle bundle = null;
    try {
      bundle = new Bundle();
      String id = object.getString("id");

      URL profile_pic = new URL("https://graph.facebook.com/" + id +
          "/picture?width=200&height=150");
      bundle.putString(PROFILE_PIC, profile_pic.toString());

      bundle.putString(FACEBOOK_ID, id);
      if (object.has(FIRST_NAME)) bundle.putString(FIRST_NAME, object.getString(FIRST_NAME));
      if (object.has(LAST_NAME)) bundle.putString(LAST_NAME, object.getString(LAST_NAME));
      if (object.has(EMAIL)) bundle.putString(EMAIL, object.getString(EMAIL));
      if (object.has(GENDER)) bundle.putString(GENDER, object.getString(GENDER));
      if (object.has(BIRTHDAY)) bundle.putString(BIRTHDAY, object.getString(BIRTHDAY));
      if (object.has(USER_FRIENDS)) bundle.putString(USER_FRIENDS, object.getString(USER_FRIENDS));

      if (object.has(LOCATION)) {
        bundle.putString(LOCATION, object.getJSONObject(LOCATION).getString("name"));
      }
    } catch (JSONException | MalformedURLException e) {
      CrashReport.getInstance().log(e);
      e.printStackTrace();
    }
    return bundle;
  }

  public static void logout() {
    LoginManager.getInstance().logOut();
  }

  static class FacebookLoginCallback implements FacebookCallback<LoginResult> {

    WeakReference<Activity> weakContext;
    private final AptoideAccountManager accountManager;

    public FacebookLoginCallback(WeakReference<Activity> activityWeakReference,
        AptoideAccountManager accountManager) {
      this.weakContext = activityWeakReference;
      this.accountManager = accountManager;
    }

    /**
     * Facebook onSuccess
     *
     * @param loginResult User Data given by facebook
     */
    @Override

    public void onSuccess(LoginResult loginResult) {
      AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();

      if (hasDeclinedPermissions(currentAccessToken.getDeclinedPermissions())) {
        return;
      }

      final String token = currentAccessToken.getToken();
      GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
          new GraphRequest.GraphJSONObjectCallback() {

            @Override public void onCompleted(JSONObject object, GraphResponse response) {
              Logger.i("LoginActivity", response.toString());
              // Get facebook data from login
              Bundle bFacebookData = FacebookLoginUtils.getFacebookData(object);
              String userName = bFacebookData.containsKey(EMAIL) ? bFacebookData.getString(EMAIL)
                  : bFacebookData.getString(FACEBOOK_ID);
              accountManager.login(LoginMode.FACEBOOK, userName, token, null,
                  weakContext.get());
            }
          });

      Bundle parameters = new Bundle();
      parameters.putString("fields", "id, email");
      request.setParameters(parameters);
      request.executeAsync();
    }

    /**
     * Facebook onCancel
     */
    @Override public void onCancel() {
      accountManager
          .onLoginFail(Application.getContext().getString(R.string.user_cancelled));
    }

    /**
     * Facebook onError
     *
     * @param error error
     */
    @Override public void onError(FacebookException error) {
      Logger.e(TAG, "onError: " + error.toString());
      accountManager
          .onLoginFail(Application.getContext().getString(R.string.error_occured));
    }

    /**
     * check if user declined permissions while logging in and take care of them if he did
     *
     * @param declinedPermissions declined permissions
     * @return true if user has declined mandatory permissions, false otherwise
     */
    private boolean hasDeclinedPermissions(Set<String> declinedPermissions) {
      boolean hasDeclinedPermissions = false;
      for (String declinedPermission : declinedPermissions) {
        switch (declinedPermission) {
          case EMAIL:
            hasDeclinedPermissions = true;
            askForMailAgain();
            break;
          case USER_FRIENDS:
            Logger.e(TAG, "hasDeclinedPermissions: " + declinedPermission);
            break;
          default:
            Logger.e(TAG,
                "hasDeclinedPermissions: check if we need the permission" + declinedPermission);
        }
      }
      return hasDeclinedPermissions;
    }

    private void askForMailAgain() {
      final Activity activity = weakContext.get();
      if (activity != null) {
        GenericDialogs.createGenericOkCancelMessage(activity, null,
            R.string.facebook_email_permission_regected_message,
            R.string.facebook_grant_permission_button, android.R.string.cancel)
            .subscribe(eResponse -> {
              if (eResponse == GenericDialogs.EResponse.YES) {
                LoginManager.getInstance()
                    .logInWithReadPermissions(activity, Collections.singletonList(EMAIL));
              }
            });
      }
    }
  }
}
