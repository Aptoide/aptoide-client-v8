package cm.aptoide.pt.v8engine.view.account.user;

import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;

class ManageUserNavigator {

  private final FragmentNavigator navigator;

  public ManageUserNavigator(FragmentNavigator navigator) {
    this.navigator = navigator;
  }

  private void toProfileStepOne() {
    navigator.cleanBackStack();
    navigator.navigateTo(ProfileStepOneFragment.newInstance());
  }

  private void toHome() {
    navigator.navigateToHomeCleaningBackStack();
  }

  public void back() {
    navigator.popBackStack();
  }

  public void navigateAway(boolean isEditProfile) {
    final boolean showPrivacyConfigs = Application.getConfiguration()
        .isCreateStoreAndSetUserPrivacyAvailable();
    if (isEditProfile) {
      back();
    } else if (showPrivacyConfigs) {
      toProfileStepOne();
    } else {
      toHome();
    }
  }
}
