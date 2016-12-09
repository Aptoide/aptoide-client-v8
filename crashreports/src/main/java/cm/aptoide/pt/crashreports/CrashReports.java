package cm.aptoide.pt.crashreports;

import android.content.Context;
import cm.aptoide.pt.logger.Logger;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import io.fabric.sdk.android.Fabric;
import lombok.Setter;
import pt.aptoide.cm.crashreports.BuildConfig;

/**
 * Created by diogoloureiro on 16/09/16.
 */
public class CrashReports {

  private final static String TAG = CrashReports.class.getSimpleName();   //TAG for the logger
  @Setter private static String language;
  //var with the language the app is set to

  /**
   * setup crash reports
   *
   * @param context context from the class that's calling this method
   */
  public static void setup(Context context) {
    Fabric.with(context, new Crashlytics.Builder().core(
        new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
    Logger.d(TAG, "Setup of CrashReports");
  }

  /**
   * logs exception in crashes
   *
   * @param throwable exception you want to send
   */
  public static void logException(Throwable throwable) {
    if (!Fabric.isInitialized()) {
      Logger.w(TAG, "Fabric not initialized.");
      return;
    }

    Crashlytics.setString("Language", language);
    Crashlytics.logException(throwable);
    Logger.d(TAG, "logException: " + throwable.toString());
  }

  /**
   * logs string in crashes
   *
   * @param key unique key to send on crash
   * @param value value you want associated with the key
   */
  public static void logString(String key, String value) {
    if (!Fabric.isInitialized()) {
      Logger.w(TAG, "Fabric not initialized.");
      return;
    }

    Crashlytics.setString(key, value);
    Logger.d(TAG, "logString : key: " + key + " , value: " + value);
  }

  /**
   * logs message in crashes
   *
   * @param priority priority given to the message
   * @param tag unique tag that identifies the message
   * @param message message you want to send
   */
  public static void logMessage(int priority, String tag, String message) {
    if (!Fabric.isInitialized()) {
      Logger.w(TAG, "Fabric not initialized.");
      return;
    }

    Crashlytics.log(priority, tag, message);
    Logger.d(TAG, "logPriorityString: " + priority + " , " + tag + " , " + message);
  }
}
