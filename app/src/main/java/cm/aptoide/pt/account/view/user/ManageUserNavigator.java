package cm.aptoide.pt.account.view.user;

import cm.aptoide.pt.home.BottomHomeFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;

public class ManageUserNavigator {

  private final FragmentNavigator navigator;

  public ManageUserNavigator(FragmentNavigator navigator) {
    this.navigator = navigator;
  }

  public void toProfileStepOne() {
    navigator.cleanBackStack();
    navigator.navigateTo(ProfileStepOneFragment.newInstance(), true);
  }

  public void goToHome() {
    navigator.navigateToCleaningBackStack(new BottomHomeFragment(),
        true);
  }

  public void goBack() {
    navigator.popBackStack();
  }
}
