package cm.aptoide.analytics.implementation.navigation;

import cm.aptoide.analytics.DebugLogger;
import cm.aptoide.analytics.implementation.PageViewsAnalytics;
import java.util.Collections;
import java.util.List;

public class NavigationTracker {

  private static final String TAG = NavigationTracker.class.getSimpleName();
  private final ViewNameFilter viewNameFilter;
  private final DebugLogger logger;
  private List<ScreenTagHistory> historyList;
  private PageViewsAnalytics pageViewsAnalytics;

  public NavigationTracker(List<ScreenTagHistory> historyList, ViewNameFilter viewNameFilter,
      PageViewsAnalytics pageViewsAnalytics, DebugLogger logger) {
    this.historyList = historyList;
    this.viewNameFilter = viewNameFilter;
    this.pageViewsAnalytics = pageViewsAnalytics;
    this.logger = logger;
  }

  public void registerScreen(ScreenTagHistory screenTagHistory) {
    if (screenTagHistory != null && filter(screenTagHistory)) {
      historyList.add(screenTagHistory);
      pageViewsAnalytics.sendPageViewedEvent(getViewName(true), getViewName(false),
          screenTagHistory.getStore());
      logger.d(TAG, "NavigationTracker size: "
          + historyList.size()
          + "   Registering screen: "
          + screenTagHistory);
    }
  }

  public ScreenTagHistory getCurrentScreen() {
    if (historyList.isEmpty()) {
      return new ScreenTagHistory();
    }
    return historyList.get(historyList.size() - 1);
  }

  public List<ScreenTagHistory> getHistoryList() {
    return historyList;
  }

  public ScreenTagHistory getPreviousScreen() {
    if (historyList.size() < 2) {
      return new ScreenTagHistory();
    }
    return historyList.get(historyList.size() - 2);
  }

  public String getPreviousViewName() {
    if (historyList.size() < 2) {
      return "";
    } else {
      return historyList.get(historyList.size() - 2)
          .getFragment();
    }
  }

  public String getCurrentViewName() {
    if (historyList.isEmpty()) {
      return "";
    } else {
      return historyList.get(historyList.size() - 1)
          .getFragment();
    }
  }

  private boolean filter(ScreenTagHistory screenTagHistory) {
    return viewNameFilter.filter(screenTagHistory.getFragment());
  }

  public String getPrettyScreenHistory() {
    StringBuilder sb = new StringBuilder();
    List<ScreenTagHistory> tmp = historyList;
    Collections.reverse(tmp);
    for (ScreenTagHistory screen : tmp) {
      sb.append("[")
          .append(screen.toString())
          .append("]");
    }
    return sb.toString();
  }

  public String getViewName(boolean isCurrent) {
    String viewName = "";
    try {
      if (isCurrent) {
        viewName = getCurrentViewName();
      } else {
        viewName = getPreviousViewName();
      }
    } catch (NullPointerException exception) {
    }
    return viewName;
  }
}
