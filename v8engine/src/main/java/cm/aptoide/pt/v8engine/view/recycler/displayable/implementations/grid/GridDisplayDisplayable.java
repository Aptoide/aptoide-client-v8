/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

/**
 * Created by sithengineer on 02/05/16.
 */
public class GridDisplayDisplayable extends DisplayablePojo<GetStoreDisplays.EventImage> {

	@Getter private String storeTheme;

	public GridDisplayDisplayable() {
	}

	public GridDisplayDisplayable(GetStoreDisplays.EventImage pojo) {
		super(pojo);
	}

	public GridDisplayDisplayable(GetStoreDisplays.EventImage pojo, String storeTheme) {
		super(pojo);
		this.storeTheme = storeTheme;
	}

	@Override
	public Type getType() {
		return Type.DISPLAYS;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_grid_display;
	}
}
