/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/07/2016.
 */

package cm.aptoide.pt.v8engine.view.app.widget;

import android.view.View;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewOtherVersionsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 04/05/16.
 */
@Deprecated @Displayables({ AppViewOtherVersionsDisplayable.class })
public class AppViewOtherVersionsWidget extends Widget<AppViewOtherVersionsDisplayable> {

  public AppViewOtherVersionsWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {

  }

  @Override public void bindView(AppViewOtherVersionsDisplayable displayable) {
    final GetApp pojo = displayable.getPojo();
    // TODO
  }
}
