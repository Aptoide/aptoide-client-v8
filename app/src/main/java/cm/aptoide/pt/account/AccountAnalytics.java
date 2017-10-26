package cm.aptoide.pt.account;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.analytics.events.AptoideEvent;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import cm.aptoide.pt.analytics.events.FlurryEvent;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by trinkes on 22/05/2017.
 */
public class AccountAnalytics {
  public static final String APTOIDE_EVENT_NAME = "LOGIN";
  public static final String ACTION = "CLICK";
  public static final String CONTEXT = "LOGIN";
  public static final String STORE = "store";
  private static final String STATUS = "Status";
  private static final String FACEBOOK_LOGIN_EVENT_NAME = "Account_Login_Screen";
  private static final String FLURRY_LOGIN_EVENT_NAME = "Account_Login_Screen";
  private static final String FACEBOOK_SIGNUP_EVENT_NAME = "Account_Signup_Screen";
  private static final String FLURRY_SIGNUP_EVENT_NAME = "Account_Signup_Screen";
  private static final String LOGIN_METHOD = "Method";
  private static final String PREVIOUS_CONTEXT = "previous_context";
  private static final String STATUS_DETAIL = "Status Detail";
  private final Analytics analytics;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final String appId;
  private final SharedPreferences sharedPreferences;
  private final AppEventsLogger facebook;
  private final NavigationTracker navigationTracker;
  private AptoideEvent aptoideSuccessLoginEvent;
  private FacebookEvent facebookSuccessLoginEvent;
  private FlurryEvent flurrySuccessLoginEvent;
  private FacebookEvent signUpFacebookEvent;
  private FlurryEvent signUpFlurryEvent;

  public AccountAnalytics(Analytics analytics, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, String appId, SharedPreferences sharedPreferences,
      AppEventsLogger facebook, NavigationTracker navigationTracker) {
    this.analytics = analytics;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.appId = appId;
    this.sharedPreferences = sharedPreferences;
    this.facebook = facebook;
    this.navigationTracker = navigationTracker;
  }

  public void loginSuccess() {
    if (aptoideSuccessLoginEvent != null) {
      analytics.sendEvent(aptoideSuccessLoginEvent);
      aptoideSuccessLoginEvent = null;
    }
    if (facebookSuccessLoginEvent != null) {
      analytics.sendEvent(facebookSuccessLoginEvent);
      facebookSuccessLoginEvent = null;
    }
    if (flurrySuccessLoginEvent != null) {
      analytics.sendEvent(flurrySuccessLoginEvent);
      flurrySuccessLoginEvent = null;
    }
    if (signUpFacebookEvent != null) {
      analytics.sendEvent(signUpFacebookEvent);
      signUpFacebookEvent = null;
    }
    if (signUpFlurryEvent != null) {
      analytics.sendEvent(signUpFlurryEvent);
      signUpFacebookEvent = null;
    }
  }

  public void sendAptoideLoginButtonPressed() {
    clearSignUpEvents();
    setupLoginEvents(LoginMethod.APTOIDE);
  }

  public void sendGoogleLoginButtonPressed() {
    clearSignUpEvents();
    setupLoginEvents(LoginMethod.GOOGLE);
  }

  public void sendFacebookLoginButtonPressed() {
    clearSignUpEvents();
    setupLoginEvents(LoginMethod.FACEBOOK);
  }

  public void sendAptoideSignUpButtonPressed() {
    Bundle bundle = new Bundle();
    bundle.putString(STATUS, SignUpLoginStatus.SUCCESS.getStatus());
    signUpFacebookEvent = new FacebookEvent(facebook, FACEBOOK_SIGNUP_EVENT_NAME, bundle);
    signUpFlurryEvent = new FlurryEvent(FLURRY_SIGNUP_EVENT_NAME);
    clearLoginEvents();
  }

  private void clearSignUpEvents() {
    signUpFacebookEvent = null;
    signUpFlurryEvent = null;
  }

  private void clearLoginEvents() {
    aptoideSuccessLoginEvent = null;
    facebookSuccessLoginEvent = null;
    flurrySuccessLoginEvent = null;
  }

  public void sendAptoideLoginFailEvent() {
    sendLoginFailEvents(LoginMethod.APTOIDE, SignUpLoginStatus.FAILED,
        LoginStatusDetail.GENERAL_ERROR);
  }

  public void sendGoogleSignUpFailEvent() {
    sendLoginFailEvents(LoginMethod.GOOGLE, SignUpLoginStatus.FAILED, LoginStatusDetail.SDK_ERROR);
  }

  public void sendAptoideSignUpFailEvent() {
    analytics.sendEvent(new FlurryEvent(FLURRY_SIGNUP_EVENT_NAME));
    Bundle bundle = new Bundle();
    bundle.putString(STATUS, SignUpLoginStatus.FAILED.getStatus());
    analytics.sendEvent(new FacebookEvent(facebook, FACEBOOK_SIGNUP_EVENT_NAME, bundle));
  }

  public void sendFacebookMissingPermissionsEvent() {
    sendLoginFailEvents(LoginMethod.FACEBOOK, SignUpLoginStatus.FAILED,
        LoginStatusDetail.PERMISSIONS_DENIED);
  }

  public void sendFacebookUserCancelledEvent() {
    sendLoginFailEvents(LoginMethod.FACEBOOK, SignUpLoginStatus.FAILED, LoginStatusDetail.CANCEL);
  }

  public void sendFacebookErrorEvent() {
    sendLoginFailEvents(LoginMethod.FACEBOOK, SignUpLoginStatus.FAILED,
        LoginStatusDetail.SDK_ERROR);
  }

  private void setupLoginEvents(LoginMethod aptoide) {
    aptoideSuccessLoginEvent = createAptoideLoginEvent();
    facebookSuccessLoginEvent =
        createFacebookEvent(aptoide, SignUpLoginStatus.SUCCESS, LoginStatusDetail.SUCCESS);
    flurrySuccessLoginEvent =
        createFlurryEvent(aptoide, SignUpLoginStatus.SUCCESS, LoginStatusDetail.SUCCESS);
  }

  private FacebookEvent createFacebookEvent(LoginMethod loginMethod, SignUpLoginStatus loginStatus,
      LoginStatusDetail loginStatusDetail) {
    Bundle bundle = new Bundle();
    bundle.putString(LOGIN_METHOD, loginMethod.getMethod());
    bundle.putString(STATUS, loginStatus.getStatus());
    bundle.putString(STATUS_DETAIL, loginStatusDetail.getLoginStatusDetail());
    return new FacebookEvent(facebook, FACEBOOK_LOGIN_EVENT_NAME, bundle);
  }

  private void sendLoginFailEvents(LoginMethod loginMethod, SignUpLoginStatus loginStatus,
      LoginStatusDetail statusDetail) {
    analytics.sendEvent(createFlurryEvent(loginMethod, loginStatus, statusDetail));
    analytics.sendEvent(createFacebookEvent(loginMethod, loginStatus, statusDetail));
  }

  private FlurryEvent createFlurryEvent(LoginMethod loginMethod, SignUpLoginStatus loginStatus,
      LoginStatusDetail loginStatusDetail) {
    Map<String, String> map = new HashMap<>();
    map.put(LOGIN_METHOD, loginMethod.getMethod());
    map.put(STATUS, loginStatus.getStatus());
    map.put(STATUS_DETAIL, loginStatusDetail.getLoginStatusDetail());
    return new FlurryEvent(FLURRY_LOGIN_EVENT_NAME, map);
  }

  @NonNull private AptoideEvent createAptoideLoginEvent() {
    Map<String, Object> map = new HashMap<>();
    map.put(PREVIOUS_CONTEXT, navigationTracker.getPreviousViewName());
    ScreenTagHistory previousScreen = navigationTracker.getPreviousScreen();
    if (previousScreen != null) {
      map.put(STORE, previousScreen.getStore());
    }
    map.put(PREVIOUS_CONTEXT, navigationTracker.getPreviousViewName());
    return new AptoideEvent(map, APTOIDE_EVENT_NAME, ACTION, CONTEXT, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, appId, sharedPreferences);
  }

  private enum LoginMethod {
    APTOIDE("Aptoide"), FACEBOOK("FB"), GOOGLE("Google");

    private final String method;

    LoginMethod(String method) {
      this.method = method;
    }

    public String getMethod() {
      return method;
    }
  }

  private enum SignUpLoginStatus {
    SUCCESS("Success"), FAILED("Failed");

    private final String status;

    SignUpLoginStatus(String result) {
      this.status = result;
    }

    public String getStatus() {
      return status;
    }
  }

  private enum LoginStatusDetail {
    PERMISSIONS_DENIED("Permissions Denied"), SDK_ERROR("SDK Error"), CANCEL(
        "User canceled"), GENERAL_ERROR("General Error"), SUCCESS("Success");

    private final String loginStatusDetail;

    LoginStatusDetail(String statusDetail) {
      this.loginStatusDetail = statusDetail;
    }

    public String getLoginStatusDetail() {
      return loginStatusDetail;
    }
  }
}
