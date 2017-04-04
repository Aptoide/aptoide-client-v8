/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by neuro on 14-04-2016.
 */
public class EmptyDisplayable extends Displayable {

  private int spanSize = 1;

  public EmptyDisplayable() {
  }

  public EmptyDisplayable(int spanSize) {
    this.spanSize = spanSize;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_empty;
  }

  @Override public int getSpanSize() {
    return spanSize;
  }

  @Override public int getDefaultPerLineCount() {
    // Stub
    return 1;
  }
}
