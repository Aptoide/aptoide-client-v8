/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 15/07/2016.
 */

package cm.aptoide.pt.v8engine.interfaces;

/**
 * Created by sithengineer on 12/05/16.
 */
public interface FragmentShower {
	void pushFragment(android.app.Fragment fragment);
	void pushFragmentV4(android.support.v4.app.Fragment fragment);

	void popFragment();

	android.support.v4.app.Fragment getCurrentV4();

	android.app.Fragment getCurrent();

	android.support.v4.app.Fragment getLastV4();

	android.app.Fragment getLast();
}
