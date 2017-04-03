package cm.aptoide.pt.v8engine.crashreports;

/**
 * Created by neuro on 09-12-2016.
 */

public interface CrashLogger {

  /**
   * Log crash exception
   *
   * @param throwable exception you want to send
   */
  void log(Throwable throwable);

  /**
   * Logs key-value pair in crashes
   *
   * @param key unique key to send on crash
   * @param value value you want associated with the key
   */
  void log(String key, String value);
}
