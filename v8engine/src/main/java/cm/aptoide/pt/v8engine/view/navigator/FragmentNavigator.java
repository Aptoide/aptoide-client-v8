package cm.aptoide.pt.v8engine.view.navigator;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.V8Engine;

public class FragmentNavigator {

  private static final String TAG = FragmentNavigator.class.getName();

  private final FragmentManager fragmentManager;
  private final int containerId;
  private final int exitAnimation;
  private final int enterAnimation;

  public FragmentNavigator(FragmentManager fragmentManager, @IdRes int containerId,
      int enterAnimation, int exitAnimation) {
    this.fragmentManager = fragmentManager;
    this.containerId = containerId;
    this.enterAnimation = enterAnimation;
    this.exitAnimation = exitAnimation;
  }

  public void navigateUsing(Event event, String storeTheme, String title, String tag,
      StoreContext storeContext) {
    Fragment fragment;

    // TODO: 22/12/2016 sithengineer refactor this using the rules present in "StoreTabGridRecyclerFragment.java"
    if (event.getName() == Event.Name.listComments) {
      String action = event.getAction();
      String url = action != null ? action.replace(V7.BASE_HOST, "") : null;

      fragment =
          V8Engine.getFragmentProvider().newCommentGridRecyclerFragmentUrl(CommentType.STORE, url);
    } else {
      fragment = V8Engine.getFragmentProvider()
          .newStoreTabGridRecyclerFragment(event, title, storeTheme, tag, storeContext);
    }

    navigateTo(fragment);
  }

  public String navigateTo(Fragment fragment) {
    // add current fragment
    String tag = Integer.toString(fragmentManager.getBackStackEntryCount());
    fragmentManager.beginTransaction()
        .setCustomAnimations(enterAnimation, exitAnimation, enterAnimation, exitAnimation)
        .addToBackStack(tag)
        .replace(containerId, fragment, tag)
        .commit();

    return tag;
  }

  public void cleanBackStack() {
    for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
      fragmentManager.popBackStack();
    }
    fragmentManager.executePendingTransactions();
  }

  /**
   * @inheritDoc - doc in the interface ^
   */
  public boolean cleanBackStackUntil(String fragmentTag) {
    if (fragmentManager.getBackStackEntryCount() == 0) {
      return false;
    }

    boolean popped = false;

    while (fragmentManager.getBackStackEntryCount() > 0 && !popped) {
      if (fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1)
          .getName()
          .equals(fragmentTag)) {
        popped = true;
      }
      fragmentManager.popBackStackImmediate();
    }
    return popped;
  }

  public Fragment peekLast() {
    if (fragmentManager.getBackStackEntryCount() > 0) {
      FragmentManager.BackStackEntry backStackEntry =
          fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
      return fragmentManager.findFragmentByTag(backStackEntry.getName());
    }

    return null;
  }

  public void navigateToWithoutBackSave(Fragment fragment) {
    fragmentManager.beginTransaction()
        .setCustomAnimations(enterAnimation, exitAnimation, enterAnimation, exitAnimation)
        .replace(containerId, fragment)
        .commit();
  }
}
