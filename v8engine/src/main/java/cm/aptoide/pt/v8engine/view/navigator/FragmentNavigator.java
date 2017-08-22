package cm.aptoide.pt.v8engine.view.navigator;

import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.store.home.HomeFragment;

public class FragmentNavigator {

  private final FragmentManager fragmentManager;
  private final int containerId;
  private final int exitAnimation;
  private final int enterAnimation;
  private final SharedPreferences sharedPreferences;

  public FragmentNavigator(FragmentManager fragmentManager, @IdRes int containerId,
      int enterAnimation, int exitAnimation, SharedPreferences sharedPreferences) {
    this.fragmentManager = fragmentManager;
    this.containerId = containerId;
    this.enterAnimation = enterAnimation;
    this.exitAnimation = exitAnimation;
    this.sharedPreferences = sharedPreferences;
  }

  public void navigateUsing(Event event, String storeTheme, String title, String tag,
      StoreContext storeContext) {
    Fragment fragment;

    // TODO: 22/12/2016 refactor this using the rules present in "StoreTabGridRecyclerFragment.java"
    if (event.getName() == Event.Name.listComments) {
      String action = event.getAction();
      String url = action != null ? action.replace(V7.getHost(sharedPreferences), "") : null;

      fragment = V8Engine.getFragmentProvider()
          .newCommentGridRecyclerFragmentUrl(CommentType.STORE, url);
    } else {
      fragment = V8Engine.getFragmentProvider()
          .newStoreTabGridRecyclerFragment(event, title, storeTheme, tag, storeContext);
    }

    navigateTo(fragment);
  }

  public String navigateTo(Fragment fragment) {
    String tag = Integer.toString(fragmentManager.getBackStackEntryCount());
    prepareFragmentReplace(fragment, tag).commit();

    return tag;
  }

  public String navigateToAllowingStateLoss(Fragment fragment) {
    // add current fragment
    String tag = Integer.toString(fragmentManager.getBackStackEntryCount());
    prepareFragmentReplace(fragment, tag).commitAllowingStateLoss();

    return tag;
  }

  private FragmentTransaction prepareFragmentReplace(Fragment fragment, String tag) {
    return fragmentManager.beginTransaction()
        .setCustomAnimations(enterAnimation, exitAnimation, enterAnimation, exitAnimation)
        .addToBackStack(tag)
        .replace(containerId, fragment, tag);
  }

  /**
   * Only use this method when it is navigating to the first fragment in the activity.
   */
  public void navigateToWithoutBackSave(Fragment fragment) {
    fragmentManager.beginTransaction()
        .setCustomAnimations(enterAnimation, exitAnimation, enterAnimation, exitAnimation)
        .replace(containerId, fragment)
        .commit();
  }

  public void navigateToHomeCleaningBackStack() {
    Fragment home = HomeFragment.newInstance();
    cleanBackStack();
    navigateToWithoutBackSave(home);
  }

  public boolean popBackStack() {
    return fragmentManager.popBackStackImmediate();
  }

  public void cleanBackStack() {
    for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
      fragmentManager.popBackStack();
    }
    fragmentManager.executePendingTransactions();
  }

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

  public Fragment getFragment() {
    return fragmentManager.findFragmentById(containerId);
  }
}
