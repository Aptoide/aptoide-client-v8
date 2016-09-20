package cm.aptoide.pt.utils;

import android.content.Context;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import io.fabric.sdk.android.Fabric;
import cm.aptoide.pt.logger.Logger;

/**
 * Created by diogoloureiro on 16/09/16.
 */
public class CrashReports {

  private final static String TAG = CrashReports.class.getSimpleName();

  /**
   * setup crash reports
   * @param context context from the class that's calling this method
   * @param fabric_configured true by default
   */
  public static void setup(Context context, boolean fabric_configured) {
    Fabric.with(context, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(!fabric_configured).build()).build());
  }

  /**
   * logs exception in crashs
   * @param throwable exception you want to send
   */
  public static void logException(Throwable throwable){
    Crashlytics.logException(throwable);
    Logger.d(TAG,"logException: "+throwable.toString());
  }

  /**
   * logs string in crashs
   * @param key unique key to send on crash
   * @param value value you want associated with the key
   */
  public static void logString(String key, String value){
    Crashlytics.setString(key,value);
    Logger.d(TAG,"logString : key: "+key+" , value: "+value);
  }
}