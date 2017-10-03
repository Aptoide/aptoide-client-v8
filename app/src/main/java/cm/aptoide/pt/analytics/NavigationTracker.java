package cm.aptoide.pt.analytics;

/**
 * Created by jdandrade on 06/09/2017.
 */

public interface NavigationTracker {
  void registerView(String viewName);

  String getPreviousViewName();

  String getCurrentViewName();
}
