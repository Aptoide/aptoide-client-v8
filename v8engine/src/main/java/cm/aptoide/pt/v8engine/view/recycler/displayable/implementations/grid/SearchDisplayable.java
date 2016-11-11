/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 02/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchDisplayable extends DisplayablePojo<ListSearchApps.SearchAppsApp> {

  public SearchDisplayable() {
  }

  public SearchDisplayable(ListSearchApps.SearchAppsApp pojo) {
    super(pojo);
  }

  @Override public int getViewLayout() {
    return R.layout.search_app_row;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }
}
