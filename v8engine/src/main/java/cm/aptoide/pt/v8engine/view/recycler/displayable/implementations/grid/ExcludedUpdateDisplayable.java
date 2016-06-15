/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 15/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 15/06/16.
 */
public class ExcludedUpdateDisplayable extends DisplayablePojo<GetApp> {
	
	@Override
	public Type getType() {
		return Type.EXCLUDED_UPDATE;
	}

	@Override
	public int getViewLayout() {
		return R.layout.row_excluded_update;
	}
}
