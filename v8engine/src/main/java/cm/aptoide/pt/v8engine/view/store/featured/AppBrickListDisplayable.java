/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.v8engine.view.store.featured;

import android.support.annotation.LayoutRes;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

/**
 * Created by neuro on 09-05-2016.
 */
public class AppBrickListDisplayable extends DisplayablePojo<App> {

  @Getter private String tag;

  public AppBrickListDisplayable() {
  }

  public AppBrickListDisplayable(App pojo, String tag) {
    super(pojo);
    this.tag = tag;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @LayoutRes @Override public int getViewLayout() {
    return R.layout.brick_app_item_list;
  }
}
