/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 09/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.support.annotation.LayoutRes;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

/**
 * Created by sithengineer on 28/04/16.
 *
 * @author SithEngineer
 */
public class GridAppDisplayable extends DisplayablePojo<App> {

  @Getter private String tag;

  public GridAppDisplayable() {
  }

  public GridAppDisplayable(App pojo, String tag) {
    super(pojo);
    this.tag = tag;
  }

  @Override public Type getType() {
    return Type.APPS_GROUP;
  }

  @LayoutRes @Override public int getViewLayout() {
    return R.layout.displayable_grid_app;
  }

  @Override protected Configs getConfig() {
    return new Configs(3, false);
  }
}
