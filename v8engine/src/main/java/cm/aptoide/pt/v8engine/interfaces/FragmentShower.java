/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 23/08/2016.
 */

package cm.aptoide.pt.v8engine.interfaces;

/**
 * Created by sithengineer on 12/05/16.
 * <p>
 *     Abstraction methods to manipulate {@link android.app.Fragment Fragment} (or {@link android.support.v4.app.Fragment v4.Fragment}).
 * </p>
 */
public interface FragmentShower {

	//
	// methods for legacy v4 fragments (android.support.v4.app.Fragment)
	//

	/**
	 * Pushes the passed {@link android.support.v4.app.Fragment v4.Fragment} using the implementing activity {@link android.support.v4.app.FragmentManager
	 * v4.FragmentManager}
	 *
	 * @param fragment {@link android.support.v4.app.Fragment v4.Fragment} to push
	 */
	void pushFragmentV4(android.support.v4.app.Fragment fragment);

	/**
	 * @return Uses {@link android.support.v4.app.FragmentManager v4.FragmentManager} and returns the {@link android.support.v4.app.Fragment v4.Fragment} at
	 * the
	 * index position 0 in the {@link android.support.v4.app.FragmentManager.BackStackEntry}.
	 */
	android.support.v4.app.Fragment getCurrentV4();

	/**
	 * @return Uses {@link android.support.v4.app.FragmentManager v4.FragmentManager} and returns the {@link android.support.v4.app.Fragment v4.Fragment} at
	 * the index position
	 * {@link android.support.v4.app.FragmentManager.BackStackEntry#getBackStackEntryCount() v4.FragmentManager.BackStackEntry.getBackStackEntryCount()}-1
	 * in the {@link android.support.v4.app.FragmentManager.BackStackEntry}.
	 */
	android.support.v4.app.Fragment getLastV4();

	//
	// methods for fragments (android.app.Fragment)
	//

	/**
	 * Pushes the passed {@link android.app.Fragment Fragment} using the implementing activity {@link android.app.FragmentManager FragmentManager}
	 *
	 * @param fragment {@link android.app.Fragment Fragment} to push
	 */
	void pushFragment(android.app.Fragment fragment);

	/**
	 * @return Uses {@link android.app.FragmentManager FragmentManager} and returns the {@link android.app.Fragment Fragment} at them index position 0 in the
	 * {@link android.app.FragmentManager.BackStackEntry}.
	 */
	android.app.Fragment getCurrent();

	/**
	 * @return Uses {@link android.app.FragmentManager FragmentManager} and returns the {@link android.app.Fragment v4.Fragment} at
	 * the index position
	 * {@link android.app.FragmentManager.BackStackEntry#getBackStackEntryCount() FragmentManager.BackStackEntry.getBackStackEntryCount()}-1
	 * in the {@link android.app.FragmentManager.BackStackEntry}.
	 */
	android.app.Fragment getLast();
}
