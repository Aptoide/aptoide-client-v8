/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.store.GetStoreWidgets;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 29/04/16.
 */
public class GridStoreDisplayable extends DisplayablePojo<Store> {

	public GridStoreDisplayable() {
	}

	@Override
	public GetStoreWidgets.Type getType() {
		return GetStoreWidgets.Type.STORES_GROUP;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_grid_store;
	}

	@Override
	public int getDefaultPerLineCount() {
		return 2;
	}
}
