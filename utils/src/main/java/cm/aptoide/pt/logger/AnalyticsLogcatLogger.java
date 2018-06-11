package cm.aptoide.pt.logger;

import cm.aptoide.analytics.AnalyticsLogger;

public class AnalyticsLogcatLogger implements AnalyticsLogger {
  @Override public void logDebug(String tag, String msg) {
    Logger.getInstance()
        .d(tag, msg);
  }

  @Override public void logWarningDebug(String TAG, String msg) {
    Logger.getInstance()
        .w(TAG, msg);
  }
}
