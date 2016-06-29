/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 27/06/16.
 */
public class HeaderDisplayable extends DisplayablePojo<String> {

	public HeaderDisplayable() {
	}

	public HeaderDisplayable(String pojo) {
		super(pojo);
	}

	@Override
	public Type getType() {
		return Type.FOOTER_ROW;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_grid_header;
	}
}
