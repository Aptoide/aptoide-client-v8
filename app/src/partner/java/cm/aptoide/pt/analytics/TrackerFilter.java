package cm.aptoide.pt.analytics;

import cm.aptoide.pt.account.view.LoginSignUpCredentialsFragment;
import cm.aptoide.pt.store.view.StoreFragment;

public class TrackerFilter {

  public boolean filter(String viewName) {
    if (viewName.equals(LoginSignUpCredentialsFragment.class.getSimpleName())) {
      return false;
    } else if (viewName.equals(StoreFragment.class.getSimpleName())) {
      return false;
    } else {
      return true;
    }
  }
}
