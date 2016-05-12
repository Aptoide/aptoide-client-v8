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
public class AppViewRateThisDisplayable extends AppViewDisplayable<Object> {

	public AppViewRateThisDisplayable() {
	}

	@Override
	public Type getType() {
		return Type.APP_VIEW_RATE_THIS;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_app_view_rate_this;
	}
}
