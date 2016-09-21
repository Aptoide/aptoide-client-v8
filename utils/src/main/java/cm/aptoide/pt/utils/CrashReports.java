package cm.aptoide.pt.utils;

import android.app.FragmentManager;
import android.content.Context;
import cm.aptoide.pt.logger.Logger;
import android.os.Build;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import io.fabric.sdk.android.Fabric;
import cm.aptoide.pt.logger.Logger;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by diogoloureiro on 16/09/16.
 */
public class CrashReports {

  private final static String TAG = CrashReports.class.getSimpleName();   //TAG for the logger
  @Setter private static String language;                                 //var with the language the app is set to
  @Getter @Setter private static boolean fabric_configured = true;        //var if fabric is configured or not, true by default

  /**
   * setup crash reports
   * @param context context from the class that's calling this method
   * @param fabric_configured true by default
   */
  public static void setup(Context context, boolean fabric_configured) {
    setFabric_configured(fabric_configured);
    Fabric.with(context, new Crashlytics.Builder().core(
        new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG || !fabric_configured).build()).build());
    Logger.d(TAG,"Setup of CrashReports");
  }

  /**
   * logs exception in crashes
   * @param throwable exception you want to send
   */
  public static void logException(Throwable throwable) {
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
    Crashlytics.setString(key, value);
    Logger.d(TAG, "logString : key: " + key + " , value: " + value);
  }

  /**
   * logs message in crashes
   * @param priority priority given to the message
   * @param tag unique tag that identifies the message
   * @param message message you want to send
   */
  public static void logMessage(int priority, String tag, String message) {
    Crashlytics.log(priority, tag, message);
    Logger.d(TAG, "logPriorityString: " + priority + " , " + tag + " , " + message);
  }
}