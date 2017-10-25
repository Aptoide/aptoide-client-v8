/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.app.view.displayable;

import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;

/**
 * Created on 04/05/16.
 */
public class AppViewDeveloperDisplayable extends AppViewDisplayable {

  public AppViewDeveloperDisplayable() {
  }

  public AppViewDeveloperDisplayable(GetApp getApp) {
    super(getApp);
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_developer;
  }
}
