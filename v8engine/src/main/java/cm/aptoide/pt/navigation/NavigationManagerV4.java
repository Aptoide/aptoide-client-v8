package cm.aptoide.pt.navigation;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.Event;
import java.util.List;

public interface NavigationManagerV4 {

  void navigateUsing(Event event, String storeTheme, String title, String tag,
      StoreContext storeContext);

  void navigateTo(android.support.v4.app.Fragment fragment);

  /**
   * Use this method to replace the back stack and navigate to fragment.
   *
   * @param newBackStack New back stack
   * @param fragment Current fragment to display
   */
  void navigateTo(Fragment fragment, @Nullable List<Fragment> newBackStack);

  void cleanBackStack();

  Fragment peekLast();

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
