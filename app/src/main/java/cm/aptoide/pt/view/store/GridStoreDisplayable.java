/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 09/05/2016.
 */

package cm.aptoide.pt.view.store;

import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.Type;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

/**
 * Created on 29/04/16.
 */
public class GridStoreDisplayable extends DisplayablePojo<Store> {

  private String origin = "";
  private StoreAnalytics storeAnalytics;

  public GridStoreDisplayable(Store pojo, String origin, StoreAnalytics storeAnalytics) {
    super(pojo);
    this.origin = origin;
    this.storeAnalytics = storeAnalytics;
  }

  public GridStoreDisplayable(Store pojo) {
    super(pojo);
  }

  public GridStoreDisplayable() {
  }

  public StoreAnalytics getStoreAnalytics() {
    return storeAnalytics;
  }

  @Override protected Displayable.Configs getConfig() {
    return new Displayable.Configs(Type.STORES_GROUP.getDefaultPerLineCount(),
        Type.STORES_GROUP.isFixedPerLineCount());
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_store;
  }

  public String getOrigin() {
    return origin;
  }
}
