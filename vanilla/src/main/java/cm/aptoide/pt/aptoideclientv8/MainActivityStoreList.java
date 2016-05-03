/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 03/05/2016.
 */

package cm.aptoide.pt.aptoideclientv8;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.v8engine.activity.AptoideBaseScreenActivity;
import cm.aptoide.pt.v8engine.analytics.StaticScreenNames;
import cm.aptoide.pt.v8engine.fragments.implementations.StoreGridRecyclerFragment;

/**
 * Created by sithengineer on 02/05/16.
 */
// FIXME delete this class. for tests only.
@Deprecated
public class MainActivityStoreList extends AptoideBaseScreenActivity {

	private Toolbar mToolbar;
	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;
	private FrameLayout mFragmentPlaceholder;

	private StoreGridRecyclerFragment fragment;

	@Override
	protected String getAnalyticsScreenName() {
		return StaticScreenNames.MAIN_ACTIVITY;
	}

	@Override
	protected void setupViews() {
		ListStoresRequest.of().execute(this::setupStoreList);
	}

	private void setupStoreList(ListStores listStores) {
		fragment = StoreGridRecyclerFragment.newInstance();

		fragment.setStoreList(listStores.getDatalist().getList());

		getSupportFragmentManager().beginTransaction()
				.replace(mFragmentPlaceholder.getId(), fragment)
				.commit();
	}

	@Override
	protected void setupToolbar() {
		if (mToolbar != null) {
			setSupportActionBar(mToolbar);
			mToolbar.setLogo(cm.aptoide.pt.v8engine.R.drawable.ic_aptoide_toolbar);
			mToolbar.setNavigationIcon(cm.aptoide.pt.v8engine.R.drawable.ic_drawer);
			mToolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat
					.START));
		}
	}

	@Override
	protected void bindViews() {
		mToolbar = (Toolbar) findViewById(cm.aptoide.pt.v8engine.R.id.toolbar);
		mNavigationView = (NavigationView) findViewById(cm.aptoide.pt.v8engine.R.id.nav_view);
		mDrawerLayout = (DrawerLayout) findViewById(cm.aptoide.pt.v8engine.R.id.drawer_layout);
		mFragmentPlaceholder = (FrameLayout) findViewById(cm.aptoide.pt.v8engine.R.id
				.fragment_placeholder);
	}

	@Override
	protected int getContentViewId() {
		return cm.aptoide.pt.v8engine.R.layout.main_activity_store_list;
	}
}
