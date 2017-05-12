package cm.aptoide.pt.v8engine.crashreports;

import android.content.Context;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.BuildConfig;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;
import lombok.Setter;

/**
 * Created by neuro on 09-12-2016.
 */

public class CrashlyticsCrashLogger implements CrashLogger {

  private final static String TAG = CrashlyticsCrashLogger.class.getName();

  private static final String LANGUAGE = "Language";

  //var with the language the app is set to
  @Setter private String language;

  public CrashlyticsCrashLogger(Context context, boolean isDisabled) {

    Fabric.with(context, new Crashlytics.Builder().core(
        new CrashlyticsCore.Builder().disabled(isDisabled)
            .build())
        .build(), new TwitterCore(
        new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET)));
    Logger.d(TAG, "Setup of " + this.getClass()
        .getSimpleName() + " complete.");
  }

  /**
   * Log crash exception to fabric.io
   *
   * @param throwable exception you want to send
   */
  @Override public void log(Throwable throwable) {
    if (Fabric.isInitialized()) {
      Crashlytics.setString(LANGUAGE, language);
    }
    Crashlytics.logException(throwable);
  }

  /**
   * Logs key-value pair in crashes
   *
   * @param key unique key to send on crash
   * @param value value you want associated with the key
   */
  @Override public void log(String key, String value) {
    if (Fabric.isInitialized()) {
      Crashlytics.setString(LANGUAGE, language);
      Crashlytics.setString(key, value);
    }
  }
}