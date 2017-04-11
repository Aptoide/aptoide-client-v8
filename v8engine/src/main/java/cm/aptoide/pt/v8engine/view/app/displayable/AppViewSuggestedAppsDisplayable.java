/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.view.app.displayable;

import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import java.util.List;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewSuggestedAppsDisplayable extends DisplayablePojo<List<MinimalAd>> {

  public AppViewSuggestedAppsDisplayable() {
  }

  public AppViewSuggestedAppsDisplayable(List<MinimalAd> ads) {
    super(ads);
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_suggested_apps;
  }
}
