/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 03/05/2016.
 */

package cm.aptoide.pt.aptoideclientv8;

import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;

import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.v8engine.StorePagerAdapter;
import cm.aptoide.pt.v8engine.activity.AptoideBaseScreenActivity;
import cm.aptoide.pt.v8engine.analytics.StaticScreenNames;

public class MainActivity extends AptoideBaseScreenActivity {

	private Toolbar mToolbar;
	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;
	private ViewPager mViewPager;

	@Override
	protected void setupViews() {
		setupNavigationView();

		// TODO use correct WidgetArgs
		//WidgetsArgs defaultArgs = WidgetsArgs.createDefault();
		//GetStoreRequest.of("apps", defaultArgs).execute(this::setupViewPager);

		GetStoreRequest getStoreRequest = GetStoreRequest.of("apps");
		getStoreRequest.getBody().setContext(StoreContext.home);
		getStoreRequest.execute(this::setupViewPager);
	}

	private void setupViewPager(GetStore getStore) {
		final PagerAdapter pagerAdapter = new StorePagerAdapter(getSupportFragmentManager(),
				getStore);
		mViewPager.setAdapter(pagerAdapter);

		PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(cm.aptoide
				.pt.v8engine.R.id.tabs);
		if (pagerSlidingTabStrip != null) {
			pagerSlidingTabStrip.setViewPager(mViewPager);
		}
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
		mViewPager = (ViewPager) findViewById(cm.aptoide.pt.v8engine.R.id.pager);
	}

	@Override
	protected int getContentViewId() {
		return cm.aptoide.pt.v8engine.R.layout.main_activity;
	}

	private void setupNavigationView() {
		if (mNavigationView != null) {
			mNavigationView.setNavigationItemSelectedListener(menuItem -> {

				int itemId = menuItem.getItemId();
				if (itemId == cm.aptoide.pt.v8engine.R.id.navigation_item_my_account) {
					Snackbar.make(mNavigationView, "MyAccountActivity", Snackbar.LENGTH_SHORT)
							.show();
				} else if (itemId == cm.aptoide.pt.v8engine.R.id.navigation_item_rollback) {
					Snackbar.make(mNavigationView, "Rollback", Snackbar.LENGTH_SHORT).show();
				} else if (itemId == cm.aptoide.pt.v8engine.R.id
						.navigation_item_setting_schdwntitle) {
					Snackbar.make(mNavigationView, "Scheduled Downloads", Snackbar.LENGTH_SHORT)
							.show();
				} else if (itemId == cm.aptoide.pt.v8engine.R.id
						.navigation_item_excluded_updates) {
					Snackbar.make(mNavigationView, "Excluded Updates", Snackbar.LENGTH_SHORT)
							.show();
				} else if (itemId == cm.aptoide.pt.v8engine.R.id.navigation_item_settings) {
					Snackbar.make(mNavigationView, "Settings", Snackbar.LENGTH_SHORT).show();
				} else if (itemId == cm.aptoide.pt.v8engine.R.id.navigation_item_facebook) {
					Snackbar.make(mNavigationView, "Facebook", Snackbar.LENGTH_SHORT).show();
				} else if (itemId == cm.aptoide.pt.v8engine.R.id.navigation_item_twitter) {
					Snackbar.make(mNavigationView, "Twitter", Snackbar.LENGTH_SHORT).show();
				} else if (itemId == cm.aptoide.pt.v8engine.R.id.navigation_item_backup_apps) {
					Snackbar.make(mNavigationView, "Backup Apps", Snackbar.LENGTH_SHORT).show();
				} else if (itemId == cm.aptoide.pt.v8engine.R.id.send_feedback) {
					Snackbar.make(mNavigationView, "Send Feedback", Snackbar.LENGTH_SHORT).show();
				}

				return false;
			});
		}
	}

	@Override
	protected String getAnalyticsScreenName() {
		return StaticScreenNames.MAIN_ACTIVITY;
	}
}
