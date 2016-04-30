/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 30/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations;

import cm.aptoide.pt.model.v7.store.GetStoreWidgets;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 29/04/16.
 */
public class HeaderGridDisplayable extends DisplayablePojo<Object> {

	public HeaderGridDisplayable() {
	}

	@Override
	public GetStoreWidgets.Type getName() {
		return GetStoreWidgets.Type.HEADER_ROW;
	}

	@Override
	public int getViewLayout() {
		return R.layout.header_grid_displayable_layout;
	}

	@Override
	public int getDefaultPerLineCount() {
		return 1;
	}
}
