/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 05/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest;
import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.SearchPagerAdapter;
import cm.aptoide.pt.v8engine.fragment.BasePagerToolbarFragment;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
import cm.aptoide.pt.v8engine.util.SearchUtils;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchFragment extends BasePagerToolbarFragment {

	private String query;

	transient private boolean hasSubscribedResults;
	transient private boolean hasEverywhereResults;
	transient private boolean shouldFinishLoading = false;
	// Views
	private Button subscribedButton;
	private View everywhereButton;
	private View noSearchLayout;
	private EditText noSearchLayoutSearchQuery;
	private ImageView noSearchLayoutSearchButton;
	private String storeName;

	public static SearchFragment newInstance(String query) {
		Bundle args = new Bundle();

		args.putString(BundleCons.QUERY, query);

		SearchFragment fragment = new SearchFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public static SearchFragment newInstance(String query, String storeName) {
		Bundle args = new Bundle();

		args.putString(BundleCons.QUERY, query);
		args.putString(BundleCons.STORE_NAME, storeName);

		SearchFragment fragment = new SearchFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);

		subscribedButton = (Button) view.findViewById(R.id.subscribed);
		everywhereButton = view.findViewById(R.id.everywhere);
		noSearchLayout = view.findViewById(R.id.no_search_results_layout);
		noSearchLayoutSearchQuery = (EditText) view.findViewById(R.id.search_text);
		noSearchLayoutSearchButton = (ImageView) view.findViewById(R.id.ic_search_button);

		setHasOptionsMenu(true);
	}

	@Override
	protected void setupViewPager() {
		if (hasSubscribedResults || hasEverywhereResults) {
			super.setupViewPager();
		} else {
			noSearchLayout.setVisibility(View.VISIBLE);
			noSearchLayoutSearchButton.setOnClickListener(v -> {
				String s = noSearchLayoutSearchQuery.getText().toString();

				if (s.length() > 1) {
					FragmentUtils.replaceFragmentV4(((FragmentActivity) getContext()), SearchFragment.newInstance(s,
							storeName));
				}
			});
		}
	}

	@Override
	protected PagerAdapter createPagerAdapter() {
		if (storeName != null) {
			return new SearchPagerAdapter(getChildFragmentManager(), query, storeName);
		} else {
			return new SearchPagerAdapter(getChildFragmentManager(), query, hasSubscribedResults, hasEverywhereResults);
		}
	}

	private void setupButtonVisibility() {
		if (storeName != null) {
			subscribedButton.setText(storeName);
			subscribedButton.setVisibility(View.VISIBLE);
		} else {
			if (hasSubscribedResults) {
				subscribedButton.setVisibility(View.VISIBLE);
			}
			if (hasEverywhereResults) {
				everywhereButton.setVisibility(View.VISIBLE);
			}
		}
	}

	private void handleFinishLoading() {

		if (!shouldFinishLoading) {
			shouldFinishLoading = true;
		} else {
			setupButtonVisibility();
			setupButtonsListeners();
			setupViewPager();
			finishLoading();
		}
	}

	private void executeSearchRequests() {

		if (storeName != null) {
			shouldFinishLoading = true;
			ListSearchAppsRequest of = ListSearchAppsRequest.of(query, storeName);
			of.execute(listSearchApps -> {
				List<ListSearchApps.SearchAppsApp> list = listSearchApps.getDatalist().getList();

				if (list != null && list.size() > 0) {
					hasSubscribedResults = true;
					handleFinishLoading();
				} else {
					hasSubscribedResults = false;
					handleFinishLoading();
				}
			}, e -> finishLoading());
		} else {
			ListSearchAppsRequest.of(query, true).execute(listSearchApps -> {
				List<ListSearchApps.SearchAppsApp> list = listSearchApps.getDatalist().getList();

				if (list != null && list.size() > 0) {
					hasSubscribedResults = true;
					handleFinishLoading();
				} else {
					hasSubscribedResults = false;
					handleFinishLoading();
				}
			}, e -> finishLoading());

			// Other stores
			ListSearchAppsRequest.of(query, false).execute(listSearchApps -> {
				List<ListSearchApps.SearchAppsApp> list = listSearchApps.getDatalist().getList();

				if (list != null && list.size() > 0) {
					hasEverywhereResults = true;
					handleFinishLoading();
				} else {
					hasEverywhereResults = false;
					handleFinishLoading();
				}
			}, e -> finishLoading());
		}
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

	private void setupButtonsListeners() {
		if (hasSubscribedResults) {
			subscribedButton.setOnClickListener(v -> mViewPager.setCurrentItem(0));
		}

		if (hasEverywhereResults) {
			everywhereButton.setOnClickListener(v -> mViewPager.setCurrentItem(1));
		}
	}

	@Override
	public int getContentViewId() {
		return R.layout.global_search_fragment;
	}

	@Override
	public void loadExtras(Bundle args) {
		super.loadExtras(args);

		query = args.getString(BundleCons.QUERY);
		storeName = args.getString(BundleCons.STORE_NAME);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(BundleCons.QUERY, query);
		outState.putString(BundleCons.STORE_NAME, storeName);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_search, menu);

		if (storeName != null) {
			SearchUtils.setupInsideStoreSearchView(menu, getActivity(), storeName);
		} else {
			SearchUtils.setupGlobalSearchView(menu, getActivity());
		}
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (savedInstanceState != null) {
			query = savedInstanceState.getString(BundleCons.QUERY);
			storeName = savedInstanceState.getString(BundleCons.STORE_NAME);
		}
	}

	@Override
	protected int getViewToShowAfterLoadingId() {
		return R.id.search_results_layout;
	}

	@Override
	public void load(boolean refresh) {
		if (refresh) {
			executeSearchRequests();
		} else {
			handleFinishLoading();
		}
	}

	protected static class BundleCons {

		public static final String QUERY = "query";
		public static final String STORE_NAME = "storeName";
	}
}
