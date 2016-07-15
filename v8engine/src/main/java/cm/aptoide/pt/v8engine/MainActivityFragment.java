/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 15/07/2016.
 */

package cm.aptoide.pt.v8engine;

import android.content.Intent;
import android.os.Bundle;

import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.v8engine.activities.AptoideSimpleFragmentActivity;
import cm.aptoide.pt.v8engine.fragment.implementations.HomeFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.services.PullingContentService;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			startService(new Intent(this, PullingContentService.class));
		}
	}

	@Override
	public void pushFragment(android.app.Fragment fragment) {
		FragmentUtils.replaceFragment(this, fragment);
	}

	@Override
	public void pushFragmentV4(android.support.v4.app.Fragment fragment) {
		FragmentUtils.replaceFragmentV4(this, fragment);
	}

	@Override
	public void popFragment() {
		onBackPressed();
	}

	public android.support.v4.app.Fragment getCurrentV4() {
		return FragmentUtils.getFirstFragmentV4(this);
	}

	public android.app.Fragment getCurrent() {
		return FragmentUtils.getFirstFragment(this);
	}

	public android.support.v4.app.Fragment getLastV4() {
		return FragmentUtils.getLastFragmentV4(this);
	}

	public android.app.Fragment getLast() {
		return FragmentUtils.getLastFragment(this);
	}
}
