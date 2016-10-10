/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewDeveloperDisplayable extends AppViewDisplayable {

  public AppViewDeveloperDisplayable() {
  }

  public AppViewDeveloperDisplayable(GetApp getApp) {
    super(getApp);
  }

  @Override public Type getType() {
    return Type.APP_VIEW_DEVELOPER;
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_developer;
  }
}
