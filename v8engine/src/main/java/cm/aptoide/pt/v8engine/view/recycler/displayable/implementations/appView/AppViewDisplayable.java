/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.annotation.Ignore;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 04/05/16.
 */
@Ignore
abstract class AppViewDisplayable extends DisplayablePojo<GetApp> {

	public AppViewDisplayable() {
	}

	public AppViewDisplayable(GetApp getApp) {
		super(getApp);
	}

	public AppViewDisplayable(GetApp getApp, boolean fixedPerLineCount) {
		super(getApp, fixedPerLineCount);
	}

	@Override
	public int getDefaultPerLineCount() {
		return 1;
	}
}
