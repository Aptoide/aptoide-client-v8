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
import cm.aptoide.pt.dataprovider.exception.AptoideWsV3Exception;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
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
  public static final String PERMISSIONS_DENIED = "Permissions Denied";
  public static final String SDK_ERROR = "SDK Error";
  public static final String USER_CANCELED = "User canceled";
  public static final String GENERAL_ERROR = "General Error";
  public static final String SUCCESS = "Success";
  public static final String WEB_ERROR = "Web";
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
  private final String aptoideAppId;
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
      TokenInvalidator tokenInvalidator, String aptoideAppId, SharedPreferences sharedPreferences,
      AppEventsLogger facebook, NavigationTracker navigationTracker) {
    this.analytics = analytics;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.aptoideAppId = aptoideAppId;
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
    // TODO: 21/11/2017 trinkes
    sendLoginFailEvents(LoginMethod.APTOIDE, SignUpLoginStatus.FAILED, GENERAL_ERROR,
        GENERAL_ERROR);
  }

  private void sendGoogleSignUpFailEvent(Throwable exception) {
    if (exception instanceof GoogleSignUpException) {
      sendLoginFailEvents(LoginMethod.GOOGLE, SignUpLoginStatus.FAILED, SDK_ERROR,
          ((GoogleSignUpException) exception).getError());
    } else {
      sendWebserviceErrors(LoginMethod.GOOGLE, exception);
    }
  }

  public void sendAptoideSignUpFailEvent() {
    // TODO: 21/11/2017 trinkes
    analytics.sendEvent(new FlurryEvent(FLURRY_SIGNUP_EVENT_NAME));
    Bundle bundle = new Bundle();
    bundle.putString(STATUS, SignUpLoginStatus.FAILED.getStatus());
    analytics.sendEvent(new FacebookEvent(facebook, FACEBOOK_SIGNUP_EVENT_NAME, bundle));
  }

  private void setupLoginEvents(LoginMethod aptoide) {
    aptoideSuccessLoginEvent = createAptoideLoginEvent();
    facebookSuccessLoginEvent = createFacebookEvent(aptoide, SignUpLoginStatus.SUCCESS, SUCCESS);
    flurrySuccessLoginEvent = createFlurryEvent(aptoide, SignUpLoginStatus.SUCCESS, SUCCESS);
  }

  private FacebookEvent createFacebookEvent(LoginMethod loginMethod, SignUpLoginStatus loginStatus,
      String loginStatusDetail) {
    Bundle bundle = new Bundle();
    bundle.putString(LOGIN_METHOD, loginMethod.getMethod());
    bundle.putString(STATUS, loginStatus.getStatus());
    bundle.putString(STATUS_DETAIL, loginStatusDetail);
    return new FacebookEvent(facebook, FACEBOOK_LOGIN_EVENT_NAME, bundle);
  }

  private void sendLoginFailEvents(LoginMethod loginMethod, SignUpLoginStatus loginStatus,
      String statusDetail, String loginStatusDetail) {
    analytics.sendEvent(createFlurryEvent(loginMethod, loginStatus, statusDetail));
    analytics.sendEvent(createFacebookEvent(loginMethod, loginStatus, loginStatusDetail));
  }

  private FlurryEvent createFlurryEvent(LoginMethod loginMethod, SignUpLoginStatus loginStatus,
      String loginStatusDetail) {
    Map<String, String> map = new HashMap<>();
    map.put(LOGIN_METHOD, loginMethod.getMethod());
    map.put(STATUS, loginStatus.getStatus());
    map.put(STATUS_DETAIL, loginStatusDetail);
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
        converterFactory, tokenInvalidator, aptoideAppId, sharedPreferences);
  }

  private void sendFacebookSignUpErrorEvent(Throwable throwable) {
    if (throwable instanceof FacebookSignUpException) {
      FacebookSignUpException facebookSignUpException = ((FacebookSignUpException) throwable);
      switch (facebookSignUpException.getCode()) {
        case FacebookSignUpException.MISSING_REQUIRED_PERMISSIONS:
          sendLoginFailEvents(LoginMethod.FACEBOOK, SignUpLoginStatus.FAILED, PERMISSIONS_DENIED,
              facebookSignUpException.getFacebookMessage());
          break;
        case FacebookSignUpException.USER_CANCELLED:
          sendLoginFailEvents(LoginMethod.FACEBOOK, SignUpLoginStatus.FAILED, USER_CANCELED,
              facebookSignUpException.getFacebookMessage());
          break;
        case FacebookSignUpException.ERROR:
          sendLoginFailEvents(LoginMethod.FACEBOOK, SignUpLoginStatus.FAILED, SDK_ERROR,
              facebookSignUpException.getFacebookMessage());
          break;
      }
    } else {
      sendWebserviceErrors(LoginMethod.FACEBOOK, throwable);
    }
  }

  private void sendWebserviceErrors(LoginMethod loginMethod, Throwable throwable) {
    if (throwable instanceof AptoideWsV7Exception) {
      sendV7ExceptionEvent(loginMethod, ((AptoideWsV7Exception) throwable));
    } else if (throwable instanceof AptoideWsV3Exception) {
      sendV3ExceptionEvent(loginMethod, ((AptoideWsV3Exception) throwable));
    } else {
      sendLoginFailEvents(loginMethod, SignUpLoginStatus.FAILED, GENERAL_ERROR,
          throwable.toString());
    }
  }

  private void sendV7ExceptionEvent(LoginMethod loginMethod, AptoideWsV7Exception exception) {
    sendLoginFailEvents(loginMethod, SignUpLoginStatus.FAILED, WEB_ERROR,
        exception.getBaseResponse()
            .getErrors()
            .toString());
  }

  private void sendV3ExceptionEvent(LoginMethod loginMethod, AptoideWsV3Exception exception) {
    sendLoginFailEvents(loginMethod, SignUpLoginStatus.FAILED, WEB_ERROR,
        exception.getBaseResponse()
            .getErrors()
            .toString());
  }

  public void sendSignUpErrorEvent(LoginMethod loginMethod, Throwable throwable) {
    switch (loginMethod) {
      case APTOIDE:
        sendWebserviceErrors(LoginMethod.APTOIDE, throwable);
        break;
      case FACEBOOK:
        sendFacebookSignUpErrorEvent(throwable);
        break;
      case GOOGLE:
        sendGoogleSignUpFailEvent(throwable);
        break;
    }
  }

  public enum LoginMethod {
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
}
