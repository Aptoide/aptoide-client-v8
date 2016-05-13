/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 10/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by sithengineer on 10/05/16.
 */
public class AppViewSubscriptionDisplayable extends AppViewDisplayable<GetAppMeta.App> {


	public AppViewSubscriptionDisplayable() {
	}

	public AppViewSubscriptionDisplayable(GetAppMeta.App app) {
		super(app);
	}

	public AppViewSubscriptionDisplayable(GetAppMeta.App pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
	}

	@Override
	public Type getType() {
		return Type.APP_VIEW_SUBSCRIPTION;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_app_view_subscription;
	}
}
