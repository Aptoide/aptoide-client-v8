/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;

import java.util.LinkedList;

import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest;
import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragmentWithDecorator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SearchDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.EndlessRecyclerOnScrollListener;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchPagerTabFragment extends GridRecyclerFragmentWithDecorator {

	private String query;
	private boolean subscribedStores;

	private transient ListSearchAppsRequest listSearchAppsRequest;
	private SuccessRequestListener<ListSearchApps> listSearchAppsSuccessRequestListener = listSearchApps -> {

		LinkedList<Displayable> displayables = new LinkedList<>();

		for (ListSearchApps.SearchAppsApp searchAppsApp : listSearchApps.getDatalist().getList()) {
			displayables.add(new SearchDisplayable(searchAppsApp));
		}

		addDisplayables(displayables);
	};

	public static SearchPagerTabFragment newInstance(String query, boolean subscribedStores) {
		Bundle args = new Bundle();

		args.putString(BundleCons.QUERY, query);
		args.putBoolean(BundleCons.SUBSCRIBED_STORES, subscribedStores);

		SearchPagerTabFragment fragment = new SearchPagerTabFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void load(boolean refresh) {
		recyclerView.clearOnScrollListeners();
		recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this, listSearchAppsRequest =
				ListSearchAppsRequest
				.of(query, subscribedStores, refresh), listSearchAppsSuccessRequestListener, errorRequestListener));
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
