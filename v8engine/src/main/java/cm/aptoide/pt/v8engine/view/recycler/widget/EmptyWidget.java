/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 18/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.view.View;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.EmptyDisplayable;

/**
 * Created by neuro on 14-04-2016.
 */
// using this instead of the previous method
// can also return a list of Displayable classes
@Displayables({ EmptyDisplayable.class }) public class EmptyWidget extends Widget {

  public EmptyWidget(View view) {
    super(view);
  }

  @Override protected void assignViews(View itemView) {
    // TODO
  }

  @Override public void bindView(Displayable displayable) {
    // TODO
  }
}
