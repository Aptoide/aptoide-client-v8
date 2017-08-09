package cm.aptoide.pt.v8engine.analytics;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BiUtmAnalyticsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BiUtmAnalyticsRequestBody;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.FirstLaunchAnalytics;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.events.FabricEvent;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import cm.aptoide.pt.v8engine.analytics.events.FlurryEvent;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

import static cm.aptoide.pt.v8engine.analytics.Analytics.Lifecycle.Application.facebookLogger;

/**
 * Created by neuro on 07-05-2015.f
 * v8 integration by jdandrade on 25-07-2016
 */
public class Analytics {

  // Constantes globais a todos os eventos.
  public static final String ACTION = "Action";
  private static final String METHOD = "Method";
  private static final String TAG = Analytics.class.getSimpleName();
  private static final boolean ACTIVATE_FLURRY = true;
  private static final int ALL = Integer.MAX_VALUE;
  private static final int FLURRY = 1 << 1;
  private static final int FABRIC = 1 << 2;
  private static final String[] unwantedValuesList = {
      "ads-highlighted", "apps-group-trending", "apps-group-local-top-apps",
      "timeline-your-friends-installs", "apps-group-latest-applications",
      "apps-group-top-apps-in-this-store", "apps-group-aptoide-publishers",
      "stores-group-top-stores", "stores-group-featured-stores", "reviews-group-reviews",
      "apps-group-top-games", "apps-group-top-stores", "apps-group-featured-stores",
      "apps-group-editors-choice"
  };

  private static Analytics instance;

  private final AnalyticsDataSaver saver;

  private Analytics(AnalyticsDataSaver saver) {
    this.saver = saver;
  }

  public static Analytics getInstance() {
    if (instance == null) {
      instance = new Analytics(new AnalyticsDataSaver());
    }
    return instance;
  }

  private static void track(String event, String key, String attr, int flags) {

    try {
      if (!ACTIVATE_FLURRY) {
        return;
      }

      HashMap stringObjectHashMap = new HashMap<>();

      stringObjectHashMap.put(key, attr);

      track(event, stringObjectHashMap, flags);

      Logger.d(TAG, "Event: " + event + ", Key: " + key + ", attr: " + attr);
    } catch (Exception e) {
      Logger.d(TAG, e.getStackTrace()
          .toString());
    }
  }

  private static void track(String event, HashMap map, int flags) {
    try {
      if (checkAcceptability(flags, FLURRY)) {
        FlurryAgent.logEvent(event, map);
        Logger.d(TAG, "Flurry Event: " + event + ", Map: " + map);
      }
    } catch (Exception e) {
      Logger.d(TAG, e.getStackTrace()
          .toString());
    }
  }

  /**
   * Verifica se as flags fornecidas constam em accepted.
   *
   * @param flag flags fornecidas
   * @param accepted flags aceitáveis
   *
   * @return true caso as flags fornecidas constem em accepted.
   */
  private static boolean checkAcceptability(int flag, int accepted) {
    if (accepted == FLURRY && !ACTIVATE_FLURRY) {
      Logger.d(TAG, "Flurry Disabled");
      return false;
    } else {
      return (flag & accepted) == accepted;
    }
  }

  private static void logFacebookEvents(String eventName) {
    if (BuildConfig.BUILD_TYPE.equals("debug")) {
      return;
    }
    facebookLogger.logEvent(eventName);
  }

  private static void logFacebookEvents(String eventName, String key, String value) {
    Bundle bundle = new Bundle();
    bundle.putString(key, value);
    facebookLogger.logEvent(eventName, bundle);
  }

  private static void track(String event, int flags) {

    try {
      if (checkAcceptability(flags, FLURRY)) {
        FlurryAgent.logEvent(event);
        Logger.d(TAG, "Flurry Event: " + event);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void logFabricEvent(String event, Map<String, String> map, int flags) {
    if (checkAcceptability(flags, FABRIC)) {
      CustomEvent customEvent = new CustomEvent(event);
      for (Map.Entry<String, String> entry : map.entrySet()) {
        customEvent.putCustomAttribute(entry.getKey(), entry.getValue());
      }
      Answers.getInstance()
          .logCustom(customEvent);
      Logger.d(TAG, "Fabric Event: " + event + ", Map: " + map);
    }
  }

  private static void logFacebookEvents(String eventName, Map<String, String> map) {
    if (BuildConfig.BUILD_TYPE.equals("debug") && map == null) {
      return;
    }
    Bundle parameters = new Bundle();
    if (map != null) {
      for (String s : map.keySet()) {
        parameters.putString(s, map.get(s));
      }
    }
    logFacebookEvents(eventName, parameters);
  }

  private static void logFacebookEvents(String eventName, Bundle parameters) {
    if (BuildConfig.BUILD_TYPE.equals("debug")) {
      return;
    }
    Logger.w(TAG, "Facebook Event: " + eventName + " : " + parameters.toString());
    facebookLogger.logEvent(eventName, parameters);
  }

  public void save(@NonNull String key, @NonNull Event event) {
    saver.save(key + event.getClass()
        .getName(), event);
  }

  public @Nullable Event get(String key, Class<? extends Event> clazz) {
    return saver.get(key + clazz.getName());
  }

  public FacebookEvent getFacebookEvent(String key) {
    return (FacebookEvent) get(key, FacebookEvent.class);
  }

  public FlurryEvent getFlurryEvent(String key) {
    return (FlurryEvent) get(key, FlurryEvent.class);
  }

  public FabricEvent getFabricEvent(String key) {
    return (FabricEvent) get(key, FabricEvent.class);
  }

  public void sendEvent(Event event) {
    event.send();
    saver.remove(event);
  }

  /**
   * This method is dealing with spot and share events only and should be refactored in case
   * one would want to send the same event to fabric AND any other analytics platform
   */
  public void sendSpotAndShareEvents(String eventName, Map<String, String> attributes,
      boolean fabric) {
    if (fabric) {
      logFabricEvent(eventName, attributes, FABRIC);
    } else {
      logFacebookEvents(eventName, attributes);
    }
  }

  public static class Lifecycle {

    public static class Application {

      private static final String UNKNOWN = "unknown";
      private static final String BI_ACTION = "OPEN";
      private static final String EVENT_NAME = "FIRST_LAUNCH";
      private static final String CONTEXT = "APPLICATION";
      private static final String UTM_SOURCE = "utm_source";
      private static final String UTM_MEDIUM = "utm_medium";
      private static final String UTM_CAMPAIGN = "utm_campaign";
      private static final String UTM_CONTENT = "utm_content";
      private static final String ENTRY_POINT = "entry_point";
      private static final String URL = "app_url";
      private static final String PACKAGE = "app_package";
      private static final String COUNTRY = "country";
      private static final String BROWSER = "browser";
      private static final String SITE_VERSION = "site_version";
      private static final String USER_AGENT = "user_agent";
      static AppEventsLogger facebookLogger;
      private static String utmSource = UNKNOWN;
      private static String utmMedium = UNKNOWN;
      private static String utmCampaign = UNKNOWN;
      private static String utmContent = UNKNOWN;
      private static String entryPoint = UNKNOWN;

      public static Completable onCreate(android.app.Application application,
          Converter.Factory converterFactory, OkHttpClient okHttpClient,
          BodyInterceptor bodyInterceptor, SharedPreferences sharedPreferences,
          TokenInvalidator tokenInvalidator) {

        //Integrate FacebookSDK
        FacebookSdk.sdkInitialize(application);
        AppEventsLogger.activateApp(application);
        facebookLogger = AppEventsLogger.newLogger(application);
        FirstLaunchAnalytics firstLaunchAnalytics =
            new FirstLaunchAnalytics(facebookLogger, Analytics.getInstance());
        return Observable.fromCallable(() -> {
          AppEventsLogger.setUserID(((V8Engine) application).getIdsRepository()
              .getUniqueIdentifier());
          return null;
        })
            .filter(firstRun -> SecurePreferences.isFirstRun(sharedPreferences))
            .doOnNext(dimensions -> setupDimensions(application))
            .doOnNext(facebookFirstLaunch -> firstLaunchAnalytics.sendFirstLaunchEvent(utmSource,
                utmMedium, utmCampaign, utmContent, entryPoint))
            .flatMap(facebookFirstLaunch -> {
              UTMTrackingBuilder utmTrackingBuilder =
                  new UTMTrackingBuilder(getTracking(application), getUTM());
              BiUtmAnalyticsRequestBody body =
                  new BiUtmAnalyticsRequestBody(utmTrackingBuilder.getUTMTrackingData());
              return BiUtmAnalyticsRequest.of(BI_ACTION, EVENT_NAME, CONTEXT, body, bodyInterceptor,
                  okHttpClient, converterFactory, sharedPreferences, tokenInvalidator)
                  .observe();
            })
            .toCompletable()
            .subscribeOn(Schedulers.io());
      }

      private static UTM getUTM() {
        return new UTM(utmSource, utmMedium, utmCampaign, utmContent, entryPoint);
      }

      private static void setupDimensions(android.app.Application application) {
        if (!checkForUTMFileInMetaINF(application)) {
          Dimensions.setUTMDimensionsToUnknown();
        } else {
          Dimensions.setUserProperties(utmSource, utmMedium, utmCampaign, utmContent, entryPoint);
        }
      }

      private static boolean checkForUTMFileInMetaINF(android.app.Application application) {
        ZipFile myZipFile = null;
        try {
          final String sourceDir = application.getApplicationContext()
              .getPackageManager()
              .getPackageInfo(application.getApplicationContext()
                  .getPackageName(), 0).applicationInfo.sourceDir;
          myZipFile = new ZipFile(sourceDir);
          final InputStream utmInputStream =
              myZipFile.getInputStream(myZipFile.getEntry("META-INF/utm"));

          UTMTrackingFileParser utmTrackingFileParser = new UTMTrackingFileParser(utmInputStream);
          myZipFile.close();

          utmSource = utmTrackingFileParser.valueExtracter(UTM_SOURCE);
          utmMedium = utmTrackingFileParser.valueExtracter(UTM_MEDIUM);
          utmCampaign = utmTrackingFileParser.valueExtracter(UTM_CAMPAIGN);
          utmContent = utmTrackingFileParser.valueExtracter(UTM_CONTENT);
          entryPoint = utmTrackingFileParser.valueExtracter(ENTRY_POINT);

          utmInputStream.close();
        } catch (IOException e) {
          Logger.d(TAG, "problem parsing utm/no utm file");
          return false;
        } catch (PackageManager.NameNotFoundException e) {
          Logger.d(TAG, "No package name utm file.");
          return false;
        } catch (NullPointerException e) {
          if (myZipFile != null) {
            try {
              myZipFile.close();
            } catch (IOException e1) {
              e1.printStackTrace();
              return false;
            }
            return false;
          }
          Logger.d(TAG, "No utm file.");
        }
        return true;
      }

      private static Tracking getTracking(android.app.Application application) {
        Tracking tracking = null;
        try {
          tracking = createTrackingObject(getTrackingFile(application));
        } catch (Exception e) {
          Logger.d(TAG, "Failed to parse utm/tracking files");
          return new Tracking(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN);
        }
        return tracking;
      }

      private static ZipFile getTrackingFile(android.app.Application application) throws Exception {
        final String sourceDir = application.getApplicationContext()
            .getPackageManager()
            .getPackageInfo(application.getApplicationContext()
                .getPackageName(), 0).applicationInfo.sourceDir;
        return new ZipFile(sourceDir);
      }

      private static Tracking createTrackingObject(ZipFile zipFile) throws IOException {
        InputStream inputStream = zipFile.getInputStream(zipFile.getEntry("META-INF/tracking"));
        UTMTrackingFileParser utmTrackingFileParser = new UTMTrackingFileParser(inputStream);
        zipFile.close();

        String url = utmTrackingFileParser.valueExtracter(URL);
        String packageName = utmTrackingFileParser.valueExtracter(PACKAGE);
        String country = utmTrackingFileParser.valueExtracter(COUNTRY);
        String browser = utmTrackingFileParser.valueExtracter(BROWSER);
        String siteVersion = utmTrackingFileParser.valueExtracter(SITE_VERSION);
        String userAgent = utmTrackingFileParser.valueExtracter(USER_AGENT);

        return new Tracking(url, packageName, country, browser, siteVersion, userAgent);
      }
    }

    public static class Activity {

      public static void onCreate(android.app.Activity activity) {

      }

      public static void onDestroy(android.app.Activity activity) {
      }

      public static void onResume(android.app.Activity activity) {

        final AptoideAccountManager accountManager =
            ((V8Engine) activity.getApplicationContext()).getAccountManager();
        //This needs to be cleaned when localytics is killed
        Bundle bundle = new Bundle();
        if (!accountManager.isLoggedIn()) {
          bundle.putString("Logged In", "Not Logged In");
          AppEventsLogger.updateUserProperties(bundle,
              response -> Logger.d("Facebook Analytics: ", response.toString()));
        } else {
          bundle.putString("Logged In", "Logged In");
          AppEventsLogger.updateUserProperties(bundle,
              response -> Logger.d("Facebook Analytics: ", response.toString()));
        }
      }

      public static void onStart(android.app.Activity activity) {

        if (!ACTIVATE_FLURRY) {
          return;
        }

        Logger.d(TAG, "FlurryAgent.onStartSession called");
        FlurryAgent.onStartSession(activity, BuildConfig.FLURRY_KEY);
      }

      public static void onStop(android.app.Activity activity) {

        if (!ACTIVATE_FLURRY) {
          return;
        }

        Logger.d(TAG, "FlurryAgent.onEndSession called");
        FlurryAgent.onEndSession(activity);
      }
    }
  }

  public static class Rollback {

    private static final String EVENT_NAME_DOWNGRADE_DIALOG = "Downgrade_Dialog";

    public static void downgradeDialogContinue() {
      track(EVENT_NAME_DOWNGRADE_DIALOG, ACTION, "Continue", FLURRY);
    }

    public static void downgradeDialogCancel() {
      track(EVENT_NAME_DOWNGRADE_DIALOG, ACTION, "Cancel", FLURRY);
    }
  }

  public static class Account {

    private static final String LOGIN_SIGN_UP_START_SCREEN = "Account_Login_Signup_Start_Screen";
    private static final String SIGNUP_SCREEN = "Account_Signup_Screen";
    private static final String LOGIN_SCREEN = "Account_Login_Screen";
    private static final String CREATE_USER_PROFILE = "Account_Create_A_User_Profile_Screen";
    private static final String PROFILE_SETTINGS = "Account_Profile_Settings_Screen";
    private static final String CREATE_YOUR_STORE = "Account_Create_Your_Store_Screen";
    private static final String HAS_PICTURE = "has_picture";
    private static final String SCREEN = "Screen";
    private static final String ENTRY = "Account_Entry";
    private static final String SOURCE = "Source";
    private static final String STATUS = "Status";
    private static final String STATUS_DETAIL = "Status Detail";

    public static void clickIn(StartupClick clickEvent, StartupClickOrigin startupClickOrigin) {
      track(LOGIN_SIGN_UP_START_SCREEN, ACTION, clickEvent.getClickEvent(), ALL);
      Map<String, String> map = new HashMap<>();
      map.put(ACTION, clickEvent.getClickEvent());
      map.put(SCREEN, startupClickOrigin.getClickOrigin());
      logFacebookEvents(LOGIN_SIGN_UP_START_SCREEN, map);
    }

    public static void signInSuccessAptoide(SignUpLoginStatus result) {
      track(SIGNUP_SCREEN, ALL);
      logFacebookEvents(SIGNUP_SCREEN, STATUS, result.getStatus());
    }

    public static void loginStatus(LoginMethod loginMethod, SignUpLoginStatus status,
        LoginStatusDetail statusDetail) {
      track(LOGIN_SCREEN, METHOD, loginMethod.getMethod(), ALL);
      Map<String, String> map = new HashMap<>();
      map.put(METHOD, loginMethod.getMethod());
      map.put(STATUS, status.getStatus());
      map.put(STATUS_DETAIL, statusDetail.getLoginStatusDetail());
      logFacebookEvents(LOGIN_SCREEN, map);
    }

    public static void createdUserProfile(boolean hasPicture) {
      track(CREATE_USER_PROFILE, HAS_PICTURE, hasPicture ? "True" : "False", ALL);
      Map<String, String> map = new HashMap<>();
      map.put(HAS_PICTURE, hasPicture ? "True" : "False");
      logFacebookEvents(CREATE_USER_PROFILE, map);
    }

    public static void accountProfileAction(int screen, ProfileAction action) {
      HashMap<String, String> map = new HashMap<>();
      map.put(ACTION, action.getAction());
      map.put("screen", Integer.toString(screen));
      track(PROFILE_SETTINGS, map, ALL);
      logFacebookEvents(PROFILE_SETTINGS, map);
    }

    public static void createStore(boolean hasPicture, CreateStoreAction action) {
      HashMap<String, String> map = new HashMap<>();
      map.put(ACTION, action.getAction());
      map.put(HAS_PICTURE, hasPicture ? "True" : "False");
      track(CREATE_YOUR_STORE, map, ALL);
      logFacebookEvents(CREATE_YOUR_STORE, map);
    }

    public static void enterAccountScreen(AccountOrigins sourceValue) {
      logFacebookEvents(ENTRY, SOURCE, sourceValue.getOrigin());
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
      MAIN("Main"), JOIN_UP("Join Aptoide Slide Up"), LOGIN_UP("Login Slide Up");

      private String clickOrigin;

      StartupClickOrigin(String clickOrigin) {
        this.clickOrigin = clickOrigin;
      }

      public String getClickOrigin() {
        return clickOrigin;
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
      WIZARD("Wizard"), MY_ACCOUNT("My Account"), TIMELINE("Timeline"), STORE(
          "Store"), APP_VIEW_FLAG("App View Flag"), APP_VIEW_SHARE(
          "App View Share on Timeline"), SHARE_CARD("Share Card"), LIKE_CARD(
          "Like Card"), COMMENT_LIST("Comment List"), RATE_DIALOG("Reviews FAB"), REPLY_REVIEW(
          "Reply Review"), REVIEW_FEEDBACK("Review Feedback"), SOCIAL_LIKE(
          "Like Social Card"), STORE_COMMENT("Store Comment"), LATEST_COMMENTS_STORE(
          "Comment on Latest Store Comments"), POST_ON_TIMELINE("Post on Timeline"),;

      private final String origin;

      AccountOrigins(String origin) {
        this.origin = origin;
      }

      public String getOrigin() {
        return origin;
      }
    }

    public enum SignUpLoginStatus {
      SUCCESS("Success"), FAILED("Failed");

      private final String status;

      SignUpLoginStatus(String result) {
        this.status = result;
      }

      public String getStatus() {
        return status;
      }
    }

    public enum LoginStatusDetail {
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

  public static class AdultContent {

    public static final String EVENT_NAME = "Adult Content";

    public static final String UNLOCK = "false";
    public static final String LOCK = "true";

    public static void lock() {
      track(EVENT_NAME, ACTION, LOCK, FLURRY);
    }

    public static void unlock() {
      track(EVENT_NAME, ACTION, UNLOCK, FLURRY);
    }
  }

  public static class ApplicationLaunch {

    public static final String EVENT_NAME = "Application Launch";
    public static final String FACEBOOK_APP_LAUNCH = "Aptoide Launch";
    public static final String SOURCE = "Source";
    public static final String LAUNCHER = "Launcher";
    public static final String WEBSITE = "Website";
    public static final String NEW_UPDATES_NOTIFICATION = "New Updates Available";
    public static final String DOWNLOADING_UPDATES = "Downloading Updates";
    public static final String TIMELINE_NOTIFICATION = "Timeline Notification";
    public static final String NEW_REPO = "New Repository";
    public static final String URI = "Uri";

    public static void launcher() {
      logFacebookEvents(FACEBOOK_APP_LAUNCH, SOURCE, LAUNCHER);
    }

    public static void website(String uri) {
      Logger.d(TAG, "website: " + uri);
      HashMap<String, String> map = new HashMap<>();
      map.put(SOURCE, WEBSITE);

      if (uri != null) {
        map.put(URI, uri.substring(0, uri.indexOf(":")));
      }

      track(EVENT_NAME, map, ALL);
      logFacebookEvents(FACEBOOK_APP_LAUNCH, map);
    }

    public static void newUpdatesNotification() {
      track(EVENT_NAME, SOURCE, NEW_UPDATES_NOTIFICATION, ALL);
      logFacebookEvents(EVENT_NAME, SOURCE, NEW_UPDATES_NOTIFICATION);
    }

    public static void downloadingUpdates() {
      track(EVENT_NAME, SOURCE, DOWNLOADING_UPDATES, ALL);
      logFacebookEvents(EVENT_NAME, SOURCE, DOWNLOADING_UPDATES);
    }

    public static void timelineNotification() {
      track(EVENT_NAME, SOURCE, TIMELINE_NOTIFICATION, ALL);
      logFacebookEvents(EVENT_NAME, SOURCE, TIMELINE_NOTIFICATION);
    }

    public static void newRepo() {
      track(EVENT_NAME, SOURCE, NEW_REPO, ALL);
      logFacebookEvents(EVENT_NAME, SOURCE, NEW_REPO);
    }
  }

  public static class ClickedOnInstallButton {

    private static final String EVENT_NAME = "Clicked on install button";

    //attributes
    private static final String APPLICATION_NAME = "Application Name";
    private static final String WARNING = "Warning";
    private static final String APPLICATION_PUBLISHER = "Application Publisher";

    public static void clicked(GetAppMeta.App app) {
      try {
        HashMap<String, String> map = new HashMap<>();

        map.put(APPLICATION_NAME, app.getPackageName());
        map.put(APPLICATION_PUBLISHER, app.getDeveloper()
            .getName());

        track(EVENT_NAME, map, ALL);

        Bundle parameters = new Bundle();
        parameters.putString(APPLICATION_NAME, app.getPackageName());
        parameters.putString(APPLICATION_PUBLISHER, app.getDeveloper()
            .getName());
        logFacebookEvents(EVENT_NAME, parameters);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static class AppsTimeline {

    public static final String EVENT_NAME = "Apps Timeline";
    public static final String ACTION = "Action";
    public static final String PACKAGE_NAME = "Package Name";
    public static final String TITLE = "Title";
    public static final String PUBLISHER = "Publisher";
    public static final String CARD_TYPE = "Card Type";

    public static final String BLANK = "(blank)";
    public static final String OPEN_ARTICLE = "Open Article";
    public static final String OPEN_ARTICLE_HEADER = "Open Article Header";
    public static final String OPEN_VIDEO = "Open Video";
    public static final String OPEN_VIDEO_HEADER = "Open Video Header";
    public static final String OPEN_STORE = "Open Store";
    public static final String OPEN_APP_VIEW = "Open App View";
    public static final String UPDATE_APP = "Update Application";

    public static void clickOnCard(String cardType, String packageName, String title,
        String publisher, String action) {
      HashMap<String, String> map = new HashMap<>();

      map.put(ACTION, action);
      map.put(PACKAGE_NAME, packageName);
      map.put(TITLE, title);
      map.put(PUBLISHER, publisher);

      flurryTrack(map, cardType);
    }

    private static void flurryTrack(HashMap<String, String> map, String cardType) {
      String eventName = cardType + "_" + EVENT_NAME;
      track(eventName, map, FLURRY);
    }

    public static void pullToRefresh() {
      track("Pull-to-refresh_Apps Timeline", FLURRY);
    }

    public static void endlessScrollLoadMore() {
      track("Endless-scroll_Apps Timeline", FLURRY);
    }

    public static void openTimeline() {
      track("Open Apps Timeline", FLURRY);
    }
  }

  public static class Dimensions {

    public static final String UNKNOWN = "unknown";
    public static final String GMS = "GMS";
    public static final String HAS_HGMS = "Has GMS";
    public static final String NO_GMS = "No GMS";
    public static final String UTM_SOURCE = "UTM Source";
    public static final String UTM_MEDIUM = "UTM Medium";
    public static final String UTM_CONTENT = "UTM Content";
    public static final String UTM_CAMPAIGN = "UTM Campaign";
    public static final String ENTRY_POINT = "Entry Point";

    public static void setGmsPresent(boolean isPlayServicesAvailable) {
      if (isPlayServicesAvailable) {
        setUserProperties(GMS, HAS_HGMS);
      } else {
        setUserProperties(GMS, NO_GMS);
      }
    }

    /**
     * Responsible for setting facebook analytics user properties
     * These were known as custom dimensions in localytics
     */
    private static void setUserProperties(String key, String value) {
      Bundle parameters = new Bundle();
      parameters.putString(key, value);
      AppEventsLogger.updateUserProperties(parameters,
          response -> Logger.d("Facebook Analytics: ", response.toString()));
    }

    private static void setUserPropertiesWithBundle(Bundle data) {
      AppEventsLogger.updateUserProperties(data,
          response -> Logger.d("Facebook Analytics: ", response.toString()));
    }

    public static void setUserProperties(String utmSource, String utmMedium, String utmCampaign,
        String utmContent, String entryPoint) {
      setUserPropertiesWithBundle(
          createUserPropertiesBundle(utmSource, utmMedium, utmCampaign, utmContent, entryPoint));
    }

    public static Bundle createUserPropertiesBundle(String utmSource, String utmMedium,
        String utmCampaign, String utmContent, String entryPoint) {
      Bundle data = new Bundle();
      data.putString(UTM_SOURCE, utmSource);
      data.putString(UTM_MEDIUM, utmMedium);
      data.putString(UTM_CAMPAIGN, utmCampaign);
      data.putString(UTM_CONTENT, utmContent);
      data.putString(ENTRY_POINT, entryPoint);
      return data;
    }

    public static void setUTMDimensionsToUnknown() {
      Bundle data = new Bundle();
      data.putString(UTM_SOURCE, UNKNOWN);
      data.putString(UTM_MEDIUM, UNKNOWN);
      data.putString(UTM_CAMPAIGN, UNKNOWN);
      data.putString(UTM_CONTENT, UNKNOWN);
      data.putString(ENTRY_POINT, UNKNOWN);
      setUserPropertiesWithBundle(data);
    }
  }

  public static class AppViewViewedFrom {

    public static final String APP_VIEWED_OPEN_FROM_EVENT_NAME_KEY = "App_Viewed_Open_From";
    public static final int NUMBER_OF_STEPS_TO_RECORD = 5;
    public static final String HOME_SCREEN_STEP = "home";
    private static ArrayList<String> STEPS = new ArrayList<>();
    private static String lastStep;

    public static void appViewOpenFrom(String packageName, String developerName,
        String trustedBadge) {

      Collections.reverse(STEPS);
      if (STEPS.contains(HOME_SCREEN_STEP)) {
        String stringForSourceEvent = formatStepsToSingleEvent(STEPS);
        HashMap<String, String> map = new HashMap<>();
        map.put("Package Name", packageName);
        map.put("Source", stringForSourceEvent);
        map.put("Trusted Badge", trustedBadge);
        map.put("Application Publisher", developerName);
        int index = STEPS.indexOf(HOME_SCREEN_STEP);
        if (index > 0) {
          String source = STEPS.get(index - 1);
          if (source.equals("apps-group-editors-choice")) {
            map.put("editors package name", packageName);
          } else {
            map.put("bundle package name", source + "_" + packageName);
            map.put("bundle category", source);
          }
        }
        Logger.d(TAG, "appViewOpenFrom: " + map);

        if (map.containsKey("Source") && !containsUnwantedValues(map.get("Source"))) {
          track(APP_VIEWED_OPEN_FROM_EVENT_NAME_KEY, map, FLURRY);
        }

        Bundle parameters = new Bundle();
        parameters.putString("Package Name", packageName);
        parameters.putString("Source", stringForSourceEvent);
        parameters.putString("Trusted Badge", trustedBadge);
        parameters.putString("Application Publisher", developerName);
        logFacebookEvents(APP_VIEWED_OPEN_FROM_EVENT_NAME_KEY, parameters);
      }
      STEPS.clear();
    }

    private static String formatStepsToSingleEvent(ArrayList<String> listOfSteps) {
      return TextUtils.join("_", listOfSteps.subList(0, listOfSteps.indexOf(HOME_SCREEN_STEP)));
    }

    protected static boolean containsUnwantedValues(String source) {
      String[] sourceArray = source.split("_");
      for (String step : sourceArray) {
        if (Arrays.asList(unwantedValuesList)
            .contains(step)) {
          return true;
        }
      }
      return false;
    }

    public static void addStepToList(String step) {
      if (!TextUtils.isEmpty(step)) {
        STEPS.add(step.replace(" ", "-")
            .toLowerCase());
        Logger.d(TAG, "addStepToList() called with: step = [" + step + "]");
        if (STEPS.size() > NUMBER_OF_STEPS_TO_RECORD) {
          STEPS.remove(0);
        }
        lastStep = step;
      }
    }

    static String getLastStep() {
      return lastStep;
    }
  }

  public static class HomePageEditorsChoice {

    public static final String HOME_PAGE_EDITORS_CHOICE = "Home_Page_Editors_Choice";

    public static void clickOnEditorsChoiceItem(int position, String packageName, boolean isHome) {
      HashMap<String, String> map = new HashMap<>();
      map.put("Application Name", packageName);
      if (isHome) {
        map.put("Search Position", "Home_" + Integer.valueOf(position)
            .toString());
      } else {
        map.put("Search Position", "More_" + Integer.valueOf(position)
            .toString());
      }

      track(HOME_PAGE_EDITORS_CHOICE, map, FLURRY);
    }
  }

  public static class File {

    private static final String EVENT_NAME = "Download_99percent";
    private static final String ATTRIBUTE = "APK";

    public static void moveFile(String moveType) {
      track(EVENT_NAME, ATTRIBUTE, moveType, FLURRY);
    }
  }

  public static class RootInstall {

    private static final String ROOT_INSTALL_EVENT_NAME = "ROOT_INSTALL";
    private static final String EXIT_CODE = "EXIT_CODE";
    private static final String IS_INSTALLED = "IS_INSTALLED";
    private static final String CONCAT = "CONCAT";
    private static final String IS_ROOT = "IS_ROOT";
    private static final String SETTING_ROOT = "SETTING_ROOT";
    private static final String IS_INSTALLATION_TYPE_EVENT_NAME = "INSTALLATION_TYPE";

    public static void rootInstallCompleted(int exitCode, boolean isInstalled) {
      Map<String, String> map = new HashMap<>();
      map.put(EXIT_CODE, String.valueOf(exitCode));
      map.put(IS_INSTALLED, String.valueOf(isInstalled));
      map.put(CONCAT, String.valueOf(isInstalled) + "_" + exitCode);

      logFabricEvent(ROOT_INSTALL_EVENT_NAME, map, FABRIC);
    }

    public static void installationType(boolean isRootAllowed, boolean isRoot) {
      Map<String, String> map = new HashMap<>();
      map.put(IS_ROOT, String.valueOf(isRoot));
      map.put(SETTING_ROOT, String.valueOf(isRootAllowed));
      map.put(CONCAT, String.valueOf(isRootAllowed) + "_" + String.valueOf(isRoot));

      logFabricEvent(IS_INSTALLATION_TYPE_EVENT_NAME, map, FABRIC);
    }
  }
}
