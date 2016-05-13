/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 12/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import java.util.LinkedList;

import cm.aptoide.pt.dataprovider.ws.v7.listapps.StoreUtils;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid
		.AddMoreStoresDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid
		.SubscribedStoreDisplayable;

/**
 * Created by neuro on 11-05-2016.
 */
public class SubscribedStoresFragment extends GridRecyclerFragment {

	public static SubscribedStoresFragment newInstance() {
		SubscribedStoresFragment fragment = new SubscribedStoresFragment();
		return fragment;
	}

	@Override
	public void load(boolean refresh) {
		// todo: dummy
		LinkedList<Displayable> displayables = new LinkedList<>();

		Store store = StoreUtils.getSubscribedStores().get(0);

		SubscribedStoreDisplayable subscribedStoreDisplayable = new SubscribedStoreDisplayable
				(store);

		displayables.add(subscribedStoreDisplayable);

		// Add the final row as a button
		displayables.add(new AddMoreStoresDisplayable());

		setDisplayables(displayables);

		finishLoading();
	}
}
