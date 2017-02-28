package cm.aptoide.pt.navigation;

import android.app.Fragment;
import android.support.annotation.Nullable;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import java.util.List;

public interface NavigationManager {

  void navigateUsing(GetStoreWidgets.WSWidget wsWidget, String storeTheme, String tag);

  void navigateTo(android.app.Fragment fragment);

  /**
   * Use this method to replace the back stack and navigate to fragment.
   *
   * @param newBackStack New back stack
   * @param fragment Current fragment to display
   */
  void navigateTo(Fragment fragment, @Nullable List<Fragment> newBackStack);

  void cleanBackStack();

  //
  // Builder
  //

  final class Builder {

    public static NavigationManager buildWith(android.app.Activity fragmentActivityV4) {
      return new ConcreteNavigationManager(fragmentActivityV4);
    }
  }
}
