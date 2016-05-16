/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 11/05/2016.
 */

package cm.aptoide.pt.v8engine;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.v8engine.activity.AptoideBaseActivity;
import cm.aptoide.pt.v8engine.analytics.StaticScreenNames;
import cm.aptoide.pt.v8engine.fragment.deprecated.StoreGridRecyclerFragmentSith;

/**
 * Created by sithengineer on 02/05/16.
 */
// FIXME delete this class. for tests only.
@Deprecated
public class MainActivityStoreList extends AptoideBaseActivity {

	private Toolbar mToolbar;
	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;
	private FrameLayout mFragmentPlaceholder;

	private StoreGridRecyclerFragmentSith fragment;

	@Override
	public void bindViews(View view) {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mNavigationView = (NavigationView) findViewById(R.id.nav_view);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mFragmentPlaceholder = (FrameLayout) findViewById(R.id.fragment_placeholder);
	}

	@Override
	public void loadExtras(Bundle extras) {

	}

	@Override
	public void setupViews() {
		ListStoresRequest.of(false).execute(this::setupStoreList);
	}

	@Override
	public void setupToolbar() {
		if (mToolbar != null) {
			setSupportActionBar(mToolbar);
			mToolbar.setLogo(R.drawable.ic_aptoide_toolbar);
			mToolbar.setNavigationIcon(R.drawable.ic_drawer);
			mToolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat
					.START));
		}
	}

	@Override
	public int getContentViewId() {
		return R.layout.activity_main_store_list;
	}

	@Override
	protected String getAnalyticsScreenName() {
		return StaticScreenNames.MAIN_ACTIVITY;
	}

	private void setupStoreList(ListStores listStores) {
		fragment = StoreGridRecyclerFragmentSith.newInstance();

		fragment.setStoreList(listStores.getDatalist().getList());

		getSupportFragmentManager().beginTransaction()
				.replace(mFragmentPlaceholder.getId(), fragment)
				.commit();
	}
}
