package cm.aptoide.pt.navigation;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.Event;

public interface NavigationManagerV4 {

  void navigateUsing(Event event, String storeTheme, String title, String tag,
      StoreContext storeContext);

  String navigateTo(Fragment fragment);

  void cleanBackStack();

  void cleanBackStackUntil(String fragmentTag);

  Fragment peekLast();

  Fragment peekLastFromAll();

  Fragment peekFirst();

  Fragment peekFirstFromAll();

  void navigateToWithoutBackSave(Fragment fragment);

  //
  // Builder
  //

  final class Builder {

    public static NavigationManagerV4 buildWith(
        android.support.v4.app.FragmentActivity fragmentActivityV4) {
      return new ConcreteNavigationManagerV4(fragmentActivityV4);
    }
  }
}
