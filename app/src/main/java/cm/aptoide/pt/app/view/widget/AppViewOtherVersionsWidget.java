/*
 * Copyright (c) 2016.
 * Modified on 04/07/2016.
 */

package cm.aptoide.pt.app.view.widget;

import android.view.View;
import cm.aptoide.pt.app.view.displayable.AppViewOtherVersionsDisplayable;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.view.recycler.widget.Widget;

/**
 * Created on 04/05/16.
 */
@Deprecated public class AppViewOtherVersionsWidget
    extends Widget<AppViewOtherVersionsDisplayable> {

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
