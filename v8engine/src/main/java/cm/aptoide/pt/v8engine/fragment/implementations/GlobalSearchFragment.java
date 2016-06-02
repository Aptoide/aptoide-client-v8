/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 02/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.SearchPagerAdapter;
import cm.aptoide.pt.v8engine.fragment.BasePagerToolbarFragment;
import cm.aptoide.pt.v8engine.util.SearchUtils;

/**
 * Created by neuro on 01-06-2016.
 */
public class GlobalSearchFragment extends BasePagerToolbarFragment {

	private String query;

	// Views
	private View subscribedButton;
	private View everywhereButton;

	public static GlobalSearchFragment newInstance(String query) {
		Bundle args = new Bundle();

		args.putString(BundleCons.QUERY, query);

		GlobalSearchFragment fragment = new GlobalSearchFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);

		subscribedButton = view.findViewById(R.id.subscribed);
		everywhereButton = view.findViewById(R.id.everywhere);

		setHasOptionsMenu(true);
	}

	@Override
	protected void setupViewPager() {
		super.setupViewPager();
		finishLoading();
	}

	@Override
	protected PagerAdapter createPagerAdapter() {
		return new SearchPagerAdapter(getChildFragmentManager(), query);
	}

	@Override
	public void setupViews() {
		super.setupViews();

		setupViewPager();
		setupButtons();
	}

	@Override
	public void setupToolbar() {
		super.setupToolbar();

		if (toolbar != null) {
			((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(query);
			((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			toolbar.setLogo(R.drawable.ic_store);
		}
	}

	private void setupButtons() {
		subscribedButton.setOnClickListener(v -> mViewPager.setCurrentItem(0));
		everywhereButton.setOnClickListener(v -> mViewPager.setCurrentItem(1));
	}

	@Override
	public int getContentViewId() {
		return R.layout.global_search_fragment;
	}

	@Override
	public void loadExtras(Bundle args) {
		super.loadExtras(args);

		query = args.getString(BundleCons.QUERY);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(BundleCons.QUERY, query);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_search, menu);

		SearchUtils.setupSearch(menu, getActivity().getSupportFragmentManager());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (savedInstanceState != null) {
			query = savedInstanceState.getString(BundleCons.QUERY);
		}
	}

	@Override
	protected int getViewToShowAfterLoadingId() {
		return R.id.search_results_layout;
	}

	@Override
	public void load(boolean refresh) {

	}

	protected static class BundleCons {

		public static final String QUERY = "query";
	}
}
