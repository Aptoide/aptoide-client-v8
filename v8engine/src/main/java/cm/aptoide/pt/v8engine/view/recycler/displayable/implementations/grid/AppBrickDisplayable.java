/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
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
public class AppBrickDisplayable extends DisplayablePojo<App> {

	public AppBrickDisplayable() {
	}

	public AppBrickDisplayable(App pojo) {
		super(pojo);
	}

	@Override
	public Type getType() {
		return Type.APP_BRICK;
	}

	@LayoutRes
	@Override
	public int getViewLayout() {
		return R.layout.brick_app_item;
	}
}
