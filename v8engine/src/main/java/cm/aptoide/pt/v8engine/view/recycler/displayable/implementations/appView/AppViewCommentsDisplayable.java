/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.model.v7.store.GetStoreWidgets;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewCommentsDisplayable extends AppViewDisplayable<Object> {

	@Override
	public GetStoreWidgets.Type getType() {
		return null;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_app_view_comments;
	}
}
