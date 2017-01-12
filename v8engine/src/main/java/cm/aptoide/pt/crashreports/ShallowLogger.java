package cm.aptoide.pt.crashreports;

/**
 * Useful class for further log implementations to avoid the need to implement all the
 * interface methods and only implement the used methods
 */
class ShallowLogger implements CrashLogger {
  @Override public void log(Throwable throwable) {
    /* no-op */
  }

  @Override public void log(String key, String value) {
    /* no-op */
  }
}
