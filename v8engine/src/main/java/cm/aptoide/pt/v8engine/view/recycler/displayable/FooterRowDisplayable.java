/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 27/06/16.
 */
public class FooterRowDisplayable extends DisplayablePojo<String> {

  public FooterRowDisplayable() {
  }

  public FooterRowDisplayable(String pojo) {
    super(pojo);
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_footer_text;
  }
}
