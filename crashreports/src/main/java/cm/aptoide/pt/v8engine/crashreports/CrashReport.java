package cm.aptoide.pt.v8engine.crashreports;

import android.util.Log;
import java.util.ArrayList;

public class CrashReport implements CrashLogger {

  private static final String TAG = CrashReport.class.getName();

  //
  // singleton architecture
  //

  private static CrashReport singleton = new CrashReport();
  // TODO: 12/1/2017 sithengineer should we protect this list from concurrent modifications?
  private ArrayList<CrashLogger> crashLoggers;

  private CrashReport() {
    // TODO: 12/1/2017 sithengineer is lazy initialization necessary here?
    crashLoggers = new ArrayList<>();
  }

  //
  // CrashReporter methods
  //

  public static CrashReport getInstance() {
    return singleton;
  }

  public CrashReport addLogger(CrashLogger crashLogger) {
    crashLoggers.add(crashLogger);
    return this;
  }

  @Override public void log(Throwable throwable) {
    if (!isInitialized()) {
      Log.e(TAG, "not initialized");
      return;
    }

    for (int i = 0; i < crashLoggers.size(); i++) {
      crashLoggers.get(i).log(throwable);
    }
  }

  @Override public void log(String key, String value) {
    if (!isInitialized()) {
      Log.e(TAG, "not initialized");
      return;
    }

    for (int i = 0; i < crashLoggers.size(); i++) {
      crashLoggers.get(i).log(key, value);
    }
  }

  private boolean isInitialized() {
    return crashLoggers != null && !crashLoggers.isEmpty();
  }

  public CrashLogger getLogger(Class<? extends CrashLogger> clazz) {
    if (!isInitialized()) {
      Log.e(TAG, "not initialized");
      return null;
    }

    for (int i = 0; i < crashLoggers.size(); i++) {
      if (clazz.isAssignableFrom(crashLoggers.get(i).getClass())) {
        return crashLoggers.get(i);
      }
    }
    return null;
  }
}
