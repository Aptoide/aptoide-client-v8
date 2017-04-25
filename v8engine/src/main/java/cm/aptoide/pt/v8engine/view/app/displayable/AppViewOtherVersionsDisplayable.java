/*
 * Copyright (c) 2016.
 * Modified on 04/07/2016.
 */

package cm.aptoide.pt.v8engine.view.app.displayable;

import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.v8engine.R;

/**
 * Created on 04/05/16.
 */
@Deprecated public class AppViewOtherVersionsDisplayable extends AppViewDisplayable {

  public AppViewOtherVersionsDisplayable() {
  }

  public AppViewOtherVersionsDisplayable(GetApp getApp) {
    super(getApp);
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_other_versions;
  }
}
