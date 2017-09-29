package cm.aptoide.pt.analytics;

import android.text.TextUtils;
import cm.aptoide.pt.account.view.LoginSignUpCredentialsFragment;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.view.search.SearchPagerTabFragment;
import cm.aptoide.pt.view.store.GetStoreFragment;
import cm.aptoide.pt.view.store.GetStoreWidgetsFragment;
import cm.aptoide.pt.view.store.home.HomeFragment;
import cm.aptoide.pt.view.wizard.WizardFragment;
import java.util.List;

/**
 * Created by jdandrade on 06/09/2017.
 */

public class AptoideNavigationTracker implements NavigationTracker {

  private List<String> viewList;
  private boolean insert;

  public AptoideNavigationTracker(List<String> viewList) {
    this.viewList = viewList;
  }

  @Override public void registerView(String viewName) {
    insert = filter(viewName);
    if (insert) {
      viewList.add(checkViewname(viewName));
      Logger.d(this.getClass()
          .getName(), "View is: " + getCurrentViewName());
    }
  }

  @Override public String getPreviousViewName() {
    if (viewList.isEmpty() || viewList.size() < 2) {
      return "";
    }
    return viewList.get(viewList.size() - 2);
  }

  @Override public String getCurrentViewName() {
    if (viewList.isEmpty()) {
      return "";
    }
    return viewList.get(viewList.size() - 1);
  }

  private String checkViewname(String viewName) {
    if (viewName.equals(GetStoreFragment.class.getSimpleName()) || viewName.equals(
        GetStoreWidgetsFragment.class.getSimpleName())) {
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
    } else if (getCurrentViewName().equals(viewName)) {
      insert = false;
    } else if (getCurrentViewName().equals(checkViewname(viewName))) {
      insert = false;
    } else {
      insert = true;
    }
    return insert;
  }
}
