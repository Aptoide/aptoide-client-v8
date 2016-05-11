/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 11/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import java.util.LinkedList;

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

		Store pojo = new Store();
		pojo.setAppearance(new Store.Appearance("default", "void"));
		pojo.setName("apps");
		pojo.setAvatar("http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar"
				+ ".jpg");

		SubscribedStoreDisplayable subscribedStoreDisplayable = new SubscribedStoreDisplayable
				(pojo);

		displayables.add(subscribedStoreDisplayable);

		// Add the final row as a button
		displayables.add(new AddMoreStoresDisplayable());

		setDisplayables(displayables);

		finishLoading();
	}
}
