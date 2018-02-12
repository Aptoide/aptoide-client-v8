package cm.aptoide.pt.account;

import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AccountException;
import cm.aptoide.accountmanager.AccountValidationException;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV3Exception;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trinkes on 22/05/2017.
 */
public class AccountAnalytics {
  public static final String APTOIDE_EVENT_NAME = "LOGIN";
  public static final String STORE_ACTION = "Click";
  public static final String STORE = "store";
  public static final String PERMISSIONS_DENIED = "Permissions Denied";
  public static final String SDK_ERROR = "SDK Error";
  public static final String USER_CANCELED = "User canceled";
  public static final String GENERAL_ERROR = "General Error";
  public static final String SUCCESS = "Success";
  public static final String WEB_ERROR = "Web";
  public static final int UNKNOWN_STATUS_CODE = 12501;
  public static final String LOGIN_SIGN_UP_START_SCREEN = "Account_Login_Signup_Start_Screen";
  public static final String CREATE_USER_PROFILE = "Account_Create_A_User_Profile_Screen";
  public static final String PROFILE_SETTINGS = "Account_Profile_Settings_Screen";
  public static final String HAS_PICTURE = "has_picture";
  public static final String SCREEN = "Screen";
  public static final String ENTRY = "Account_Entry";
  public static final String SOURCE = "Source";
  public static final String LOGIN_EVENT_NAME = "Account_Login_Screen";
  public static final String SIGN_UP_EVENT_NAME = "Account_Signup_Screen";
  public static final String CREATE_YOUR_STORE = "Account_Create_Your_Store_Screen";
  private static final String INVALID_GRANT_CODE = "invalid_grant";
  private static final String STATUS = "Status";
  private static final String LOGIN_METHOD = "Method";
  private static final String PREVIOUS_CONTEXT = "previous_context";
  private static final String STATUS_DETAIL = "Status Detail";
  private static final String STATUS_DESCRIPTION = "Status Description";
  private static final String STATUS_CODE = "Status Code";
  private final NavigationTracker navigationTracker;
  private final CrashReport crashReport;
  private final AnalyticsManager analyticsManager;
  private AccountEvent aptoideSuccessLoginEvent;
  private AccountEvent facebookAndFlurrySuccessLoginEvent;
  private AccountEvent signUpEvent;

  public AccountAnalytics(NavigationTracker navigationTracker, CrashReport crashReport,
      AnalyticsManager analyticsManager) {
    this.navigationTracker = navigationTracker;
    this.crashReport = crashReport;
    this.analyticsManager = analyticsManager;
  }

  public void loginSuccess() {
    if (aptoideSuccessLoginEvent != null) {
      analyticsManager.logEvent(aptoideSuccessLoginEvent.getMap(),
          aptoideSuccessLoginEvent.getEventName(), aptoideSuccessLoginEvent.getAction(),
          aptoideSuccessLoginEvent.getContext());
      aptoideSuccessLoginEvent = null;
    }
    if (facebookAndFlurrySuccessLoginEvent != null) {
      analyticsManager.logEvent(facebookAndFlurrySuccessLoginEvent.getMap(),
          facebookAndFlurrySuccessLoginEvent.getEventName(),
          facebookAndFlurrySuccessLoginEvent.getAction(),
          facebookAndFlurrySuccessLoginEvent.getContext());
      facebookAndFlurrySuccessLoginEvent = null;
    }
    if (signUpEvent != null) {
      analyticsManager.logEvent(signUpEvent.getMap(), signUpEvent.getEventName(),
          signUpEvent.getAction(), signUpEvent.getContext());
      signUpEvent = null;
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
    Map<String, Object> map = new HashMap<>();
    map.put(STATUS, SignUpLoginStatus.SUCCESS.getStatus());
    signUpEvent =
        new AccountEvent(map, SIGN_UP_EVENT_NAME, AnalyticsManager.Action.CLICK, getViewName(true));
    clearLoginEvents();
  }

  private void clearSignUpEvents() {
    signUpEvent = null;
  }

  private void clearLoginEvents() {
    aptoideSuccessLoginEvent = null;
    facebookAndFlurrySuccessLoginEvent = null;
  }

  private void sendGoogleLoginFailEvent(Throwable exception) {
    if (exception instanceof GoogleSignUpException) {
      GoogleSignUpException googleSignUpException = (GoogleSignUpException) exception;
      if (googleSignUpException.getStatusCode() == UNKNOWN_STATUS_CODE) {
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
    facebookAndFlurrySuccessLoginEvent =
        createFacebookAndFlurryEvent(LOGIN_EVENT_NAME, aptoide, SignUpLoginStatus.SUCCESS, SUCCESS,
            null, null);
  }

  private AccountEvent createFacebookAndFlurryEvent(String eventName, LoginMethod loginMethod,
      SignUpLoginStatus loginStatus, String statusDetail, String statusCode,
      String statusDescription) {
    Map<String, Object> map = new HashMap<>();
    map.put(LOGIN_METHOD, loginMethod.getMethod());
    map.put(STATUS, loginStatus.getStatus());
    map.put(STATUS_DETAIL, statusDetail);
    if (statusCode != null) {
      map.put(STATUS_CODE, statusCode);
    }
    if (statusDescription != null) {
      map.put(STATUS_DESCRIPTION, statusDescription);
    }
    return new AccountEvent(map, eventName, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private void sendEvents(String eventName, LoginMethod loginMethod, SignUpLoginStatus loginStatus,
      String statusDetail, String statusCode, String statusDescription) {
    AccountEvent event =
        createFacebookAndFlurryEvent(eventName, loginMethod, loginStatus, statusDetail, statusCode,
            statusDescription);
    analyticsManager.logEvent(event.getMap(), event.getEventName(), event.getAction(),
        event.getContext());
  }

  @NonNull private AccountEvent createAptoideLoginEvent() {
    Map<String, Object> map = new HashMap<>();
    map.put(PREVIOUS_CONTEXT, navigationTracker.getPreviousViewName());
    ScreenTagHistory previousScreen = navigationTracker.getPreviousScreen();
    if (previousScreen != null) {
      map.put(STORE, previousScreen.getStore());
    }
    map.put(PREVIOUS_CONTEXT, navigationTracker.getPreviousViewName());
    AccountEvent aptoideEvent =
        new AccountEvent(map, APTOIDE_EVENT_NAME, AnalyticsManager.Action.CLICK, getViewName(true));
    return aptoideEvent;
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
    if (error.equals(INVALID_GRANT_CODE)) {
      sendEvents(LOGIN_EVENT_NAME, loginMethod, SignUpLoginStatus.INVALID, WEB_ERROR, error,
          errorDescription);
    } else {
      sendEvents(LOGIN_EVENT_NAME, loginMethod, SignUpLoginStatus.FAILED, WEB_ERROR, error,
          errorDescription);
    }
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

  public void clickIn(StartupClick connectGoogle, StartupClickOrigin startupClickOrigin) {
    Map<String, Object> map = new HashMap<>();
    map.put("Action", connectGoogle.getClickEvent());
    map.put(SCREEN, startupClickOrigin.getClickOrigin());
    analyticsManager.logEvent(map, LOGIN_SIGN_UP_START_SCREEN, AnalyticsManager.Action.CLICK,
        getViewName(true));
  }

  public void createdUserProfile(boolean hasPicture) {
    Map<String, Object> map = new HashMap<>();
    map.put(HAS_PICTURE, hasPicture ? "True" : "False");
    analyticsManager.logEvent(map, CREATE_USER_PROFILE, AnalyticsManager.Action.CLICK,
        getViewName(true));
  }

  public void accountProfileAction(int screen, ProfileAction action) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("Action", action.getAction());
    map.put("screen", Integer.toString(screen));
    analyticsManager.logEvent(map, PROFILE_SETTINGS, AnalyticsManager.Action.CLICK,
        getViewName(true));
  }

  public void createStore(boolean hasPicture, CreateStoreAction action) {
    HashMap<String, Object> map = new HashMap<>();
    map.put(STORE_ACTION, action);
    map.put(HAS_PICTURE, hasPicture ? "True" : "False");
    analyticsManager.logEvent(map, CREATE_YOUR_STORE, AnalyticsManager.Action.CLICK,
        getViewName(true));
  }

  public void enterAccountScreen(AccountOrigins sourceValue) {
    Map<String, Object> map = new HashMap<>();
    map.put(SOURCE, sourceValue.getOrigin());
    analyticsManager.logEvent(map, ENTRY, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
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

  public enum StartupClick {
    JOIN_APTOIDE("Join Aptoide"), LOGIN("Login"), CONNECT_FACEBOOK(
        "Connect with FB"), CONNECT_GOOGLE("Connect with Google");

    private final String clickEvent;

    StartupClick(String clickEvent) {
      this.clickEvent = clickEvent;
    }

    public String getClickEvent() {
      return clickEvent;
    }
  }

  public enum StartupClickOrigin {
    MAIN("Main"), JOIN_UP("Join Aptoide Slide Up"), LOGIN_UP(
        "Login Slide Up"), NOT_LOGGED_IN_DIALOG("Not logged in Dialog");

    private String clickOrigin;

    StartupClickOrigin(String clickOrigin) {
      this.clickOrigin = clickOrigin;
    }

    public String getClickOrigin() {
      return clickOrigin;
    }
  }

  public enum ProfileAction {
    MORE_INFO("More info"), CONTINUE("Continue"), PRIVATE_PROFILE(
        "Make my profile private"), PUBLIC_PROFILE("Make my profile public");

    private final String action;

    ProfileAction(String action) {
      this.action = action;
    }

    public String getAction() {
      return action;
    }
  }

  public enum CreateStoreAction {
    SKIP("Skip"), CREATE("Create store");

    private final String action;

    CreateStoreAction(String action) {
      this.action = action;
    }

    public String getAction() {
      return action;
    }
  }

  public enum AccountOrigins {
    WIZARD("Wizard"), MY_ACCOUNT("My Account"), TIMELINE("Timeline"), STORE("Store"), APP_VIEW_FLAG(
        "App View Flag"), APP_VIEW_SHARE("App View Share on Timeline"), SHARE_CARD(
        "Share Card"), LIKE_CARD("Like Card"), COMMENT_LIST("Comment List"), RATE_DIALOG(
        "Reviews FAB"), REPLY_REVIEW("Reply Review"), REVIEW_FEEDBACK(
        "Review Feedback"), SOCIAL_LIKE("Like Social Card"), STORE_COMMENT(
        "Store Comment"), LATEST_COMMENTS_STORE(
        "Comment on Latest Store Comments"), POST_ON_TIMELINE("Post on Timeline");

    private final String origin;

    AccountOrigins(String origin) {
      this.origin = origin;
    }

    public String getOrigin() {
      return origin;
    }
  }

  private class AccountEvent {
    private Map<String, Object> map;
    private String eventName;
    private AnalyticsManager.Action action;
    private String context;

    public AccountEvent(Map<String, Object> map, String eventName, AnalyticsManager.Action action,
        String context) {
      this.map = map;
      this.eventName = eventName;
      this.action = action;
      this.context = context;
    }

    public Map<String, Object> getMap() {
      return map;
    }

    public String getEventName() {
      return eventName;
    }

    public AnalyticsManager.Action getAction() {
      return action;
    }

    public String getContext() {
      return context;
    }
  }
}
