package cm.aptoide.pt.view.share;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.FacebookSignUpException;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by trinkes on 28/09/2017.
 */

public class NotLoggedInShareAnalytics {
  public static final String EVENT_NAME = "Pop_Up_Share_On_Timeline_Interact";
  public static final String NONE_PARAMETER = "None";
  public static final String CLOSE_PARAMETER = "Close";
  public static final String APP_VIEW_PARAMETER = "app_view";
  public static final String SOURCE_PARAMETER_NAME = "source";
  public static final String ACTION_PARAMETER_NAME = "action";
  public static final String STATUS_PARAMETER_NAME = "status";
  public static final String SHARE_SUCCESS_PARAMETER = "Share success";
  public static final String SHARE_FAILED_PARAMETER = "Share failed";
  public static final String LOGIN_INCOMPLETE_PARAMETER = "Login incomplete";
  public static final String LOGIN_GOOGLE_PARAMETER = "Login Google";
  public static final String LOGIN_FACEBOOK_PARAMETER = "Login Facebook";
  private final AccountAnalytics accountAnalytics;
  private final AppEventsLogger facebook;
  private final Analytics analytics;
  private Bundle loginEventBundle;

  public NotLoggedInShareAnalytics(AccountAnalytics accountAnalytics, AppEventsLogger facebook,
      Analytics analytics) {
    this.accountAnalytics = accountAnalytics;
    this.facebook = facebook;
    this.analytics = analytics;
  }

  public void sendGoogleLoginButtonPressed() {
    accountAnalytics.sendGoogleLoginButtonPressed();
    loginEventBundle = createBundle(LOGIN_GOOGLE_PARAMETER, NONE_PARAMETER);
  }

  public void sendFacebookLoginButtonPressed() {
    accountAnalytics.sendFacebookLoginButtonPressed();
    loginEventBundle = createBundle(LOGIN_FACEBOOK_PARAMETER, NONE_PARAMETER);
  }

  public void sendGoogleSignUpFailEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, EVENT_NAME,
        createBundle(LOGIN_GOOGLE_PARAMETER, LOGIN_INCOMPLETE_PARAMETER)));
    loginEventBundle = null;
  }

  private void sendFacebookMissingPermissionsEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, EVENT_NAME,
        createBundle(LOGIN_FACEBOOK_PARAMETER, LOGIN_INCOMPLETE_PARAMETER)));
    loginEventBundle = null;
  }

  private void sendFacebookUserCancelledEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, EVENT_NAME,
        createBundle(LOGIN_FACEBOOK_PARAMETER, LOGIN_INCOMPLETE_PARAMETER)));
    loginEventBundle = null;
  }

  private void sendFacebookErrorEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, EVENT_NAME,
        createBundle(LOGIN_FACEBOOK_PARAMETER, LOGIN_INCOMPLETE_PARAMETER)));
    loginEventBundle = null;
  }

  public void loginSuccess() {
    accountAnalytics.loginSuccess();
  }

  public void sendBackButtonPressed() {
    analytics.sendEvent(new FacebookEvent(facebook, EVENT_NAME,
        createBundle("Tap on Back Button", NONE_PARAMETER)));
  }

  public void sendTapOutside() {
    analytics.sendEvent(
        new FacebookEvent(facebook, EVENT_NAME, createBundle("Tap Outside", NONE_PARAMETER)));
  }

  public void sendTapOnFakeToolbar() {
    analytics.sendEvent(new FacebookEvent(facebook, EVENT_NAME,
        createBundle("Tap on Install - Login - Share image", NONE_PARAMETER)));
  }

  public void sendTapOnFakeTimeline() {
    analytics.sendEvent(new FacebookEvent(facebook, EVENT_NAME,
        createBundle("Tap on Timeline image", NONE_PARAMETER)));
  }

  public void sendShareSuccess() {
    loginEventBundle.putString(STATUS_PARAMETER_NAME, SHARE_SUCCESS_PARAMETER);
    analytics.sendEvent(new FacebookEvent(facebook, EVENT_NAME, loginEventBundle));
    loginEventBundle = null;
  }

  public void sendShareFail() {
    loginEventBundle.putString(STATUS_PARAMETER_NAME, SHARE_FAILED_PARAMETER);
    analytics.sendEvent(new FacebookEvent(facebook, EVENT_NAME, loginEventBundle));
    loginEventBundle = null;
  }

  @NonNull private Bundle createBundle(String action, String status) {
    Bundle bundle = new Bundle();
    bundle.putString(SOURCE_PARAMETER_NAME, APP_VIEW_PARAMETER);
    bundle.putString(ACTION_PARAMETER_NAME, action);
    bundle.putString(STATUS_PARAMETER_NAME, status);
    return bundle;
  }

  public void sendCloseEvent() {
    new FacebookEvent(facebook, EVENT_NAME, createBundle(CLOSE_PARAMETER, NONE_PARAMETER));
  }

  public void sendSignUpErrorEvent(AccountAnalytics.LoginMethod loginMethod, Throwable throwable) {
    accountAnalytics.sendSignUpErrorEvent(loginMethod, throwable);
    if (loginMethod.equals(AccountAnalytics.LoginMethod.GOOGLE)) {
      sendGoogleSignUpFailEvent();
    }
    if (throwable instanceof FacebookSignUpException) {
      sendFacebookErrorAnalytics((FacebookSignUpException) throwable);
    }
  }

  private void sendFacebookErrorAnalytics(FacebookSignUpException facebookException) {
    switch (facebookException.getCode()) {
      case FacebookSignUpException.MISSING_REQUIRED_PERMISSIONS:
        sendFacebookMissingPermissionsEvent();
        break;
      case FacebookSignUpException.USER_CANCELLED:
        sendFacebookUserCancelledEvent();
        break;
      case FacebookSignUpException.ERROR:
        sendFacebookErrorEvent();
        break;
    }
  }
}
