package cm.aptoide.pt.analytics;

/**
 * Created by jdandrade on 06/09/2017.
 */

public interface NavigationTracker {
  void registerView(String viewName);

  void registerTag(String tag);

  void registerTagNewObject(String tag);

  ScreenTagHistory getCurrentScreen();

  String getPreviousViewName();

  String getCurrentViewName();

  String getPreviousViewTag();

  String getCurrentViewTag();
}
