/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trello.rxlifecycle.FragmentEvent;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.StorePagerAdapter;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.interfaces.DrawerFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
import cm.aptoide.pt.v8engine.util.SearchUtils;
import cm.aptoide.pt.v8engine.view.BadgeView;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by neuro on 09-05-2016.
 */
public class HomeFragment extends StoreFragment implements DrawerFragment {

	public static final String APTOIDE_FACEBOOK_LINK = "http://www.facebook.com/aptoide";
	public static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
	public static final String BACKUP_APPS_PACKAGE_NAME = "pt.aptoide.backupapps";
	public static final String TWITTER_PACKAGE_NAME = "com.twitter.android";
	public static final String APTOIDE_TWITTER_URL = "http://www.twitter.com/aptoide";
	private static final String TAG = HomeFragment.class.getSimpleName();
	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;
	private BadgeView updatesBadge;
	@Getter @Setter private Event.Name desiredViewPagerItem = null;
	private ChangeTabReceiver receiver;

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
			mNavigationView.setItemIconTintList(null);
			mNavigationView.setNavigationItemSelectedListener(menuItem -> {

				int itemId = menuItem.getItemId();
				if (itemId == R.id.navigation_item_my_account) {
					AptoideAccountManager.openAccountManager(getContext());
				} else if (itemId == R.id.navigation_item_rollback) {
					((FragmentShower) getActivity()).pushFragmentV4(RollbackFragment.newInstance());
				} else if (itemId == R.id.navigation_item_setting_scheduled_downloads) {
					((FragmentShower) getActivity()).pushFragmentV4(ScheduledDownloadsFragment.newInstance());
				} else if (itemId == R.id.navigation_item_excluded_updates) {
					((FragmentShower) getActivity()).pushFragmentV4(ExcludedUpdatesFragment.newInstance());
				} else if (itemId == R.id.navigation_item_settings) {
					((FragmentShower) getActivity()).pushFragmentV4(SettingsFragment.newInstance());
				} else if (itemId == R.id.navigation_item_facebook) {
					openFacebook();
				} else if (itemId == R.id.navigation_item_twitter) {
					openTwitter();
				} else if (itemId == R.id.navigation_item_backup_apps) {
					openBackupApps();
				} else if (itemId == R.id.send_feedback) {
					startFeedbackFragment();
				}

				mDrawerLayout.closeDrawer(mNavigationView);

				return false;
			});
		}
	}

	private void openBackupApps() {
		Installed installedBackupApps = DeprecatedDatabase.InstalledQ.get(BACKUP_APPS_PACKAGE_NAME, realm);
		if(installedBackupApps == null){
			AppViewFragment.newInstance(BACKUP_APPS_PACKAGE_NAME,false);
			FragmentUtils.replaceFragmentV4(this.getActivity(),AppViewFragment.newInstance(BACKUP_APPS_PACKAGE_NAME, false));
		}
		else {
			Intent i = getContext().getPackageManager().getLaunchIntentForPackage(BACKUP_APPS_PACKAGE_NAME);
			startActivity(i);
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
		Installed installedFacebook = DeprecatedDatabase.InstalledQ.get(FACEBOOK_PACKAGE_NAME, realm);
		openSocialLink(FACEBOOK_PACKAGE_NAME, APTOIDE_FACEBOOK_LINK, getContext().getString(R.string.social_facebook_screen_title), Uri.parse(AptoideUtils
				.SocialLinksU
				.getFacebookPageURL(installedFacebook == null ? 0 : installedFacebook.getVersionCode(), APTOIDE_FACEBOOK_LINK)));
	}

	private void openSocialLink(String packageName, String socialUrl, String pageTitle, Uri uriToOpenApp) {
		Installed installedFacebook = DeprecatedDatabase.InstalledQ.get(packageName, realm);
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

		Analytics.AppViewViewedFrom.addStepToList("HOME");

		setHasOptionsMenu(true);
	}

	private void setUserDataOnHeader() {
		TextView userEmail = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.profile_email_text);
		TextView userUsername = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.profile_name_text);
		ImageView userAvatar = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.profile_image);

		if (AptoideAccountManager.isLoggedIn()) {
			userEmail.setText(AptoideAccountManager.getUserName());
			userUsername.setText(AptoideAccountManager.getUserInfo().getNickName());
			if (URLUtil.isValidUrl(AptoideAccountManager.getUserInfo().getUserAvatar())) {
				ImageLoader.load(AptoideAccountManager.getUserInfo().getUserAvatar(), userAvatar);
			} else {
				userAvatar.setImageResource(R.drawable.ic_user_icon);
			}
		} else {
			userEmail.setText("");
			userUsername.setText("");
			userAvatar.setImageResource(R.drawable.ic_user_icon);
		}
	}

	//	@Override
	//	public void onDestroyView() {
	//		super.onDestroyView();
	//
	//		mDrawerLayout = null;
	//		mNavigationView = null;
	//	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		receiver = new ChangeTabReceiver();
		getContext().registerReceiver(receiver, new IntentFilter(ChangeTabReceiver.SET_TAB_EVENT));
	}

	@Override
	public void onResume() {
		super.onResume();
		setUserDataOnHeader();
	}

	@Override
	public void onDetach() {
		getContext().unregisterReceiver(receiver);
		receiver = null;
		super.onDetach();
	}

	@Override
	public int getContentViewId() {
		return R.layout.activity_main;
	}

	@Override
	protected void setupViewPager() {
		super.setupViewPager();

		StorePagerAdapter adapter = (StorePagerAdapter) mViewPager.getAdapter();
		int count = adapter.getCount();
		for (int i = 0 ; i < count ; i++) {
			if (Event.Name.myUpdates.equals(adapter.getEventName(i))) {
				updatesBadge = new BadgeView(getContext(), ((LinearLayout) pagerSlidingTabStrip.getChildAt(0)).getChildAt(i));
				break;
			}
		}

		DeprecatedDatabase.UpdatesQ.getAll(realm, false).asObservable().compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(updates -> {
			refreshUpdatesBadge(updates.size());
		});

		if (desiredViewPagerItem != null) {
			if (adapter.containsEventName(desiredViewPagerItem)) {
				mViewPager.setCurrentItem(adapter.getEventNamePosition(desiredViewPagerItem));
			}
		}
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
		// No updates present
		if (updatesBadge == null) {
			return;
		}

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

	@Override
	public boolean isDrawerOpened() {
		return mDrawerLayout.isDrawerOpen(Gravity.LEFT);
	}

	@Override
	public void openDrawer() {
		mDrawerLayout.openDrawer(Gravity.LEFT);
	}

	@Override
	public void closeDrawer() {
		mDrawerLayout.closeDrawers();
	}

	public class ChangeTabReceiver extends BroadcastReceiver {

		public static final String SET_TAB_EVENT = "SET_TAB_EVENT";

		@Override
		public void onReceive(Context context, Intent intent) {
			Event.Name tabToChange = (Event.Name) intent.getSerializableExtra(SET_TAB_EVENT);
			if (tabToChange != null) {
				StorePagerAdapter storePagerAdapter = mViewPager.getAdapter() instanceof StorePagerAdapter ? ((StorePagerAdapter) mViewPager.getAdapter()) :
						null;
				if (storePagerAdapter != null) {
					mViewPager.setCurrentItem(((StorePagerAdapter) mViewPager.getAdapter()).getEventNamePosition(tabToChange));
				}
			}
		}
	}
}
