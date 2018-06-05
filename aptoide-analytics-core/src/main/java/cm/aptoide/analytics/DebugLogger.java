package cm.aptoide.analytics;

public interface DebugLogger {
  void v(String tag, String msg);

  void v(String tag, String msg, Throwable tr);

  void d(Object object, String msg);

  void d(String tag, String msg);

  void d(String tag, String msg, Throwable tr);

  void i(Object object, String msg);

  void i(String tag, String msg);

  void i(Class clz, String msg);

  void w(String TAG, String msg);

  void w(String TAG, String msg, Throwable tr);

  void e(Object object, String msg);

  void e(String TAG, String msg);

  void e(Object object, Throwable tr);

  void e(String TAG, Throwable tr);

  void e(String TAG, String msg, Throwable tr);
}
