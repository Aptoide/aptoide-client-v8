package cm.aptoide.pt.aptoidesdk.misc;

/**
 * Created by neuro on 19-12-2016.
 */
public class RemoteLogger {
  private static final RemoteLogger instance = new RemoteLogger();

  protected RemoteLogger() {
  }

  public static RemoteLogger getInstance() {
    return instance;
  }

  public void log(Throwable throwable) {
    // TODO: 19-12-2016 neuro
  }
}
