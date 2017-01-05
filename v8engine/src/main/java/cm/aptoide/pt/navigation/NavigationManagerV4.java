package cm.aptoide.pt.navigation;

import cm.aptoide.pt.model.v7.Event;

public interface NavigationManagerV4  {

  void navigateUsing(Event event, String storeTheme, String title, String tag);

  void navigateTo(android.support.v4.app.Fragment fragment);

  //
  // Builder
  //

  final class Builder {

    public static NavigationManagerV4 buildWith(android.support.v4.app.FragmentActivity fragmentActivityV4) {
      return new ConcreteNavigationManagerV4(fragmentActivityV4);
    }
  }
}
