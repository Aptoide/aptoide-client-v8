/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.store.GetStoreWidgets;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 29/04/16.
 */
public class GridHeaderDisplayable extends DisplayablePojo<GetStoreWidgets.WSWidget> {

	public GridHeaderDisplayable() {
	}

	public GridHeaderDisplayable(GetStoreWidgets.WSWidget pojo) {
		super(pojo);
	}

	@Override
	public GetStoreWidgets.Type getType() {
		return GetStoreWidgets.Type.HEADER_ROW;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_grid_header;
	}

	@Override
	public int getDefaultPerLineCount() {
		return 1;
	}
}
