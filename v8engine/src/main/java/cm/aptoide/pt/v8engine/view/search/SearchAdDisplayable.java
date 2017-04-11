/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.v8engine.view.search;

import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by neuro on 20-06-2016.
 */
public class SearchAdDisplayable extends DisplayablePojo<MinimalAd> {

  public SearchAdDisplayable() {
  }

  public SearchAdDisplayable(MinimalAd pojo) {
    super(pojo);
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.suggested_app_search;
  }
}
