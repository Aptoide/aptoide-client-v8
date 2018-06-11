package cm.aptoide.analytics;

public interface AnalyticsLogger {
  void logDebug(String tag, String msg);

  void logWarningDebug(String TAG, String msg);
}
