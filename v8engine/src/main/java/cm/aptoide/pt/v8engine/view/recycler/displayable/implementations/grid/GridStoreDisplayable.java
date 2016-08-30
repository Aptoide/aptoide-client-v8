/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 09/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 29/04/16.
 */
public class GridStoreDisplayable extends DisplayablePojo<Store> {

	public GridStoreDisplayable(Store pojo) {
		super(pojo);
	}

	public GridStoreDisplayable() {
	}

	@Override
	public Type getType() {
		return Type.STORES_GROUP;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_grid_store;
	}
}
