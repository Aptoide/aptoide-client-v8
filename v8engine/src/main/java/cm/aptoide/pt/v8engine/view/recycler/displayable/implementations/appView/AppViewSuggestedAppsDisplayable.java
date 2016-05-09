/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.model.v7.Type;

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
		return 0;
	}
}
