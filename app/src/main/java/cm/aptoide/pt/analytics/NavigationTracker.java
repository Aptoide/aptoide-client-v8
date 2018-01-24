package cm.aptoide.pt.analytics;

import android.support.annotation.Nullable;
import cm.aptoide.pt.PageViewsAnalytics;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.store.view.home.HomeFragment;
import java.util.Collections;
import java.util.List;

public class NavigationTracker {

  private static final String TAG = NavigationTracker.class.getSimpleName();
  private final TrackerFilter trackerFilter;
  private List<ScreenTagHistory> historyList;
  private PageViewsAnalytics pageViewsAnalytics;

  public NavigationTracker(List<ScreenTagHistory> historyList, TrackerFilter trackerFilter,
      PageViewsAnalytics pageViewsAnalytics) {
    this.historyList = historyList;
    this.trackerFilter = trackerFilter;
    this.pageViewsAnalytics = pageViewsAnalytics;
  }

  public void registerScreen(ScreenTagHistory screenTagHistory) {
    if (screenTagHistory != null && filter(screenTagHistory)) {
      historyList.add(screenTagHistory);
      if (screenTagHistory.getFragment()
          .equals(HomeFragment.class.getSimpleName()) && screenTagHistory.getStore()
          .equals(ScreenTagHistory.Builder.APTOIDE_MAIN_HISTORY_STORE)) {
        pageViewsAnalytics.sendPageViewedEvent("Store_Fragment");
      } else {
        pageViewsAnalytics.sendPageViewedEvent(screenTagHistory.getFragment());
      }
      Logger.d(TAG, "VIEW - " + screenTagHistory);
    }
  }

  public @Nullable ScreenTagHistory getCurrentScreen() {
    if (historyList.isEmpty()) {
      return null;
    }
    return historyList.get(historyList.size() - 1);
  }

  public List<ScreenTagHistory> getHistoryList() {
    return historyList;
  }

  public @Nullable ScreenTagHistory getPreviousScreen() {
    if (historyList.size() < 2) {
      return null;
    }
    return historyList.get(historyList.size() - 2);
  }

  public String getPreviousViewName() {
    if (historyList.size() < 2) {
      return "";
    }
    return historyList.get(historyList.size() - 2)
        .getFragment();
  }

  public String getCurrentViewName() {
    if (historyList.isEmpty()) {
      return "";
    } else if (historyList.get(historyList.size() - 1)
        .getFragment() == null) {
      return "";
    }
    return historyList.get(historyList.size() - 1)
        .getFragment();
  }

  private boolean filter(ScreenTagHistory screenTagHistory) {
    return trackerFilter.filter(screenTagHistory.getFragment());
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
}
