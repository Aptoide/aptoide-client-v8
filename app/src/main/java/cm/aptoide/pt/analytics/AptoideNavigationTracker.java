package cm.aptoide.pt.analytics;

import cm.aptoide.pt.logger.Logger;
import java.util.List;

/**
 * Created by jdandrade on 06/09/2017.
 */

public class AptoideNavigationTracker implements NavigationTracker {

  private List<String> viewList;

  public AptoideNavigationTracker(List<String> viewList) {
    this.viewList = viewList;
  }

  @Override public void registerView(String viewName) {
    viewList.add(viewName);
    Logger.d(this.getClass()
        .getName(), "View is: " + viewName);
  }

  @Override public String getPreviousViewName() {
    // TODO: 07/09/2017 fix this logic
    return viewList.get(viewList.size() - 1);
  }

  @Override public String getCurrentViewName() {
    // TODO: 07/09/2017 fix this logic
    return viewList.get(viewList.size() - 1);
  }
}
