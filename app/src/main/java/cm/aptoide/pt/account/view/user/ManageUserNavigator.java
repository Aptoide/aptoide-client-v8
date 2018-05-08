package cm.aptoide.pt.account.view.user;

import cm.aptoide.pt.home.BottomNavigationNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;

public class ManageUserNavigator {

  private final FragmentNavigator navigator;
  private final BottomNavigationNavigator bottomNavigationNavigator;

  public ManageUserNavigator(FragmentNavigator navigator,
      BottomNavigationNavigator bottomNavigationNavigator) {
    this.navigator = navigator;
    this.bottomNavigationNavigator = bottomNavigationNavigator;
  }

  public void toProfileStepOne() {
    navigator.cleanBackStack();
    navigator.navigateTo(ProfileStepOneFragment.newInstance(), true);
  }

  public void goToHome() {
    bottomNavigationNavigator.navigateToHome();
  }

  public void goBack() {
    navigator.popBackStack();
  }
}
