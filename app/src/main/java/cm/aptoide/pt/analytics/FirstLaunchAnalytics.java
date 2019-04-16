package cm.aptoide.pt.analytics;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import cm.aptoide.analytics.AnalyticsLogger;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.safetynet.HarmfulAppsData;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.safetynet.SafetyNetClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by pedroribeiro on 27/06/17.
 */

public class FirstLaunchAnalytics {

  public static final String FIRST_LAUNCH = "Aptoide_First_Launch";
  public static final String PLAY_PROTECT_EVENT = "Google_Play_Protect";
  public static final String FIRST_LAUNCH_BI = "FIRST_LAUNCH";
  private static final String GMS = "GMS";
  private static final String HAS_HGMS = "Has GMS";
  private static final String NO_GMS = "No GMS";
  private static final String UNKNOWN = "unknown";
  private static final String CONTEXT = "APPLICATION";
  private static final String UTM_SOURCE = "UTM Source";
  private static final String UTM_MEDIUM = "UTM Medium";
  private static final String UTM_CONTENT = "UTM Content";
  private static final String UTM_CAMPAIGN = "UTM Campaign";
  private static final String ENTRY_POINT = "Entry Point";
  private static final String CATEGORY = "category";
  private static final String IS_ACTIVE = "is_active";
  private static final String FLAGGED = "flagged";
  private static final String TAG = FirstLaunchAnalytics.class.getSimpleName();

  private final AnalyticsManager analyticsManager;
  private final AnalyticsLogger logger;
  private final String packageName;
  private String utmSource = UNKNOWN;
  private String utmMedium = UNKNOWN;
  private String utmCampaign = UNKNOWN;
  private String utmContent = UNKNOWN;
  private String entryPoint = UNKNOWN;
  private SafetyNetClient safetyNetClient;

  public FirstLaunchAnalytics(AnalyticsManager analyticsManager, AnalyticsLogger logger,
      SafetyNetClient safetyNetClient, String packageName) {
    this.analyticsManager = analyticsManager;
    this.logger = logger;
    this.safetyNetClient = safetyNetClient;
    this.packageName = packageName;
  }

  private void sendFirstLaunchEvent(String utmSource, String utmMedium, String utmCampaign,
      String utmContent, String entryPoint) {
    analyticsManager.logEvent(
        createFacebookFirstLaunchDataMap(utmSource, utmMedium, utmCampaign, utmContent, entryPoint),
        FIRST_LAUNCH, AnalyticsManager.Action.OPEN, CONTEXT);
    analyticsManager.logEvent(
        createFacebookFirstLaunchDataMap(utmSource, utmMedium, utmCampaign, utmContent, entryPoint),
        FIRST_LAUNCH_BI, AnalyticsManager.Action.OPEN, CONTEXT);
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
      SharedPreferences sharedPreferences) {

    FacebookSdk.sdkInitialize(application);
    AppEventsLogger.activateApp(application);
    AppEventsLogger.newLogger(application);
    return Observable.fromCallable(() -> {
      AppEventsLogger.setUserID((((AptoideApplication) application).getIdsRepository()
          .getUniqueIdentifier()));
      return null;
    })
        .doOnNext(__ -> sendPlayProtectEvent())
        .doOnNext(__ -> setupDimensions(application))
        .filter(firstRun -> SecurePreferences.isFirstRun(sharedPreferences))
        .doOnNext(
            __ -> sendFirstLaunchEvent(utmSource, utmMedium, utmCampaign, utmContent, entryPoint))
        .toCompletable()
        .subscribeOn(Schedulers.io());
  }

  private void setupDimensions(android.app.Application application) {
    if (!checkForUTMFileInMetaINF(application)) {
      setUTMDimensionsToUnknown();
    } else {
      setUserProperties(utmSource, utmMedium, utmCampaign, utmContent, entryPoint);
    }
  }

  private boolean checkForUTMFileInMetaINF(android.app.Application application) {
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

      utmSource = utmTrackingFileParser.valueExtracter("utm_source");
      utmMedium = utmTrackingFileParser.valueExtracter("utm_medium");
      utmCampaign = utmTrackingFileParser.valueExtracter("utm_campaign");
      utmContent = utmTrackingFileParser.valueExtracter("utm_content");
      entryPoint = utmTrackingFileParser.valueExtracter("entry_point");

      utmInputStream.close();
    } catch (IOException e) {
      logger.logDebug(TAG, "problem parsing utm/no utm file");
      return false;
    } catch (PackageManager.NameNotFoundException e) {
      logger.logDebug(TAG, "No package name utm file.");
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
      logger.logDebug(TAG, "No utm file.");
    }
    return true;
  }

  public void setGmsPresent(boolean isPlayServicesAvailable) {
    setUserProperties(GMS, isPlayServicesAvailable ? HAS_HGMS : NO_GMS);
  }

  /**
   * Responsible for setting facebook analytics user properties
   */
  private void setUserProperties(String key, String value) {
    Bundle parameters = new Bundle();
    parameters.putString(key, value);
    AppEventsLogger.updateUserProperties(parameters,
        response -> logger.logDebug("Facebook Analytics: ", response.toString()));
  }

  private void setUserPropertiesWithBundle(Bundle data) {
    AppEventsLogger.updateUserProperties(data,
        response -> logger.logDebug("Facebook Analytics: ", response.toString()));
  }

  private void setUserProperties(String utmSource, String utmMedium, String utmCampaign,
      String utmContent, String entryPoint) {
    setUserPropertiesWithBundle(
        createUserPropertiesBundle(utmSource, utmMedium, utmCampaign, utmContent, entryPoint));
  }

  private Bundle createUserPropertiesBundle(String utmSource, String utmMedium, String utmCampaign,
      String utmContent, String entryPoint) {
    Bundle data = new Bundle();
    data.putString(UTM_SOURCE, utmSource);
    data.putString(UTM_MEDIUM, utmMedium);
    data.putString(UTM_CAMPAIGN, utmCampaign);
    data.putString(UTM_CONTENT, utmContent);
    data.putString(ENTRY_POINT, entryPoint);
    return data;
  }

  private void setUTMDimensionsToUnknown() {
    Bundle data = new Bundle();
    data.putString(UTM_SOURCE, UNKNOWN);
    data.putString(UTM_MEDIUM, UNKNOWN);
    data.putString(UTM_CAMPAIGN, UNKNOWN);
    data.putString(UTM_CONTENT, UNKNOWN);
    data.putString(ENTRY_POINT, UNKNOWN);
    setUserPropertiesWithBundle(data);
  }
}
