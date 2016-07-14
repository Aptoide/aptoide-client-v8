/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.support.annotation.LayoutRes;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by neuro on 09-05-2016.
 */
public class GridAppGraphicDisplayable extends DisplayablePojo<App> {

	public GridAppGraphicDisplayable() {
	}

	public GridAppGraphicDisplayable(App pojo) {
		super(pojo);
	}

	@Override
	public Type getType() {
		return Type.APPS_GROUP_GRAPHIC;
	}

	@LayoutRes
	@Override
	public int getViewLayout() {
		return R.layout.brick_app_item_list;
	}
}
