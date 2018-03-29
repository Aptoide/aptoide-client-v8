package cm.aptoide.pt.account.view.user;

import cm.aptoide.pt.home.BottomHomeFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;

public class ManageUserNavigator {

  private final FragmentNavigator navigator;
  private final String defaultStore;
  private final String defaultTheme;

  public ManageUserNavigator(FragmentNavigator navigator, String defaultStore,
      String defaultTheme) {
    this.navigator = navigator;
    this.defaultStore = defaultStore;
    this.defaultTheme = defaultTheme;
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
