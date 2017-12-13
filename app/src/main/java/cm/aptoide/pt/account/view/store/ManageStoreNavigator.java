package cm.aptoide.pt.account.view.store;

import android.app.Activity;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.Result;
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

  public void popViewWithResult(int requestCode, boolean success) {
    fragmentNavigator.popWithResult(
        new Result(requestCode, (success ? Activity.RESULT_OK : Activity.RESULT_CANCELED), null));
  }
}
