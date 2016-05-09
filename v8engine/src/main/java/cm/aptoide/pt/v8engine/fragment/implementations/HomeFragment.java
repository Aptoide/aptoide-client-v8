/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 09/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 09-05-2016.
 */
public class HomeFragment extends StoreFragment {

	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;

	public static HomeFragment newInstance(String storeName) {
		return newInstance(storeName, StoreContext.store);
	}

	public static HomeFragment newInstance(String storeName, StoreContext storeContext) {
		Bundle args = new Bundle();
		args.putString(BundleCons.STORE_NAME, storeName);
		args.putSerializable(BundleCons.STORE_CONTEXT, storeContext);
		HomeFragment fragment = new HomeFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected void setupViews() {
		super.setupViews();
		setupNavigationView();
	}

	private void setupNavigationView() {
		if (mNavigationView != null) {
			mNavigationView.setNavigationItemSelectedListener(menuItem -> {

				int itemId = menuItem.getItemId();
				if (itemId == R.id.navigation_item_my_account) {
					Snackbar.make(mNavigationView, "MyAccountActivity", Snackbar.LENGTH_SHORT)
							.show();
				} else if (itemId == R.id.navigation_item_rollback) {
					Snackbar.make(mNavigationView, "Rollback", Snackbar.LENGTH_SHORT).show();
				} else if (itemId == R.id.navigation_item_setting_schdwntitle) {
					Snackbar.make(mNavigationView, "Scheduled Downloads", Snackbar.LENGTH_SHORT)
							.show();
				} else if (itemId == R.id.navigation_item_excluded_updates) {
					Snackbar.make(mNavigationView, "Excluded Updates", Snackbar.LENGTH_SHORT)
							.show();
				} else if (itemId == R.id.navigation_item_settings) {
					Snackbar.make(mNavigationView, "Settings", Snackbar.LENGTH_SHORT).show();
				} else if (itemId == R.id.navigation_item_facebook) {
					Snackbar.make(mNavigationView, "Facebook", Snackbar.LENGTH_SHORT).show();
				} else if (itemId == R.id.navigation_item_twitter) {
					Snackbar.make(mNavigationView, "Twitter", Snackbar.LENGTH_SHORT).show();
				} else if (itemId == R.id.navigation_item_backup_apps) {
					Snackbar.make(mNavigationView, "Backup Apps", Snackbar.LENGTH_SHORT).show();
				} else if (itemId == R.id.send_feedback) {
					Snackbar.make(mNavigationView, "Send Feedback", Snackbar.LENGTH_SHORT).show();
				}

				return false;
			});
		}
	}

	@Override
	public int getRootViewId() {
		return R.layout.activity_main;
	}

	@Override
	protected void setupToolbar() {
		if (toolbar != null) {
			((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
			toolbar.setLogo(R.drawable.ic_aptoide_toolbar);
			toolbar.setNavigationIcon(R.drawable.ic_drawer);
			toolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat
					.START));
		}
	}

	@Override
	protected void bindViews(View view) {
		super.bindViews(view);
		mNavigationView = (NavigationView) view.findViewById(R.id.nav_view);
		mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		mDrawerLayout = null;
		mNavigationView = null;
	}
}
