package cm.aptoide.pt.navigation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.Event;

public interface NavigationManagerV4 {

  void navigateUsing(Event event, String storeTheme, String title, String tag,
      StoreContext storeContext);

  String navigateTo(Fragment fragment);

  /**
   * Use this method to replace the back stack and navigate to fragment.
   *
   * @param newBackStack New back stack
   * @param fragment Current fragment to display
   */
  //void navigateTo(Fragment fragment, @Nullable List<Fragment> newBackStack);

  void cleanBackStack();

  /**
   * Pops the top states off the back stack until it reaches a specific <code>fragmentTag</code>,
   * including that tag.
   * Returns true if the tag was popped, else false. Performs the operation {@link
   * FragmentManager#popBackStackImmediate()} inside of the call.
   *
   * @param fragmentTag The Fragment tag where the pop will stop (after this tag).
   */
  boolean cleanBackStackUntil(String fragmentTag);

  Fragment peekLast();

  Fragment peekFirst();

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
