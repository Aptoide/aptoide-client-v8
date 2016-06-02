/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 11/05/16.
 */
public class AppViewScreenshotsDisplayable extends DisplayablePojo<GetAppMeta.App> {

	public AppViewScreenshotsDisplayable() {
	}

	public AppViewScreenshotsDisplayable(GetAppMeta.App pojo) {
		super(pojo);
	}

	public AppViewScreenshotsDisplayable(GetAppMeta.App pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
	}

	@Override
	public Type getType() {
		return Type.APP_VIEW_IMAGES;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_app_view_images;
	}
}
