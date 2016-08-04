/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import java.util.List;

import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewSuggestedAppsDisplayable extends DisplayablePojo<List<GetAdsResponse.Ad>> {

	public AppViewSuggestedAppsDisplayable() {
	}

	public AppViewSuggestedAppsDisplayable(List<GetAdsResponse.Ad> ads) {
		super(ads);
	}

	public AppViewSuggestedAppsDisplayable(List<GetAdsResponse.Ad> ads, boolean fixedPerLineCount) {
		super(ads, fixedPerLineCount);
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
