/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 05/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.StoreUtils;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.StorePagerAdapter;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.dialog.PrivateStoreDialog;
import cm.aptoide.pt.v8engine.fragment.BasePagerToolbarFragment;
import cm.aptoide.pt.v8engine.util.SearchUtils;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.ThemeUtils;

/**
 * Created by neuro on 06-05-2016.
 */
public class StoreFragment extends BasePagerToolbarFragment {

	private static final String TAG = StoreFragment.class.getSimpleName();

	private final int PRIVATE_STORE_REQUEST_CODE = 20;
	protected PagerSlidingTabStrip pagerSlidingTabStrip;
	private String storeName;
	private StoreContext storeContext;
	private GetStore getStore;
	private String storeTheme;

	public static StoreFragment newInstance(String storeName, String storeTheme) {
		return newInstance(storeName, StoreContext.store, storeTheme);
	}

	//Delete after completing themes implementation
	public static StoreFragment newInstance(String storeName) {
		return newInstance(storeName, StoreContext.store);
	}

	public static StoreFragment newInstance(String storeName, StoreContext storeContext, String storeTheme) {
		Bundle args = new Bundle();
		args.putString(BundleCons.STORE_NAME, storeName);
		args.putSerializable(BundleCons.STORE_CONTEXT, storeContext);
		args.putString(BundleCons.STORE_THEME, storeTheme);
		StoreFragment fragment = new StoreFragment();
		fragment.setArguments(args);
		return fragment;
	}

	//Delete after completing themes implementation
	public static StoreFragment newInstance(String storeName, StoreContext storeContext) {
		Bundle args = new Bundle();
		args.putString(BundleCons.STORE_NAME, storeName);
		args.putSerializable(BundleCons.STORE_CONTEXT, storeContext);
		StoreFragment fragment = new StoreFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if(storeTheme != null) {
			ThemeUtils.setStoreTheme(getActivity(), storeTheme);
			ThemeUtils.setStatusBarThemeColor(getActivity(), StoreThemeEnum.get(storeTheme));
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if(storeTheme != null) {
			ThemeUtils.setAptoideTheme(getActivity());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(storeTheme != null) {
			ThemeUtils.setStatusBarThemeColor(getActivity(), StoreThemeEnum.get("default"));
		}
	}

	@Override
	public void loadExtras(Bundle args) {
		super.loadExtras(args);
		storeName = args.getString(BundleCons.STORE_NAME);
		storeContext = (StoreContext) args.get(BundleCons.STORE_CONTEXT);
		storeTheme = args.getString(BundleCons.STORE_THEME);
	}

	@Override
	public int getContentViewId() {
		return R.layout.store_activity;
	}

	@Override
	protected int getViewToShowAfterLoadingId() {
		return R.id.app_bar_layout;
	}

	@Override
	public void load(boolean refresh, Bundle savedInstanceState) {
		if (refresh) {
			GetStoreRequest.of(storeName, storeContext).execute((getStore) -> {
				this.getStore = getStore;
				setupViewPager();
			}, (throwable) -> {
				if (throwable instanceof AptoideWsV7Exception) {
					BaseV7Response baseResponse = ((AptoideWsV7Exception) throwable).getBaseResponse();

					if (StoreUtils.PRIVATE_STORE_ERROR.equals(baseResponse.getError()
							.getCode()) || StoreUtils.PRIVATE_STORE_WRONG_CREDENTIALS.equals(baseResponse.getError()
							.getCode())) {
						DialogFragment dialogFragment = PrivateStoreDialog.newInstance(this,
								PRIVATE_STORE_REQUEST_CODE, storeName);
						dialogFragment.show(getFragmentManager(), PrivateStoreDialog.TAG);
					}
				}
				else {
					finishLoading(throwable);
				}
			}, refresh);
		} else {
			setupViewPager();
		}
	}

	@Override
	protected void setupViewPager() {
		super.setupViewPager();
		pagerSlidingTabStrip = (PagerSlidingTabStrip) getView().findViewById(R.id.tabs);
		if (pagerSlidingTabStrip != null) {
			pagerSlidingTabStrip.setViewPager(mViewPager);
		}

		mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				StorePagerAdapter adapter = (StorePagerAdapter) mViewPager.getAdapter();
				if (Event.Name.getUserTimeline.equals(adapter.getEventName(position))) {
					Analytics.AppsTimeline.openTimeline();
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		finishLoading();
	}

	@Override
	protected PagerAdapter createPagerAdapter() {
		return new StorePagerAdapter(getChildFragmentManager(), getStore);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PRIVATE_STORE_REQUEST_CODE) {
			switch (resultCode) {
				case Activity.RESULT_OK:
					load(true, null);
					break;
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int i = item.getItemId();

		if (i == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_search, menu);

		setupSearch(menu);
	}

	protected void setupSearch(Menu menu) {
		SearchUtils.setupInsideStoreSearchView(menu, getActivity(), storeName);
	}

	@Override
	public void setupViews() {
		super.setupViews();

		setHasOptionsMenu(true);
	}

	@Override
	public void setupToolbar() {
		super.setupToolbar();
		if (toolbar != null) {
			((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(storeName);
			((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			toolbar.setLogo(R.drawable.ic_store);
		}
		Logger.d(TAG, "LOCALYTICS TESTING - STORES ACTION ENTER " + storeName);
		Analytics.Stores.enter(storeName);
	}

	protected static class BundleCons {

		public static final String STORE_NAME = "storeName";
		public static final String STORE_CONTEXT = "storeContext";
		public static final String STORE_THEME = "storeTheme";
	}
}
