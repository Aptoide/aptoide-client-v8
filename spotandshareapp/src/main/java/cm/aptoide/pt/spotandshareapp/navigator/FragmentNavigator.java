package cm.aptoide.pt.spotandshareapp.navigator;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by filipe on 08-06-2017.
 */

public class FragmentNavigator {

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

  public void popBackStack() {
    fragmentManager.popBackStack();
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

  public Fragment getFragment() {
    return fragmentManager.findFragmentById(containerId);
  }

  public void navigateToWithoutBackSave(Fragment fragment) {
    fragmentManager.beginTransaction()
        .setCustomAnimations(enterAnimation, exitAnimation, enterAnimation, exitAnimation)
        .replace(containerId, fragment)
        .commit();
  }
}
