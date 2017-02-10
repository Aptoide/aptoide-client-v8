package cm.aptoide.pt.aptoidesdk.misc;

import lombok.Setter;

/**
 * Created by neuro on 19-12-2016.
 */
public class RemoteLogger {
  private static final RemoteLogger instance = new RemoteLogger();
  @Setter private static boolean debug = false;

  protected RemoteLogger() {
  }

  public static RemoteLogger getInstance() {
    return instance;
  }

  public void log(Throwable throwable) {
    if (debug) {
      throwable.printStackTrace();
    }
  }
}
