/*
 * Copyright (c) 2016.
 * Modified on 04/07/2016.
 */

package cm.aptoide.pt.app.view.displayable;

import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;

/**
 * Created on 04/05/16.
 */
@Deprecated public class AppViewRateThisDisplayable extends AppViewDisplayable {

  public AppViewRateThisDisplayable() {
  }

  public AppViewRateThisDisplayable(GetApp getApp) {
    super(getApp);
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_rate_this;
  }
}
