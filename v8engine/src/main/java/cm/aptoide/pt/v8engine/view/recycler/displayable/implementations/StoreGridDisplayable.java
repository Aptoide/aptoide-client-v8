/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations;

import cm.aptoide.pt.model.v7.store.GetStoreWidgets;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 29/04/16.
 */
public class StoreGridDisplayable extends DisplayablePojo<Store> {

	public StoreGridDisplayable() {
	}

	@Override
	public GetStoreWidgets.Type getName() {
		return GetStoreWidgets.Type.STORES_GROUP;
	}

	@Override
	public int getViewLayout() {
		return R.layout.store_grid_displayable_layout;
	}

	@Override
	public int getDefaultPerLineCount() {
		return 2;
	}
}
