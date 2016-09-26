/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.dataprovider.model.MinimalAd;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewSuggestedAppDisplayable extends DisplayablePojo<MinimalAd> {

  public AppViewSuggestedAppDisplayable() {
  }

  public AppViewSuggestedAppDisplayable(MinimalAd minimalAd) {
    super(minimalAd);
  }

  public AppViewSuggestedAppDisplayable(MinimalAd minimalAd, boolean fixedPerLineCount) {
    super(minimalAd, fixedPerLineCount);
  }

  @Override public Type getType() {
    return Type.APP_VIEW_SUGGESTED_APP;
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_suggested_app;
  }
}
