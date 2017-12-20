package cm.aptoide.pt.crashreports;

import com.crashlytics.android.Crashlytics;

/**
 * Created by neuro on 09-12-2016.
 */

public class CrashlyticsCrashLogger implements CrashLogger {

  private final static String TAG = CrashlyticsCrashLogger.class.getName();

  private static final String LANGUAGE = "Language";

  private final Crashlytics crashlytics;

  //var with the language the app is set to
  private String language;

  public CrashlyticsCrashLogger(Crashlytics crashlytics) {
    this.crashlytics = crashlytics;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  /**
   * Log crash exception to fabric.io
   *
   * @param throwable exception you want to send
   */
  @Override public void log(Throwable throwable) {
    Crashlytics.setString(LANGUAGE, language);
    Crashlytics.logException(throwable);
  }

  /**
   * Logs key-value pair in crashes
   *
   * @param key unique key to send on crash
   * @param value value you want associated with the key
   */
  @Override public void log(String key, String value) {
    Crashlytics.setString(LANGUAGE, language);
    Crashlytics.setString(key, value);
  }
}
