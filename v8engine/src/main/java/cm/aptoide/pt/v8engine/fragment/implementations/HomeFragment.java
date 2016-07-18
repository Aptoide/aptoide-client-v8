/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 15/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.content.Intent;
import android.net.Uri;
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
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.util.SearchUtils;
import cm.aptoide.pt.v8engine.view.BadgeView;

/**
 * Created by neuro on 09-05-2016.
 */
public class HomeFragment extends StoreFragment {

	public static final String APTOIDE_FACEBOOK_LINK = "http://www.facebook.com/aptoide";
	public static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
	public static final String TWITTER_PACKAGE_NAME = "com.twitter.android";
	public static final String APTOIDE_TWITTER_URL = "http://www.twitter.com/aptoide";
	private static final String TAG = HomeFragment.class.getSimpleName();
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
					((FragmentShower) getActivity()).pushFragmentV4(AppViewFragment.newInstance(19067731));
				} else if (itemId == R.id.navigation_item_excluded_updates) {
					((FragmentShower) getActivity()).pushFragmentV4(ExcludedUpdatesFragment.newInstance());
				} else if (itemId == R.id.navigation_item_settings) {
					((FragmentShower) getActivity()).pushFragmentV4(SettingsFragment.newInstance());
				} else if (itemId == R.id.navigation_item_facebook) {
					openFacebook();
				} else if (itemId == R.id.navigation_item_twitter) {
					openTwitter();
				} else if (itemId == R.id.navigation_item_backup_apps) {
					Snackbar.make(mNavigationView, "Backup Apps", Snackbar.LENGTH_SHORT).show();
				} else if (itemId == R.id.send_feedback) {
					startFeedbackFragment();
				}

				mDrawerLayout.closeDrawer(mNavigationView);

				return false;
			});
		}
	}

	private void startFeedbackFragment() {
		String downloadFolderPath = Application.getConfiguration().getCachePath();
		String screenshotFileName = getActivity().getClass().getSimpleName() + ".jpg";
		AptoideUtils.ScreenU.takeScreenshot(getActivity(), downloadFolderPath, screenshotFileName);
		((FragmentShower) getActivity()).pushFragmentV4(SendFeedbackFragment.newInstance(downloadFolderPath + screenshotFileName));
	}

	private void openTwitter() {
		openSocialLink(TWITTER_PACKAGE_NAME, APTOIDE_TWITTER_URL, getContext().getString(R.string.social_twitter_screen_title), Uri.parse
				(APTOIDE_TWITTER_URL));
	}

	private void openFacebook() {
		Installed installedFacebook = Database.InstalledQ.get(FACEBOOK_PACKAGE_NAME, realm);
		openSocialLink(FACEBOOK_PACKAGE_NAME, APTOIDE_FACEBOOK_LINK, getContext().getString(R.string.social_facebook_screen_title), Uri.parse(AptoideUtils
				.SocialLinksU
				.getFacebookPageURL(installedFacebook == null ? 0 : installedFacebook.getVersionCode(), APTOIDE_FACEBOOK_LINK)));
	}

	private void openSocialLink(String packageName, String socialUrl, String pageTitle, Uri uriToOpenApp) {
		Installed installedFacebook = Database.InstalledQ.get(packageName, realm);
		if (installedFacebook == null) {
			((FragmentShower) getActivity()).pushFragmentV4(SocialFragment.newInstance(socialUrl, pageTitle));
		} else {
			Intent sharingIntent = new Intent(Intent.ACTION_VIEW, uriToOpenApp);
			getContext().startActivity(sharingIntent);
		}
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

	@Override
	public int getContentViewId() {
		return R.layout.activity_main;
	}

	@Override
	protected void setupViewPager() {
		super.setupViewPager();

		updatesBadge = new BadgeView(getContext(), ((LinearLayout) pagerSlidingTabStrip.getChildAt(0)).getChildAt(3));

		Database.UpdatesQ.getAll(realm, false).asObservable().compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(updates -> {
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
	public void setupToolbar() {
		if (toolbar != null) {
			((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
			toolbar.setLogo(R.drawable.ic_aptoide_toolbar);
			toolbar.setNavigationIcon(R.drawable.ic_drawer);
			toolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat.START));
		}
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
