package cm.aptoide.pt.view.share;

import android.support.annotation.NonNull;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.FacebookSignUpException;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trinkes on 28/09/2017.
 */

public class NotLoggedInShareAnalytics {
  public static final String POP_UP_SHARE_TIMELINE = "Pop_Up_Share_On_Timeline_Interact";
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
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;
  private final AccountAnalytics accountAnalytics;
  private Map<String, Object> loginEventMap;

  public NotLoggedInShareAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker, AccountAnalytics accountAnalytics) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
    this.accountAnalytics = accountAnalytics;
  }

  public void sendGoogleLoginButtonPressed() {
    accountAnalytics.sendGoogleLoginButtonPressed();
    loginEventMap = createMap(LOGIN_GOOGLE_PARAMETER, NONE_PARAMETER);
  }

  public void sendFacebookLoginButtonPressed() {
    accountAnalytics.sendFacebookLoginButtonPressed();
    loginEventMap = createMap(LOGIN_FACEBOOK_PARAMETER, NONE_PARAMETER);
  }

  public void sendGoogleSignUpFailEvent() {
    analyticsManager.logEvent(createMap(LOGIN_GOOGLE_PARAMETER,LOGIN_INCOMPLETE_PARAMETER),POP_UP_SHARE_TIMELINE,
        AnalyticsManager.Action.CLICK,getViewName(true));
    loginEventMap = null;
  }

  private void sendFacebookMissingPermissionsEvent() {
    analyticsManager.logEvent(createMap(LOGIN_FACEBOOK_PARAMETER,LOGIN_INCOMPLETE_PARAMETER),POP_UP_SHARE_TIMELINE,
        AnalyticsManager.Action.CLICK,getViewName(true));
    loginEventMap = null;
  }

  private void sendFacebookUserCancelledEvent() {
    analyticsManager.logEvent(createMap(LOGIN_FACEBOOK_PARAMETER,LOGIN_INCOMPLETE_PARAMETER),POP_UP_SHARE_TIMELINE,
        AnalyticsManager.Action.CLICK,getViewName(true));
    loginEventMap = null;
  }

  private void sendFacebookErrorEvent() {
    analyticsManager.logEvent(createMap(LOGIN_FACEBOOK_PARAMETER,LOGIN_INCOMPLETE_PARAMETER),POP_UP_SHARE_TIMELINE,
        AnalyticsManager.Action.CLICK,getViewName(true));
    loginEventMap = null;
  }

  public void loginSuccess() {
    accountAnalytics.loginSuccess();
  }

  public void sendBackButtonPressed() {
    analyticsManager.logEvent(createMap("Tap on Back Button", NONE_PARAMETER),POP_UP_SHARE_TIMELINE,
        AnalyticsManager.Action.CLICK,getViewName(true));
  }

  public void sendTapOutside() {
    analyticsManager.logEvent(createMap("Tap Outside", NONE_PARAMETER),POP_UP_SHARE_TIMELINE,
        AnalyticsManager.Action.CLICK,getViewName(true));
  }

  public void sendTapOnFakeToolbar() {
    analyticsManager.logEvent(createMap("Tap on Install - Login - Share image", NONE_PARAMETER), POP_UP_SHARE_TIMELINE, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendTapOnFakeTimeline() {
    analyticsManager.logEvent(createMap("Tap on Timeline image", NONE_PARAMETER),POP_UP_SHARE_TIMELINE,
        AnalyticsManager.Action.CLICK,getViewName(true));
  }

  public void sendShareSuccess() {
    loginEventMap.put(STATUS_PARAMETER_NAME, SHARE_SUCCESS_PARAMETER);
    analyticsManager.logEvent(loginEventMap,POP_UP_SHARE_TIMELINE, AnalyticsManager.Action.CLICK,getViewName(false));
    loginEventMap = null;
  }

  public void sendShareFail() {
    loginEventMap.put(STATUS_PARAMETER_NAME, SHARE_FAILED_PARAMETER);
    analyticsManager.logEvent(loginEventMap,POP_UP_SHARE_TIMELINE, AnalyticsManager.Action.CLICK,getViewName(false));
    loginEventMap = null;
  }

  @NonNull private Map<String,Object> createMap(String action, String status) {
    Map<String, Object> map = new HashMap<>();
    map.put(SOURCE_PARAMETER_NAME, APP_VIEW_PARAMETER);
    map.put(ACTION_PARAMETER_NAME, action);
    map.put(STATUS_PARAMETER_NAME, status);
    return map;
  }


  public void sendCloseEvent() {
    analyticsManager.logEvent(createMap(CLOSE_PARAMETER,NONE_PARAMETER),POP_UP_SHARE_TIMELINE,
        AnalyticsManager.Action.CLICK,getViewName(true));
  }

  public void sendSignUpErrorEvent(AccountAnalytics.LoginMethod loginMethod, Throwable throwable) {
    accountAnalytics.sendLoginErrorEvent(loginMethod, throwable);
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

  private String getViewName(boolean isCurrent){
    String viewName = "";
    if(isCurrent){
      viewName = navigationTracker.getCurrentViewName();
    }
    else{
      viewName = navigationTracker.getPreviousViewName();
    }
    if(viewName.equals("")) {
      return "NotLoggedInShareAnalytics"; //Default value, shouldn't get here
    }
    return viewName;
  }
}
