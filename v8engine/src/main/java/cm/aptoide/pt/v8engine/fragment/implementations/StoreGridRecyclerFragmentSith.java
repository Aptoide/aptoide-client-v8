/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 11/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import java.util.ArrayList;
import java.util.List;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.view.recycler.DisplayableType;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 02/05/16.
 */
public class StoreGridRecyclerFragmentSith extends GridRecyclerFragment {

	private List<Store> stores;

	public static StoreGridRecyclerFragmentSith newInstance() {
		return new StoreGridRecyclerFragmentSith();
	}

	public void setStoreList(List<Store> stores) {
		this.stores = stores;
	}

	@Override
	public void onStart() {
		super.onStart();
		List<Displayable> displayables = storesToDisplayable(stores);
		addDisplayables(displayables);
	}

	public List<Displayable> storesToDisplayable(List<Store> stores) {
		List<Displayable> displayables = new ArrayList<>(stores.size());
		for (Store store : stores) {
			displayables.add(storeToDisplayable(store));
		}
		return displayables;
	}

	public Displayable storeToDisplayable(Store store) {
		DisplayablePojo<Store> d = (DisplayablePojo<Store>) DisplayableType.newDisplayable(Type
				.STORES_GROUP);
		d.setPojo(store);
		return d;
	}

	@Override
	public void load(boolean refresh) {

	}

	@Override
	protected void setupToolbar() {

	}
}
