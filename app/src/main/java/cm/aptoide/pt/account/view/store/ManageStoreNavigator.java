package cm.aptoide.pt.account.view.store;

import cm.aptoide.pt.view.navigator.FragmentNavigator;
import cm.aptoide.pt.store.view.home.HomeFragment;

public class ManageStoreNavigator {

  private final FragmentNavigator fragmentNavigator;
  private String defaultStore;
  private String defaultTheme;

  public ManageStoreNavigator(FragmentNavigator fragmentNavigator, String defaultStore,
      String defaultTheme) {
    this.fragmentNavigator = fragmentNavigator;
    this.defaultStore = defaultStore;
    this.defaultTheme = defaultTheme;
  }

  public void goBack() {
    fragmentNavigator.popBackStack();
  }

  public void goToHome() {
    fragmentNavigator.navigateToCleaningBackStack(
        HomeFragment.newInstance(defaultStore, defaultTheme), true);
  }
}
