package cm.aptoide.pt.account.view.store;

import android.app.Activity;
import cm.aptoide.pt.home.BottomNavigationNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.Result;

public class ManageStoreNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final BottomNavigationNavigator bottomNavigationNavigator;

  public ManageStoreNavigator(FragmentNavigator fragmentNavigator,
      BottomNavigationNavigator bottomNavigationNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.bottomNavigationNavigator = bottomNavigationNavigator;
  }

  public void goToHome() {
    bottomNavigationNavigator.navigateToHome();
  }

  public void popViewWithResult(int requestCode, boolean success) {
    fragmentNavigator.popWithResult(
        new Result(requestCode, (success ? Activity.RESULT_OK : Activity.RESULT_CANCELED), null));
  }
}
