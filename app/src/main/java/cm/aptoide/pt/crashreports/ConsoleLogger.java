package cm.aptoide.pt.crashreports;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.crashreports.CrashLogger;

public class ConsoleLogger implements CrashLogger {

  private static final String TAG = ConsoleLogger.class.getName();

  @Override public void log(Throwable throwable) {
    Logger.e(TAG, "Exception found", throwable);
  }

  @Override public void log(String key, String value) {
    Logger.w(TAG, "logString : key: " + key + " , value: " + value);
  }
}
