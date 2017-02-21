package cm.aptoide.pt.v8engine.interfaces;

import android.support.v4.app.Fragment;

/**
 * <p>
 * Abstraction methods to manipulate {@link android.app.Fragment Fragment} (or {@link
 * android.support.v4.app.Fragment v4.Fragment}).
 * </p>
 */
public interface FragmentShower {

  /**
   * @return Uses {@link android.support.v4.app.FragmentManager v4.FragmentManager} and returns the
   * {@link android.support.v4.app.Fragment v4.Fragment} at
   * the index position
   * {@link android.support.v4.app.FragmentManager.BackStackEntry#getBackStackEntryCount()
   * v4.FragmentManager.BackStackEntry.getBackStackEntryCount()}-1
   * in the {@link android.support.v4.app.FragmentManager.BackStackEntry}.
   */
  Fragment getLast();
}
