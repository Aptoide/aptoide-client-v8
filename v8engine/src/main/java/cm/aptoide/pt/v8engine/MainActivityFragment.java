/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/06/2016.
 */

package cm.aptoide.pt.v8engine;

import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.v8engine.activities.AptoideSimpleFragmentActivity;
import cm.aptoide.pt.v8engine.fragment.implementations.HomeFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.util.FragmentUtils;

/**
 * Created by neuro on 06-05-2016.
 */
public class MainActivityFragment extends AptoideSimpleFragmentActivity implements FragmentShower {

	@Override
	protected android.support.v4.app.Fragment createFragment() {
		return HomeFragment.newInstance(V8Engine.getConfiguration().getDefaultStore(), StoreContext.home);
	}

	@Override
	public void pushFragment(android.app.Fragment fragment) {
		requestAccessToExternalFileSystem( () ->
				FragmentUtils.replaceFragment(this, fragment)
		);
	}

	@Override
	public void pushFragmentV4(android.support.v4.app.Fragment fragment) {
		requestAccessToExternalFileSystem( () ->
				FragmentUtils.replaceFragmentV4(this, fragment)
		);
	}

	@Override
	public void popFragment() {
		// TODO
	}
}
