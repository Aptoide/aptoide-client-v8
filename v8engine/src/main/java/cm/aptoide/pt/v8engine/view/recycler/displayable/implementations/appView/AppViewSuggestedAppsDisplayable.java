/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import java.util.List;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewSuggestedAppsDisplayable extends DisplayablePojo<List<GetAdsResponse.Ad>> {

  public AppViewSuggestedAppsDisplayable() {
  }

  public AppViewSuggestedAppsDisplayable(List<GetAdsResponse.Ad> ads) {
    super(ads);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_suggested_apps;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }
}
