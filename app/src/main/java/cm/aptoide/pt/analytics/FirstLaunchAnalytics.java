package cm.aptoide.pt.analytics;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BiUtmAnalyticsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BiUtmAnalyticsRequestBody;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
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

/**
 * Created by pedroribeiro on 27/06/17.
 */

public class FirstLaunchAnalytics {

  public static final String FIRST_LAUNCH = "Aptoide_First_Launch";
  public static final String EVENT_NAME = "FIRST_LAUNCH";
  public static final String GMS = "GMS";
  public static final String HAS_HGMS = "Has GMS";
  public static final String NO_GMS = "No GMS";
  private static final String URL = "app_url";
  private static final String PACKAGE = "app_package";
  private static final String COUNTRY = "country";
  private static final String BROWSER = "browser";
  private static final String SITE_VERSION = "site_version";
  private static final String USER_AGENT = "user_agent";
  private static final String UNKNOWN = "unknown";
  private static final String BI_ACTION = "OPEN";
  private static final String CONTEXT = "APPLICATION";
  private static final String UTM_SOURCE = "UTM Source";
  private static final String UTM_MEDIUM = "UTM Medium";
  private static final String UTM_CONTENT = "UTM Content";
  private static final String UTM_CAMPAIGN = "UTM Campaign";
  private static final String ENTRY_POINT = "Entry Point";
  private static final String TAG = FirstLaunchAnalytics.class.getSimpleName();

  private final AnalyticsManager analyticsManager;
  private String utmSource = UNKNOWN;
  private String utmMedium = UNKNOWN;
  private String utmCampaign = UNKNOWN;
  private String utmContent = UNKNOWN;
  private String entryPoint = UNKNOWN;

  public FirstLaunchAnalytics(AnalyticsManager analyticsManager) {
    this.analyticsManager = analyticsManager;
  }

  public void sendFirstLaunchEvent(String utmSource, String utmMedium, String utmCampaign,
      String utmContent, String entryPoint) {
    analyticsManager.logEvent(
        createFacebookFirstLaunchDataMap(utmSource, utmMedium, utmCampaign, utmContent, entryPoint),
        FIRST_LAUNCH, AnalyticsManager.Action.OPEN, "");
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
      SharedPreferences sharedPreferences, Converter.Factory converterFactory,
      OkHttpClient okHttpClient, BodyInterceptor<BaseBody> bodyInterceptor,
      TokenInvalidator tokenInvalidator) {

    FacebookSdk.sdkInitialize(application);
    AppEventsLogger.activateApp(application);
    AppEventsLogger.newLogger(application);
    return Observable.fromCallable(() -> {
      AppEventsLogger.setUserID((((AptoideApplication) application).getIdsRepository()
          .getUniqueIdentifier()));
      return null;
    })
        .filter(firstRun -> SecurePreferences.isFirstRun(sharedPreferences))
        .doOnNext(dimensions -> setupDimensions(application))
        .doOnNext(facebookFirstLaunch -> sendFirstLaunchEvent(utmSource, utmMedium, utmCampaign,
            utmContent, entryPoint))
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

  private UTM getUTM() {
    return new UTM(utmSource, utmMedium, utmCampaign, utmContent, entryPoint);
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

  private Tracking getTracking(android.app.Application application) {
    Tracking tracking = null;
    try {
      tracking = createTrackingObject(getTrackingFile(application));
    } catch (Exception e) {
      Logger.d(TAG, "Failed to parse utm/tracking files");
      return new Tracking(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN);
    }
    return tracking;
  }

  private ZipFile getTrackingFile(android.app.Application application) throws Exception {
    final String sourceDir = application.getApplicationContext()
        .getPackageManager()
        .getPackageInfo(application.getApplicationContext()
            .getPackageName(), 0).applicationInfo.sourceDir;
    return new ZipFile(sourceDir);
  }

  private Tracking createTrackingObject(ZipFile zipFile) throws IOException {
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

  public void setGmsPresent(boolean isPlayServicesAvailable) {
    setUserProperties(GMS, isPlayServicesAvailable ? HAS_HGMS : NO_GMS);
  }

  /**
   * Responsible for setting facebook analytics user properties
   * These were known as custom dimensions in localytics
   */
  private void setUserProperties(String key, String value) {
    Bundle parameters = new Bundle();
    parameters.putString(key, value);
    AppEventsLogger.updateUserProperties(parameters,
        response -> Logger.d("Facebook Analytics: ", response.toString()));
  }

  private void setUserPropertiesWithBundle(Bundle data) {
    AppEventsLogger.updateUserProperties(data,
        response -> Logger.d("Facebook Analytics: ", response.toString()));
  }

  public void setUserProperties(String utmSource, String utmMedium, String utmCampaign,
      String utmContent, String entryPoint) {
    setUserPropertiesWithBundle(
        createUserPropertiesBundle(utmSource, utmMedium, utmCampaign, utmContent, entryPoint));
  }

  public Bundle createUserPropertiesBundle(String utmSource, String utmMedium, String utmCampaign,
      String utmContent, String entryPoint) {
    Bundle data = new Bundle();
    data.putString(UTM_SOURCE, utmSource);
    data.putString(UTM_MEDIUM, utmMedium);
    data.putString(UTM_CAMPAIGN, utmCampaign);
    data.putString(UTM_CONTENT, utmContent);
    data.putString(ENTRY_POINT, entryPoint);
    return data;
  }

  public void setUTMDimensionsToUnknown() {
    Bundle data = new Bundle();
    data.putString(UTM_SOURCE, UNKNOWN);
    data.putString(UTM_MEDIUM, UNKNOWN);
    data.putString(UTM_CAMPAIGN, UNKNOWN);
    data.putString(UTM_CONTENT, UNKNOWN);
    data.putString(ENTRY_POINT, UNKNOWN);
    setUserPropertiesWithBundle(data);
  }
}
