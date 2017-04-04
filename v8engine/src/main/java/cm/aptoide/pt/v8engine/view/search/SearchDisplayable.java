/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 02/06/2016.
 */

package cm.aptoide.pt.v8engine.view.search;

import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;
import rx.functions.Action0;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchDisplayable extends DisplayablePojo<ListSearchApps.SearchAppsApp> {

  @Getter private Action0 clickCallback;

  public SearchDisplayable() {
  }

  public SearchDisplayable(ListSearchApps.SearchAppsApp pojo, Action0 clickCallback) {
    super(pojo);
    this.clickCallback = clickCallback;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.search_app_row;
  }
}
