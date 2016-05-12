/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewSuggestedAppsDisplayable extends AppViewDisplayable<Object> {

	public AppViewSuggestedAppsDisplayable() {
	}

	@Override
	public Type getType() {
		return Type.APP_VIEW_SUGGESTED_APPS;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_app_view_suggested_apps;
	}
}
