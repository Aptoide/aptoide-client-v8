package cm.aptoide.pt.v8engine.interfaces;

/**
 * <p>
 * Abstraction methods to manipulate {@link android.app.Fragment Fragment} (or {@link
 * android.support.v4.app.Fragment v4.Fragment}).
 * </p>
 */
public interface FragmentShower {

  //
  // methods for legacy v4 fragments (android.support.v4.app.Fragment)
  //

  /**
   * Pushes the passed {@link android.support.v4.app.Fragment v4.Fragment} using the implementing
   * activity {@link android.support.v4.app.FragmentManager
   * v4.FragmentManager}
   *
   * @param fragment {@link android.support.v4.app.Fragment v4.Fragment} to push
   */
  void pushFragmentV4(android.support.v4.app.Fragment fragment);

  /**
   * @return Uses {@link android.support.v4.app.FragmentManager v4.FragmentManager} and returns the
   * {@link android.support.v4.app.Fragment v4.Fragment} at
   * the index position
   * {@link android.support.v4.app.FragmentManager.BackStackEntry#getBackStackEntryCount()
   * v4.FragmentManager.BackStackEntry.getBackStackEntryCount()}-1
   * in the {@link android.support.v4.app.FragmentManager.BackStackEntry}.
   */
  android.support.v4.app.Fragment getLastV4();

  /**
   * @return Uses {@link android.app.FragmentManager FragmentManager} and returns the {@link
   * android.app.Fragment Fragment} at them index position 0 in the
   * {@link android.app.FragmentManager.BackStackEntry}.
   */
  android.app.Fragment getCurrent();
}
