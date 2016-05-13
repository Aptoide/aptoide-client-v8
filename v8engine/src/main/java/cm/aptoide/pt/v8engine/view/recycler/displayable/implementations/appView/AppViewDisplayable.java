/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 10/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.annotation.Ignore;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 04/05/16.
 */
@Ignore
public abstract class AppViewDisplayable extends DisplayablePojo<GetAppMeta.App> {

	public AppViewDisplayable() {
	}

	public AppViewDisplayable(GetAppMeta.App app) {
		super(app);
	}

	public AppViewDisplayable(GetAppMeta.App app, boolean fixedPerLineCount) {
		super(app, fixedPerLineCount);
	}

	@Override
	public int getDefaultPerLineCount() {
		return 1;
	}
}
