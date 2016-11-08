/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by neuro on 20-06-2016.
 */
public class SearchAdDisplayable extends DisplayablePojo<GetAdsResponse.Ad> {

  public SearchAdDisplayable() {
  }

  public SearchAdDisplayable(GetAdsResponse.Ad pojo) {
    super(pojo);
  }

  @Override public int getViewLayout() {
    return R.layout.suggested_app_search;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }
}
