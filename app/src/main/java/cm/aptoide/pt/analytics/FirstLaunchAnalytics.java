package cm.aptoide.pt.analytics;

import android.content.SharedPreferences;
import android.os.Bundle;
import cm.aptoide.analytics.AnalyticsLogger;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.GmsStatusValueProvider;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.safetynet.HarmfulAppsData;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.safetynet.SafetyNetClient;
import com.indicative.client.android.Indicative;
import io.rakam.api.Rakam;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Completable;
import rx.schedulers.Schedulers;

/**
 * Created by pedroribeiro on 27/06/17.
 */

public class FirstLaunchAnalytics {

  public static final String FIRST_LAUNCH = "Aptoide_First_Launch";
  public static final String PLAY_PROTECT_EVENT = "Google_Play_Protect";
  public static final String FIRST_LAUNCH_BI = "FIRST_LAUNCH";
  public static final String FIRST_LAUNCH_RAKAM = "aptoide_first_launch";
  private static final String GMS = "GMS";
  private static final String GMS_RAKAM = "gms";
  private static final String UNKNOWN = "unknown";
  private static final String CONTEXT = "APPLICATION";
  private static final String UTM_SOURCE = "UTM Source";
  private static final String UTM_MEDIUM = "UTM Medium";
  private static final String UTM_CONTENT = "UTM Content";
  private static final String UTM_CAMPAIGN = "UTM Campaign";

  private static final String UTM_TERM = "UTM Term";
  private static final String ENTRY_POINT = "Entry Point";
  private static final String CATEGORY = "category";
  private static final String IS_ACTIVE = "is_active";
  private static final String FLAGGED = "flagged";
  private static final String TAG = FirstLaunchAnalytics.class.getSimpleName();
  private final static String UTM_CONTENT_RAKAM = "utm_content";
  private final static String UTM_SOURCE_RAKAM = "utm_source";
  private final static String UTM_CAMPAIGN_RAKAM = "utm_campaign";
  private final static String UTM_MEDIUM_RAKAM = "utm_medium";
  private static final String UTM_PACKAGE_NAME = "utm_package_name";
  private final static String ENTRY_POINT_RAKAM = "entry_point";
  private static final String APTOIDE_PACKAGE = "aptoide_package";
  private final AnalyticsManager analyticsManager;
  private final AnalyticsLogger logger;
  private final String packageName;
  private final GmsStatusValueProvider gmsStatusValueProvider;
  private final SafetyNetClient safetyNetClient;
  private String utmSource = UNKNOWN;
  private String utmMedium = UNKNOWN;
  private String utmCampaign = UNKNOWN;
  private String utmContent = UNKNOWN;
  private String entryPoint = UNKNOWN;

  public FirstLaunchAnalytics(AnalyticsManager analyticsManager, AnalyticsLogger logger,
      SafetyNetClient safetyNetClient, String packageName,
      GmsStatusValueProvider gmsStatusValueProvider) {
    this.analyticsManager = analyticsManager;
    this.logger = logger;
    this.safetyNetClient = safetyNetClient;
    this.packageName = packageName;
    this.gmsStatusValueProvider = gmsStatusValueProvider;
  }

  private void sendFirstLaunchEvent(String utmSource, String utmMedium, String utmCampaign,
      String utmContent, String entryPoint) {
    analyticsManager.logEvent(
        createFacebookFirstLaunchDataMap(utmSource, utmMedium, utmCampaign, utmContent, entryPoint),
        FIRST_LAUNCH, AnalyticsManager.Action.OPEN, CONTEXT);
    analyticsManager.logEvent(
        createFacebookFirstLaunchDataMap(utmSource, utmMedium, utmCampaign, utmContent, entryPoint),
        FIRST_LAUNCH_BI, AnalyticsManager.Action.OPEN, CONTEXT);
    analyticsManager.logEvent(new HashMap<>(), FIRST_LAUNCH_RAKAM, AnalyticsManager.Action.OPEN,
        CONTEXT);
  }

  private void sendPlayProtectEvent() {
    safetyNetClient.listHarmfulApps()
        .addOnCompleteListener(task -> {
          boolean isActive = false;
          boolean isFlagged = false;
          String category = null;

          if (task.isSuccessful()) {
            isActive = true;
            isFlagged = false;
            SafetyNetApi.HarmfulAppsResponse result = task.getResult();
            category = getCategoryFlaggedByPlayProtect(result.getHarmfulAppsList());
            if (category != null) {
              isFlagged = true;
            }
          }
          Map<String, Object> data = new HashMap<>();
          data.put(IS_ACTIVE, isActive ? "true" : "false");
          data.put(FLAGGED, isFlagged ? "true" : "false");
          data.put(CATEGORY, category);
          analyticsManager.logEvent(data, PLAY_PROTECT_EVENT, AnalyticsManager.Action.OPEN,
              CONTEXT);
        });
  }

  private String getCategoryFlaggedByPlayProtect(List<HarmfulAppsData> list) {
    for (HarmfulAppsData app : list) {
      if (app.apkPackageName.equals(packageName)) {
        return getPlayProtectCategoryName(app.apkCategory);
      }
    }
    return null;
  }

  private String getPlayProtectCategoryName(int apkCategory) {
    switch (apkCategory) {
      case 9:
        return "BACKDOOR";
      case 8:
        return "CALL_FRAUD";
      case 21:
        return "DATA_COLLECTION";
      case 20:
        return "DENIAL_OF_SERVICE";
      case 5:
        return "FRAUDWARE";
      case 11:
        return "GENERIC_MALWARE";
      case 12:
        return "HARMFUL_SITE";
      case 14:
        return "HOSTILE_DOWNLOADER";
      case 15:
        return "NON_ANDROID_THREAT";
      case 2:
        return "PHISHING";
      case 17:
        return "PRIVILEGE_ESCALATION";
      case 1:
        return "RANSOMWARE";
      case 16:
        return "ROOTING";
      case 10:
        return "SPYWARE";
      case 19:
        return "SPAM";
      case 6:
        return "TOLL_FRAUD";
      case 18:
        return "TRACKING";
      case 3:
        return "TROJAN";
      case 4:
        return "UNCOMMON";
      case 7:
        return "WAP_FRAUD";
      case 13:
        return "WINDOWS_MALWARE";
    }
    return "UNKNOWN_CATEGORY";
  }

  private Map<String, Object> createFacebookFirstLaunchDataMap(String utmSource, String utmMedium,
      String utmCampaign, String utmContent, String entryPoint) {
    Map<String, Object> data = new HashMap<>();
    data.put(UTM_SOURCE, utmSource);
    data.put(UTM_MEDIUM, utmMedium);
    data.put(UTM_CAMPAIGN, utmCampaign);
    data.put(UTM_CONTENT, utmContent);
    data.put(ENTRY_POINT, entryPoint);
    return data;
  }

  public Completable sendAppStart(android.app.Application application,
      SharedPreferences sharedPreferences, IdsRepository idsRepository) {
    return idsRepository.getUniqueIdentifier()
        .doOnSuccess(AppEventsLogger::setUserID)
        .toObservable()
        .doOnNext(
            __ -> setupFirstLaunchSuperProperty(SecurePreferences.isFirstRun(sharedPreferences)))
        .doOnNext(__ -> sendPlayProtectEvent())
        .filter(__ -> SecurePreferences.isFirstRun(sharedPreferences))
        .doOnNext(
            __ -> sendFirstLaunchEvent(utmSource, utmMedium, utmCampaign, utmContent, entryPoint))
        .toCompletable()
        .subscribeOn(Schedulers.io());
  }

  private void setupFirstLaunchSuperProperty(boolean isFirstLaunch) {
    Rakam.getInstance()
        .setSuperProperties(addFirstLaunchProperties(isFirstLaunch, Rakam.getInstance()
            .getSuperProperties()));

    Map<String, Object> indicativeProperties = new HashMap<>();
    indicativeProperties.put("first_session", isFirstLaunch);
    indicativeProperties.put(APTOIDE_PACKAGE, packageName);
    Indicative.addProperties(indicativeProperties);
  }

  @NotNull
  private JSONObject addFirstLaunchProperties(boolean isFirstLaunch, JSONObject superProperties) {
    if (superProperties == null) {
      superProperties = new JSONObject();
    }
    try {
      superProperties.put("first_session", isFirstLaunch);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return superProperties;
  }

  public void setGmsPresent() {
    setUserProperties(GMS, gmsStatusValueProvider.getGmsValue());
  }

  /**
   * Responsible for setting facebook analytics user properties
   */
  private void setUserProperties(String key, String value) {
    Bundle parameters = new Bundle();
    parameters.putString(key, value);
    AppEventsLogger.updateUserProperties(parameters,
        response -> logger.logDebug("Facebook Analytics: ", response.toString()));
    FlurryAgent.UserProperties.add(key, value);
  }

  public void sendIndicativeFirstLaunchSourceUserProperties(String utmContent, String utmSource,
      String utmCampaign, String utmMedium, String term, String packageName) {
    Map<String, Object> indicativeProperties = new HashMap<>();
    indicativeProperties.put(GMS_RAKAM, gmsStatusValueProvider.getGmsValue());
    indicativeProperties.put(UTM_CONTENT_RAKAM, utmContent);
    indicativeProperties.put(UTM_SOURCE_RAKAM, utmSource);
    indicativeProperties.put(UTM_CAMPAIGN_RAKAM, utmCampaign);
    indicativeProperties.put(UTM_MEDIUM_RAKAM, utmMedium);
    indicativeProperties.put(UTM_TERM, term);
    indicativeProperties.put(UTM_PACKAGE_NAME, packageName);
    Indicative.addProperties(indicativeProperties);
  }
}
