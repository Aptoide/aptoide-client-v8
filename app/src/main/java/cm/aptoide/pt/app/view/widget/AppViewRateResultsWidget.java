/*
 * Copyright (c) 2016.
 * Modified on 04/05/2016.
 */

package cm.aptoide.pt.app.view.widget;

import android.view.View;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.app.view.displayable.AppViewRateResultsDisplayable;
import cm.aptoide.pt.view.recycler.widget.Widget;

/**
 * Created on 04/05/16.
 */
public class AppViewRateResultsWidget extends Widget<AppViewRateResultsDisplayable> {

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
