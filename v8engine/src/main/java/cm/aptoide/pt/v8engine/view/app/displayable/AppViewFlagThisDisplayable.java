/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/07/2016.
 */

package cm.aptoide.pt.v8engine.view.app.displayable;

import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by sithengineer on 30/06/16.
 */
public class AppViewFlagThisDisplayable extends AppViewDisplayable {

  public AppViewFlagThisDisplayable() {
  }

  public AppViewFlagThisDisplayable(GetApp getApp) {
    super(getApp);
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_flag_this;
  }
}
