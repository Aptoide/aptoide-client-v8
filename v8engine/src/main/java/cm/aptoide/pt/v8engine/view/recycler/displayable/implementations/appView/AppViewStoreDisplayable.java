/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by sithengineer on 10/05/16.
 */
public class AppViewStoreDisplayable extends AppViewDisplayable {

  public AppViewStoreDisplayable() {
  }

  public AppViewStoreDisplayable(GetApp getApp) {
    super(getApp);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_subscription;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }
}
