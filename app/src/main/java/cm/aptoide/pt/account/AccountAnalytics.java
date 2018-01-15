package cm.aptoide.pt.account;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AccountException;
import cm.aptoide.accountmanager.AccountValidationException;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.analytics.events.AptoideEvent;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import cm.aptoide.pt.analytics.events.FlurryEvent;
import cm.aptoide.pt.crashreports.CrashReport;
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
  public static final String UNKNOWN_STATUS_CODE = "12501";
  private static final String STATUS = "Status";
  private static final String LOGIN_EVENT_NAME = "Account_Login_Screen";
  private static final String SIGN_UP_EVENT_NAME = "Account_Signup_Screen";
  private static final String LOGIN_METHOD = "Method";
  private static final String PREVIOUS_CONTEXT = "previous_context";
  private static final String STATUS_DETAIL = "Status Detail";
  private static final String STATUS_DESCRIPTION = "Status Description";
  private static final String STATUS_CODE = "Status Code";
  private final Analytics analytics;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final String aptoideAppId;
  private final SharedPreferences sharedPreferences;
  private final AppEventsLogger facebook;
  private final NavigationTracker navigationTracker;
  private final CrashReport crashReport;
  private AptoideEvent aptoideSuccessLoginEvent;
  private FacebookEvent facebookSuccessLoginEvent;
  private FlurryEvent flurrySuccessLoginEvent;
  private FacebookEvent signUpFacebookEvent;
  private FlurryEvent signUpFlurryEvent;

  public AccountAnalytics(Analytics analytics, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, String aptoideAppId, SharedPreferences sharedPreferences,
      AppEventsLogger facebook, NavigationTracker navigationTracker, CrashReport crashReport) {
    this.analytics = analytics;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.aptoideAppId = aptoideAppId;
    this.sharedPreferences = sharedPreferences;
    this.facebook = facebook;
    this.navigationTracker = navigationTracker;
    this.crashReport = crashReport;
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
    signUpFacebookEvent = new FacebookEvent(facebook, SIGN_UP_EVENT_NAME, bundle);
    signUpFlurryEvent = new FlurryEvent(SIGN_UP_EVENT_NAME);
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

  private void sendGoogleLoginFailEvent(Throwable exception) {
    if (exception instanceof GoogleSignUpException) {
      GoogleSignUpException googleSignUpException = (GoogleSignUpException) exception;
      if (googleSignUpException.getStatusCode() == 12501) {
        sendEvents(LOGIN_EVENT_NAME, LoginMethod.GOOGLE, SignUpLoginStatus.INVALID, SDK_ERROR,
            LoginMethod.GOOGLE.toString(), googleSignUpException.getError());
      } else {
        sendEvents(LOGIN_EVENT_NAME, LoginMethod.GOOGLE, SignUpLoginStatus.FAILED, SDK_ERROR,
            LoginMethod.GOOGLE.toString(), googleSignUpException.getError());
      }
    } else {
      sendWebserviceErrors(LOGIN_EVENT_NAME, LoginMethod.GOOGLE, exception);
    }
  }

  private void setupLoginEvents(LoginMethod aptoide) {
    aptoideSuccessLoginEvent = createAptoideLoginEvent();
    facebookSuccessLoginEvent =
        createFacebookEvent(LOGIN_EVENT_NAME, aptoide, SignUpLoginStatus.SUCCESS, SUCCESS, null,
            null);
    flurrySuccessLoginEvent =
        createFlurryEvent(LOGIN_EVENT_NAME, aptoide, SignUpLoginStatus.SUCCESS, SUCCESS, null,
            null);
  }

  private FacebookEvent createFacebookEvent(String eventName, LoginMethod loginMethod,
      SignUpLoginStatus loginStatus, String statusDetail, String statusCode,
      String statusDescription) {
    Bundle bundle = new Bundle();
    bundle.putString(LOGIN_METHOD, loginMethod.getMethod());
    bundle.putString(STATUS, loginStatus.getStatus());
    bundle.putString(STATUS_DETAIL, statusDetail);
    bundle.putString(STATUS_CODE, statusCode);
    if (statusDescription != null) {
      bundle.putString(STATUS_DESCRIPTION, statusDescription);
    }
    return new FacebookEvent(facebook, eventName, bundle);
  }

  private void sendEvents(String eventName, LoginMethod loginMethod, SignUpLoginStatus loginStatus,
      String statusDetail, String statusCode, String statusDescription) {
    analytics.sendEvent(
        createFlurryEvent(eventName, loginMethod, loginStatus, statusDetail, statusCode,
            statusDescription));
    analytics.sendEvent(
        createFacebookEvent(eventName, loginMethod, loginStatus, statusDetail, statusCode,
            statusDescription));
  }

  private FlurryEvent createFlurryEvent(String eventName, LoginMethod loginMethod,
      SignUpLoginStatus loginStatus, String statusDetail, String statusCode,
      @Nullable String statusDescription) {
    Map<String, String> map = new HashMap<>();
    map.put(LOGIN_METHOD, loginMethod.getMethod());
    map.put(STATUS, loginStatus.getStatus());
    map.put(STATUS_DETAIL, statusDetail);
    map.put(STATUS_CODE, statusCode);
    if (statusDescription != null) {
      map.put(STATUS_DESCRIPTION, statusDescription);
    }
    return new FlurryEvent(eventName, map);
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

  private void sendFacebookLoginErrorEvent(Throwable throwable) {
    if (throwable instanceof FacebookSignUpException) {
      FacebookSignUpException facebookSignUpException = ((FacebookSignUpException) throwable);
      switch (facebookSignUpException.getCode()) {
        case FacebookSignUpException.MISSING_REQUIRED_PERMISSIONS:
          sendEvents(LOGIN_EVENT_NAME, LoginMethod.FACEBOOK, SignUpLoginStatus.INVALID,
              PERMISSIONS_DENIED, String.valueOf(facebookSignUpException.getCode()),
              facebookSignUpException.getFacebookMessage());
          break;
        case FacebookSignUpException.USER_CANCELLED:
          sendEvents(LOGIN_EVENT_NAME, LoginMethod.FACEBOOK, SignUpLoginStatus.INVALID,
              USER_CANCELED, String.valueOf(facebookSignUpException.getCode()),
              facebookSignUpException.getFacebookMessage());
          break;
        case FacebookSignUpException.ERROR:
          sendEvents(LOGIN_EVENT_NAME, LoginMethod.FACEBOOK, SignUpLoginStatus.FAILED, SDK_ERROR,
              String.valueOf(facebookSignUpException.getCode()),
              facebookSignUpException.getFacebookMessage());
          break;
      }
    } else {
      sendWebserviceErrors(LOGIN_EVENT_NAME, LoginMethod.FACEBOOK, throwable);
    }
  }

  private void sendWebserviceErrors(String eventName, LoginMethod loginMethod,
      Throwable throwable) {
    if (throwable instanceof AptoideWsV7Exception) {
      sendV7ExceptionEvent(loginMethod, ((AptoideWsV7Exception) throwable));
    } else if (throwable instanceof AptoideWsV3Exception) {
      sendV3ExceptionEvent(loginMethod, ((AptoideWsV3Exception) throwable));
    } else if (throwable instanceof AccountException) {
      sendV3ExceptionEvent(loginMethod, ((AccountException) throwable));
    } else if (throwable instanceof AccountValidationException) {
      sendEvents(eventName, loginMethod, SignUpLoginStatus.INVALID, GENERAL_ERROR, "no_code",
          throwable.toString());
    } else {
      sendEvents(eventName, loginMethod, SignUpLoginStatus.FAILED, GENERAL_ERROR, "no_code",
          throwable.toString());
      crashReport.log(throwable);
    }
  }

  private void sendV3ExceptionEvent(LoginMethod loginMethod, AccountException exception) {
    String error = getWsError(exception);
    String errorDescription = exception.getErrors()
        .get(error);
    sendEvents(LOGIN_EVENT_NAME, loginMethod, SignUpLoginStatus.FAILED, WEB_ERROR, error,
        errorDescription);
  }

  private String getWsError(AccountException exception) {
    return exception.getErrors()
        .keySet()
        .toString()
        .replace("[", "")
        .replace("]", "");
  }

  private void sendV7ExceptionEvent(LoginMethod loginMethod, AptoideWsV7Exception exception) {
    sendEvents(LOGIN_EVENT_NAME, loginMethod, SignUpLoginStatus.FAILED, WEB_ERROR,
        exception.getBaseResponse()
            .getErrors()
            .get(0)
            .getCode(), exception.getBaseResponse()
            .getErrors()
            .get(0)
            .getDescription());
  }

  private void sendV3ExceptionEvent(LoginMethod loginMethod, AptoideWsV3Exception exception) {
    sendEvents(LOGIN_EVENT_NAME, loginMethod, SignUpLoginStatus.FAILED, WEB_ERROR,
        exception.getBaseResponse()
            .getErrors()
            .get(0).code, exception.getBaseResponse()
            .getErrors()
            .get(0).msg);
  }

  public void sendLoginErrorEvent(LoginMethod loginMethod, Throwable throwable) {
    switch (loginMethod) {
      case APTOIDE:
        sendWebserviceErrors(LOGIN_EVENT_NAME, LoginMethod.APTOIDE, throwable);
        break;
      case FACEBOOK:
        sendFacebookLoginErrorEvent(throwable);
        break;
      case GOOGLE:
        sendGoogleLoginFailEvent(throwable);
        break;
    }
  }

  public void sendSignUpErrorEvent(LoginMethod loginMethod, Throwable throwable) {
    if (loginMethod.equals(LoginMethod.APTOIDE)) {
      sendAptoideSignUpErrorEvent(throwable);
    } else {
      throw new IllegalStateException("unknown sign up method: " + loginMethod.name());
    }
  }

  private void sendAptoideSignUpErrorEvent(Throwable throwable) {
    if (throwable instanceof AccountException) {
      sendEvents(SIGN_UP_EVENT_NAME, LoginMethod.APTOIDE, SignUpLoginStatus.FAILED, WEB_ERROR,
          ((AccountException) throwable).getErrors()
              .keySet()
              .toString(), ((AccountException) throwable).getErrors()
              .values()
              .toString());
    } else {
      sendWebserviceErrors(SIGN_UP_EVENT_NAME, LoginMethod.APTOIDE, throwable);
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
    SUCCESS("Success"), FAILED("Failed"), INVALID("Invalid");

    private final String status;

    SignUpLoginStatus(String result) {
      this.status = result;
    }

    public String getStatus() {
      return status;
    }
  }
}
