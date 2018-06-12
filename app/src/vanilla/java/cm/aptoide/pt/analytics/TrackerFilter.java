package cm.aptoide.pt.analytics;

import cm.aptoide.analytics.implementation.navigation.ViewNameFilter;
import cm.aptoide.pt.account.view.LoginSignUpCredentialsFragment;
import cm.aptoide.pt.store.view.StoreFragment;
import cm.aptoide.pt.view.wizard.WizardFragment;

public class TrackerFilter implements ViewNameFilter {

  @Override public boolean filter(String viewName) {
    if (viewName.equals(WizardFragment.class.getSimpleName())) {
      return false;
    } else if (viewName.equals(LoginSignUpCredentialsFragment.class.getSimpleName())) {
      return false;
    } else if (viewName.equals(StoreFragment.class.getSimpleName())) {
      return false;
    } else {
      return true;
    }
  }
}
