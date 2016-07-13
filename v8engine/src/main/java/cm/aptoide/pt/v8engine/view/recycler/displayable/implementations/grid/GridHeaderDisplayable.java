/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

/**
 * Created by sithengineer on 29/04/16.
 */
public class GridHeaderDisplayable extends DisplayablePojo<GetStoreWidgets.WSWidget> {

	@Getter private String storeTheme;

	public GridHeaderDisplayable() { }

	public GridHeaderDisplayable(GetStoreWidgets.WSWidget pojo) {
		super(pojo);
	}

	public GridHeaderDisplayable(GetStoreWidgets.WSWidget pojo, String storeTheme) {
		super(pojo);
		this.storeTheme = storeTheme;
	}

	@Override
	public Type getType() {
		return Type.HEADER_ROW;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_grid_header;
	}

}
