/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 05/05/2016.
 */

package cm.aptoide.pt.v8engine.fragments.implementations;

import java.util.ArrayList;
import java.util.List;

import cm.aptoide.pt.model.v7.store.GetStoreWidgets;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.fragments.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayableLoader;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 02/05/16.
 */
public class StoreGridRecyclerFragment extends GridRecyclerFragment {

	private List<Store> stores;

	public static StoreGridRecyclerFragment newInstance() {
		return new StoreGridRecyclerFragment();
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
		DisplayablePojo<Store> d = (DisplayablePojo<Store>) DisplayableLoader.INSTANCE
				.newDisplayable(GetStoreWidgets.Type.STORES_GROUP);
		d.setPojo(store);
		return d;
	}

	@Override
	public void load() {

	}
}
