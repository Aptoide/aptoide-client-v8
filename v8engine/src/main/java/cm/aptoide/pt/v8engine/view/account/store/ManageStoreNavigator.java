package cm.aptoide.pt.v8engine.view.account.store;

import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;

public class ManageStoreNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final boolean goBackToHome;

  public ManageStoreNavigator(FragmentNavigator fragmentNavigator, boolean goBackToHome) {
    this.fragmentNavigator = fragmentNavigator;
    this.goBackToHome = goBackToHome;
  }

  public void navigate() {
    if (goBackToHome) {
      fragmentNavigator.navigateToHomeCleaningBackStack();
      return;
    }
    fragmentNavigator.popBackStack();
  }
}
