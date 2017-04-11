/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.view.View;
import cm.aptoide.pt.v8engine.view.recycler.displayable.ProgressBarDisplayable;

/**
 * Created by neuro on 07-06-2016.
 */
@Displayables({ ProgressBarDisplayable.class }) public class ProgressBarWidget
    extends Widget<ProgressBarDisplayable> {

  public ProgressBarWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {

  }

  @Override public void bindView(ProgressBarDisplayable displayable) {

  }
}
