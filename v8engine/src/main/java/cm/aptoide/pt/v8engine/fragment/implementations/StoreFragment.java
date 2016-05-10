/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;

import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.StorePagerAdapter;
import cm.aptoide.pt.v8engine.fragment.BaseLoaderToolbarFragment;

/**
 * Created by neuro on 06-05-2016.
 */
public class StoreFragment extends BaseLoaderToolbarFragment {

	private static final String TAG = "StoreFragment";

	private String storeName;
	private StoreContext storeContext;

	private ViewPager mViewPager;
	private GetStore getStore;

	public static StoreFragment newInstance(String storeName) {
		return newInstance(storeName, StoreContext.store);
	}

	public static StoreFragment newInstance(String storeName, StoreContext storeContext) {
		Bundle args = new Bundle();
		args.putString(BundleCons.STORE_NAME, storeName);
		args.putSerializable(BundleCons.STORE_CONTEXT, storeContext);
		StoreFragment fragment = new StoreFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected void loadBundle(Bundle args) {
		super.loadBundle(args);
		storeName = args.getString(BundleCons.STORE_NAME);
		storeContext = (StoreContext) args.get(BundleCons.STORE_CONTEXT);
	}

	@Override
	public int getRootViewId() {
		return R.layout.store_activity;
	}

	@Override
	protected int getViewToShowAfterLoadingId() {
		return R.id.app_bar_layout;
	}

	@Override
	public void load(boolean refresh) {
		if (refresh) {
			GetStoreRequest.of(storeName, storeContext).execute((getStore) -> {
				this.getStore = getStore;
				setupViewPager(getStore);
			}, (throwable) -> {
				finishLoading(throwable);
			});
		} else {
			setupViewPager(getStore);
		}
	}

	@Override
	protected void setupToolbar() {
		super.setupToolbar();
		if (toolbar != null) {
			((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(storeName);
			((AppCompatActivity) getActivity()).getSupportActionBar()
					.setDisplayHomeAsUpEnabled(true);
			toolbar.setLogo(R.drawable.ic_store);
		}
	}

	@Override
	protected void bindViews(View view) {
		super.bindViews(view);

		mViewPager = (ViewPager) view.findViewById(R.id.pager);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mViewPager = null;
	}

	private void setupViewPager(GetStore getStore) {
		final PagerAdapter pagerAdapter = new StorePagerAdapter(getChildFragmentManager(),
				getStore);
		mViewPager.setAdapter(pagerAdapter);

		PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) getView().findViewById
				(R.id.tabs);
		if (pagerSlidingTabStrip != null) {
			pagerSlidingTabStrip.setViewPager(mViewPager);
		}

		finishLoading();
	}

	protected static class BundleCons {

		public static final String STORE_NAME = "storeName";
		public static final String STORE_CONTEXT = "storeContext";
	}
}
