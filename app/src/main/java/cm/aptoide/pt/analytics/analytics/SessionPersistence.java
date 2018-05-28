package cm.aptoide.pt.analytics.analytics;

interface SessionPersistence {
  void saveSessionTimestamp(long timestamp);

  long getTimestamp();
}
