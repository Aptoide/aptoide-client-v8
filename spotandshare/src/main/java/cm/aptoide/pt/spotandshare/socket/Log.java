package cm.aptoide.pt.spotandshare.socket;

public final class Log {
  private static final String TAG = "SpotNShare";

  public static void d(String tag, String message) {
    System.out.println(TAG + " " + tag + " " + message);
  }

  public static void w(String tag, String message) {
    System.out.println(TAG + " " + tag + " " + message);
  }

  public static void e(String tag, String message, Throwable throwable) {
    System.out.println(TAG + " " + tag + " " + message + "\t" + throwable.toString());
  }
}
