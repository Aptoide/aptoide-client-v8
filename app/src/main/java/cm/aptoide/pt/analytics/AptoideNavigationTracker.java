package cm.aptoide.pt.analytics;

import cm.aptoide.pt.account.view.LoginSignUpCredentialsFragment;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.search.view.SearchFragment;
import cm.aptoide.pt.view.store.GetStoreFragment;
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
      viewList.add(viewName.equals(GetStoreFragment.class.getSimpleName())
          ? HomeFragment.class.getSimpleName() : viewName);
      Logger.d(this.getClass()
          .getName(), "View is: " + getCurrentViewName());
    }
  }

  @Override public String getPreviousViewName() {
    if (viewList.isEmpty()) {
      return "";
    }
    return viewList.get(viewList.size() - 1);
  }

  @Override public String getCurrentViewName() {
    if (viewList.isEmpty()) {
      return "";
    }
    return viewList.get(viewList.size() - 1);
  }

  private boolean filter(String viewName) {
    if (viewName.equals(WizardFragment.class.getSimpleName())) {
      insert = false;
    } else if (viewName.equals(LoginSignUpCredentialsFragment.class.getSimpleName())) {
      insert = false;
    } else if (viewName.equals(SearchFragment.class.getSimpleName())) {
      insert = false;
    } else {
      insert = true;
    }
    return insert;
  }
}
