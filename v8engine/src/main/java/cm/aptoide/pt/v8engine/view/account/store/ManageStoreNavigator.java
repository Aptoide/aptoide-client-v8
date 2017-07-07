package cm.aptoide.pt.v8engine.view.account.store;

import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;

public class ManageStoreNavigator {

  private final FragmentNavigator fragmentNavigator;

  public ManageStoreNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void goBack() {
    fragmentNavigator.popBackStack();
  }

  public void goToHome() {
    fragmentNavigator.navigateToHomeCleaningBackStack();
  }
}
