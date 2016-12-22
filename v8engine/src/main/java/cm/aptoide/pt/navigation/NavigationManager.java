package cm.aptoide.pt.navigation;

import cm.aptoide.pt.model.v7.GetStoreWidgets;

public interface NavigationManager {

  void navigateUsing(GetStoreWidgets.WSWidget wsWidget, String storeTheme, String tag);

  void navigateTo(android.app.Fragment fragment);

  //
  // Builder
  //

  final class Builder {

    public static NavigationManager buildWith(android.app.Activity fragmentActivityV4) {
      return new ConcreteNavigationManager(fragmentActivityV4);
    }
  }
}
