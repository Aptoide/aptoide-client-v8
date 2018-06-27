package cm.aptoide.analytics.implementation;

public interface SessionPersistence {
  void saveSessionTimestamp(long timestamp);

  long getTimestamp();
}
