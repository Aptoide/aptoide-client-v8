package cm.aptoide.pt.crashreports;

import cm.aptoide.analytics.implementation.CrashLogger;
import cm.aptoide.pt.logger.Logger;

public class ConsoleLogger implements CrashLogger {

  private static final String TAG = ConsoleLogger.class.getName();

  @Override public void log(Throwable throwable) {
    Logger.getInstance().e(TAG, "Exception found", throwable);
  }

  @Override public void log(String key, String value) {
    Logger.getInstance().w(TAG, "logString : key: " + key + " , value: " + value);
  }
}
