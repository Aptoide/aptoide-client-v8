package cm.aptoide.pt.analytics;

import android.text.TextUtils;
import cm.aptoide.pt.account.view.LoginSignUpCredentialsFragment;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.view.search.SearchPagerTabFragment;
import cm.aptoide.pt.view.store.GetStoreFragment;
import cm.aptoide.pt.view.store.home.HomeFragment;
import cm.aptoide.pt.view.wizard.WizardFragment;
import java.util.List;

/**
 * Created by jdandrade on 06/09/2017.
 */

public class AptoideNavigationTracker implements NavigationTracker {

  private List<ScreenTagHistory> historyList;
  private boolean insert;
  private ScreenTagHistory screenHistory;

  public AptoideNavigationTracker(List<ScreenTagHistory> historyList) {
    this.historyList = historyList;
  }

  @Override public void registerView(String viewName) {
    insert = filter(viewName);
    if (insert) {
      screenHistory = getScreenToEditFragment();
      screenHistory.setFragment(checkViewName(viewName));
      historyList.add(screenHistory);
      Logger.d(this.getClass()
              .getName(),
          "VIEW - " + "View is: " + getCurrentViewName() + " - Tag is: " + getCurrentViewTag());
    }
  }

  @Override public void registerTag(String tag) {
    screenHistory = getScreenToEditTag();
    screenHistory.setTag(tag);
    historyList.add(screenHistory);
    Logger.d(this.getClass()
            .getName(),
        "TAG - " + "View is: " + getCurrentViewName() + " - Tag is: " + getCurrentViewTag());
  }

  @Override public void registerTagNewObject(String tag) {
    screenHistory = new ScreenTagHistory();
    screenHistory.setTag(tag);
    historyList.add(screenHistory);
    Logger.d(this.getClass()
            .getName(),
        "[NEW]TAG - " + "View is: " + getCurrentViewName() + " - Tag is: " + getCurrentViewTag());
  }

  @Override public ScreenTagHistory getCurrentScreen() {
    if (historyList.isEmpty()) {
      screenHistory = new ScreenTagHistory();
      historyList.add(screenHistory);
    }
    return historyList.get(historyList.size() - 1);
  }

  @Override public String getPreviousViewName() {
    if (historyList.size() < 2) {
      return "";
    }
    return historyList.get(historyList.size() - 2)
        .getFragment();
  }

  @Override public String getCurrentViewName() {
    if (historyList.isEmpty()) {
      return "";
    } else if (historyList.get(historyList.size() - 1)
        .getFragment() == null) {
      return "";
    }
    return historyList.get(historyList.size() - 1)
        .getFragment();
  }

  @Override public String getPreviousViewTag() {
    return null;
  }

  @Override public String getCurrentViewTag() {
    return getCurrentScreen().getTag();
  }

  private ScreenTagHistory getScreenToEditFragment() {
    if (historyList.isEmpty()
        || historyList.get(historyList.size() - 1)
        .getFragment() != null) {
      historyList.add(new ScreenTagHistory());
    }
    return historyList.get(historyList.size() - 1);
  }

  private ScreenTagHistory getScreenToEditTag() {
    if (historyList.isEmpty()
        || historyList.get(historyList.size() - 1)
        .getTag() != null) {
      historyList.add(new ScreenTagHistory());
    }
    return historyList.get(historyList.size() - 1);
  }

  private String checkViewName(String viewName) {
    if (viewName.equals(GetStoreFragment.class.getSimpleName())) {
      return HomeFragment.class.getSimpleName();
    } else {
      return viewName;
    }
  }

  private boolean filter(String viewName) {
    if (TextUtils.isEmpty(viewName)) {
      insert = false;
    } else if (viewName.equals(HomeFragment.class.getSimpleName())) {
      insert = false;
    } else if (viewName.equals(WizardFragment.class.getSimpleName())) {
      insert = false;
    } else if (viewName.equals(LoginSignUpCredentialsFragment.class.getSimpleName())) {
      insert = false;
    } else if (viewName.equals(SearchPagerTabFragment.class.getSimpleName())) {
      insert = false;
    } else {
      insert = true;
    }
    return insert;
  }
}
