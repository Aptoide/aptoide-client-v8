/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 28/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import com.trello.rxlifecycle.FragmentEvent;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.util.SearchUtils;
import cm.aptoide.pt.v8engine.view.BadgeView;

/**
 * Created by neuro on 09-05-2016.
 */
public class HomeFragment extends StoreFragment {

	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;
	private BadgeView updatesBadge;

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

	private void setupNavigationView() {
		if (mNavigationView != null) {
			mNavigationView.setNavigationItemSelectedListener(menuItem -> {

				int itemId = menuItem.getItemId();
				if (itemId == R.id.navigation_item_my_account) {
					AptoideAccountManager.openAccountManager(getContext());
				} else if (itemId == R.id.navigation_item_rollback) {
					((FragmentShower) getActivity()).pushFragmentV4(RollbackFragment.newInstance());
				} else if (itemId == R.id.navigation_item_setting_schdwntitle) {
					Snackbar.make(mNavigationView, "Scheduled Downloads", Snackbar.LENGTH_SHORT)
							.show();
				} else if (itemId == R.id.navigation_item_excluded_updates) {
					((FragmentShower) getActivity()).pushFragmentV4(ExcludedUpdatesFragment.newInstance());
				} else if (itemId == R.id.navigation_item_settings) {
					((FragmentShower) getActivity()).pushFragmentV4(SettingsFragment.newInstance());
				} else if (itemId == R.id.navigation_item_facebook) {
					Snackbar.make(mNavigationView, "Facebook", Snackbar.LENGTH_SHORT).show();
				} else if (itemId == R.id.navigation_item_twitter) {
					Snackbar.make(mNavigationView, "Twitter", Snackbar.LENGTH_SHORT).show();
				} else if (itemId == R.id.navigation_item_backup_apps) {
					Snackbar.make(mNavigationView, "Backup Apps", Snackbar.LENGTH_SHORT).show();
				} else if (itemId == R.id.send_feedback) {
					Snackbar.make(mNavigationView, "Send Feedback", Snackbar.LENGTH_SHORT).show();
				}

				mDrawerLayout.closeDrawer(mNavigationView);

				return false;
			});
		}
	}

	@Override
	public int getContentViewId() {
		return R.layout.activity_main;
	}

	@Override
	public void setupToolbar() {
		if (toolbar != null) {
			((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
			toolbar.setLogo(R.drawable.ic_aptoide_toolbar);
			toolbar.setNavigationIcon(R.drawable.ic_drawer);
			toolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat.START));
		}
	}

	@Override
	protected void setupViewPager() {
		super.setupViewPager();

		updatesBadge = new BadgeView(getContext(), ((LinearLayout) pagerSlidingTabStrip.getChildAt(0)).getChildAt(3));

		Database.UpdatesQ.getAll(realm)
				.asObservable()
				.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
				.subscribe(updates -> {
					refreshUpdatesBadge(updates.size());
				});
	}

	@Override
	protected void setupSearch(Menu menu) {
		SearchUtils.setupGlobalSearchView(menu, getActivity());
	}

	@Override
	public void setupViews() {
		super.setupViews();
		setupNavigationView();
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
		mNavigationView = (NavigationView) view.findViewById(R.id.nav_view);
		mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);

		setHasOptionsMenu(true);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		mDrawerLayout = null;
		mNavigationView = null;
	}

	public void refreshUpdatesBadge(int num) {
		updatesBadge.setTextSize(11);

		if (num > 0) {
			updatesBadge.setText(String.valueOf(num));
			if (!updatesBadge.isShown()) {
				updatesBadge.show(true);
			}
		} else {
			if (updatesBadge.isShown()) {
				updatesBadge.hide(true);
			}
		}
	}
}
