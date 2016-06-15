/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/06/2016.
 */

package cm.aptoide.pt.v8engine.interfaces;

/**
 * Created by sithengineer on 12/05/16.
 */
public interface FragmentShower {
	void pushFragment(android.app.Fragment fragment);
	void pushFragmentV4(android.support.v4.app.Fragment fragment);

	void popFragment();
}
