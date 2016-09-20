/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 19/07/2016.
 */

package cm.aptoide.pt.v8engine;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.v8engine.activity.AptoideBaseLoaderActivity;
import cm.aptoide.pt.v8engine.analytics.StaticScreenNames;
import com.astuetz.PagerSlidingTabStrip;

public class MainActivity extends AptoideBaseLoaderActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  private Toolbar mToolbar;
  private DrawerLayout mDrawerLayout;
  private NavigationView mNavigationView;
  private ViewPager mViewPager;

  @Override public void loadExtras(Bundle extras) {

  }

  @Override public void setupViews() {
    setupNavigationView();
  }

  @Override public void setupToolbar() {
    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
      mToolbar.setLogo(R.drawable.ic_aptoide_toolbar);
      mToolbar.setNavigationIcon(R.drawable.ic_drawer);
      mToolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat.START));
    }
  }

  @Override public int getContentViewId() {
    return R.layout.activity_main;
  }

  @Override protected String getAnalyticsScreenName() {
    return StaticScreenNames.MAIN_ACTIVITY;
  }

  private void setupViewPager(GetStore getStore) {
    final PagerAdapter pagerAdapter = new StorePagerAdapter(getSupportFragmentManager(), getStore);
    mViewPager.setAdapter(pagerAdapter);

    PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
    if (pagerSlidingTabStrip != null) {
      pagerSlidingTabStrip.setViewPager(mViewPager);
    }

    finishLoading();
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mNavigationView = (NavigationView) findViewById(R.id.nav_view);
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mViewPager = (ViewPager) findViewById(R.id.pager);
  }

  @Override protected int getViewToShowAfterLoadingId() {
    return R.id.app_bar_layout;
  }

  @Override public void load(boolean created, boolean refresh, Bundle savedInstanceState) {
    GetStoreRequest.of("apps", StoreContext.home).execute(this::setupViewPager, created);
  }

  private void setupNavigationView() {
    if (mNavigationView != null) {
      mNavigationView.setNavigationItemSelectedListener(menuItem -> {

        int itemId = menuItem.getItemId();
        if (itemId == R.id.navigation_item_my_account) {
          AptoideAccountManager.openAccountManager(this, false);
        } else if (itemId == R.id.navigation_item_rollback) {
          AptoideAccountManager.updateMatureSwitch(
              !AptoideAccountManager.getUserInfo().isMatureSwitch());
          Snackbar.make(mNavigationView,
              "MatureSwitch: " + AptoideAccountManager.getUserInfo().isMatureSwitch(),
              Snackbar.LENGTH_SHORT).show();
        } else if (itemId == R.id.navigation_item_setting_scheduled_downloads) {
          Snackbar.make(mNavigationView, "Scheduled Downloads", Snackbar.LENGTH_SHORT).show();
        } else if (itemId == R.id.navigation_item_excluded_updates) {
          Snackbar.make(mNavigationView, "Excluded Updates", Snackbar.LENGTH_SHORT).show();
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
}
