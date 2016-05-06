/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 06/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 06/05/16.
 */
public class AppViewInstallDisplayable extends DisplayablePojo<GetAppMeta.App> {

	public AppViewInstallDisplayable(GetAppMeta.App app) {
	}

	@Override
	public Type getType() {
		return Type.APP_VIEW_INSTALL;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_app_view_install;
	}

	@Override
	public int getDefaultPerLineCount() {
		return 1;
	}
}
