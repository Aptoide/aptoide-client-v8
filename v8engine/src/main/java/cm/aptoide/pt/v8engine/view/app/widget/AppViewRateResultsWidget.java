/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.view.app.widget;

import android.view.View;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewRateResultsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 04/05/16.
 */
@Displayables({ AppViewRateResultsDisplayable.class }) public class AppViewRateResultsWidget
    extends Widget<AppViewRateResultsDisplayable> {

  public AppViewRateResultsWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {

  }

  @Override public void bindView(AppViewRateResultsDisplayable displayable) {
    final GetApp pojo = displayable.getPojo();
    // TODO
  }
}
