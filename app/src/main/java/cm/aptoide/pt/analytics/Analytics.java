package cm.aptoide.pt.analytics;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BiUtmAnalyticsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BiUtmAnalyticsRequestBody;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

import static cm.aptoide.pt.analytics.Analytics.Lifecycle.Application.facebookLogger;

/**
 * Created by neuro on 07-05-2015.f
 * v8 integration by jdandrade on 25-07-2016
 */
public class Analytics {

  // Constantes globais a todos os eventos.
  public static final String ACTION = "Action";
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

  private static void logFacebookEvents(String eventName, String key, String value) {
    Bundle bundle = new Bundle();
    bundle.putString(key, value);
    facebookLogger.logEvent(eventName, bundle);
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

  public void sendEvent(Event event) {
    event.send();
    saver.remove(event);
  }

  /**
   * This method is dealing with spot and share events only and should be refactored in case
   * one would want to send the same event to fabric AND any other analytics platform
   */

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
          TokenInvalidator tokenInvalidator,AnalyticsManager analyticsManager) {

        //Integrate FacebookSDK
        FacebookSdk.sdkInitialize(application);
        AppEventsLogger.activateApp(application);
        facebookLogger = AppEventsLogger.newLogger(application);
        FirstLaunchAnalytics firstLaunchAnalytics =
            new FirstLaunchAnalytics(analyticsManager);
        return Observable.fromCallable(() -> {
          AppEventsLogger.setUserID(((AptoideApplication) application).getIdsRepository()
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

    public static final String ACTION = "Action";
    public static final String PACKAGE_NAME = "Package Name";

    public static final String BLANK = "(blank)";
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

  public static class File {

    private static final String EVENT_NAME = "Download_99percent";
    private static final String ATTRIBUTE = "APK";

    public static void moveFile(String moveType) {
      track(EVENT_NAME, ATTRIBUTE, moveType, FLURRY);
    }
  }

  public static class RootInstall {

    private static final String CONCAT = "CONCAT";
    private static final String IS_ROOT = "IS_ROOT";
    private static final String SETTING_ROOT = "SETTING_ROOT";
    private static final String IS_INSTALLATION_TYPE_EVENT_NAME = "INSTALLATION_TYPE";

    public static void installationType(boolean isRootAllowed, boolean isRoot) {
      Map<String, String> map = new HashMap<>();
      map.put(IS_ROOT, String.valueOf(isRoot));
      map.put(SETTING_ROOT, String.valueOf(isRootAllowed));
      map.put(CONCAT, String.valueOf(isRootAllowed) + "_" + String.valueOf(isRoot));

      logFabricEvent(IS_INSTALLATION_TYPE_EVENT_NAME, map, FABRIC);
    }
  }
}
