package cm.aptoide.pt.navigation;

import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.v8engine.util.FragmentUtils;

class ConcreteNavigationManager implements NavigationManager {

  private final android.app.Activity activity;

  ConcreteNavigationManager(android.app.Activity activity) {
    this.activity = activity;
  }

  @Override
  public void navigateUsing(GetStoreWidgets.WSWidget wsWidget, String storeTheme, String tag) {
    throw new UnsupportedOperationException("android.app.Activity does not support v4 fragment");
  }

  @Override public void navigateTo(android.app.Fragment fragment) {
    FragmentUtils.replaceFragment(activity, fragment);
  }
}
