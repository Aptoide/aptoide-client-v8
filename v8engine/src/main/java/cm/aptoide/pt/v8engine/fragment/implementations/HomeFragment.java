/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.trello.rxlifecycle.FragmentEvent;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.model.DownloadState;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.util.SearchUtils;
import cm.aptoide.pt.v8engine.view.BadgeView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by neuro on 09-05-2016.
 */
public class HomeFragment extends StoreFragment {

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

	@Override
	public void setupViews() {
		super.setupViews();
		setupNavigationView();
	}

	private void setupNavigationView() {
		if (mNavigationView != null) {
			mNavigationView.setNavigationItemSelectedListener(menuItem -> {

				int itemId = menuItem.getItemId();
				if (itemId == R.id.navigation_item_my_account) {
					AptoideAccountManager.openAccountManager(getContext());
				} else if (itemId == R.id.navigation_item_rollback) {

					Observable<DownloadState> downloadStatus = AptoideDownloadManager.getInstance()
							.getDownloadStatus(12312);
					downloadStatus.subscribe(downloadState -> ShowMessage.show(mNavigationView,
							downloadState
							.toString()), Throwable::printStackTrace);



				} else if (itemId == R.id.navigation_item_setting_schdwntitle) {
					Observable observable = AptoideDownloadManager.getInstance()
							.startDownload("http://8ace.apk.aptoide" +
									".com/glispastore/com-fshareapps-android-10001226-18925085" +
									"-c0280b5144420856c21d861339514791.apk", 12312);
					if (observable != null) {

						final Subscription subscribe = observable.subscribe(o -> {
							Logger.d(TAG, "setupNavigationView: " + o);
						}, new Action1<Throwable>() {
							@Override
							public void call(Throwable throwable) {
								throwable.printStackTrace();
							}
						});
					}
				} else if (itemId == R.id.navigation_item_excluded_updates) {
					Snackbar.make(mNavigationView, "Excluded Updates", Snackbar.LENGTH_SHORT)
							.show();
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
			toolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat
					.START));
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_search, menu);

		SearchUtils.setupGlobalSearchView(menu, getActivity());
	}
}
