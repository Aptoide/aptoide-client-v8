/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 02/05/16.
 */
public class GridDisplayDisplayable extends DisplayablePojo<GetStoreDisplays.EventImage> {

	@Override
	public Type getType() {
		return Type.DISPLAYS;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_grid_display;
	}

	@Override
	public int getDefaultPerLineCount() {
		return 2;
	}
}
