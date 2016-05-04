/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.support.annotation.LayoutRes;

import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.GetStoreWidgets;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 28/04/16.
 *
 * @author SithEngineer
 */
public class GridAppDisplayable extends DisplayablePojo<App> {

	public GridAppDisplayable() {
	}

	public GridAppDisplayable(App pojo) {
		super(pojo);
	}

	@Override
	public GetStoreWidgets.Type getName() {
		return GetStoreWidgets.Type.APPS_GROUP;
	}

	@LayoutRes
	@Override
	public int getViewLayout() {
		return R.layout.app_grid_displayable_layout;
	}

	@Override
	public int getDefaultPerLineCount() {
		return 3;
	}
}
