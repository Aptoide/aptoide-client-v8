/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;

import java.util.LinkedList;

import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest;
import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragmentWithDecorator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SearchDisplayable;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchPagerFragment extends GridRecyclerFragmentWithDecorator {

	private String query;
	private boolean subscribedStores;

	public static SearchPagerFragment newInstance(String query, boolean subscribedStores) {
		Bundle args = new Bundle();

		args.putString(BundleCons.QUERY, query);
		args.putBoolean(BundleCons.SUBSCRIBED_STORES, subscribedStores);

		SearchPagerFragment fragment = new SearchPagerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void load(boolean refresh) {
		ListSearchAppsRequest.of(query, subscribedStores).execute(listSearchApps -> {
			LinkedList<Displayable> displayables = new LinkedList<>();

			for (ListSearchApps.SearchAppsApp searchAppsApp : listSearchApps.getDatalist().getList()) {
				displayables.add(new SearchDisplayable(searchAppsApp));
			}

			setDisplayables(displayables);
		}, e -> finishLoading());
	}

	@Override
	public void loadExtras(Bundle args) {
		super.loadExtras(args);

		query = args.getString(BundleCons.QUERY);
		subscribedStores = args.getBoolean(BundleCons.SUBSCRIBED_STORES);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(BundleCons.QUERY, query);
		outState.putBoolean(BundleCons.SUBSCRIBED_STORES, subscribedStores);
	}

	@Override
	public int getContentViewId() {
		return R.layout.recycler_fragment;
	}

	protected static class BundleCons {

		public static final String QUERY = "query";
		public static final String SUBSCRIBED_STORES = "subscribedStores";
	}
}
