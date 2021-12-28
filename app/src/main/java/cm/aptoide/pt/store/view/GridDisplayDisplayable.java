/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/05/2016.
 */

package cm.aptoide.pt.store.view;

import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.install.AptoideInstalledAppsRepository;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

/**
 * Created on 02/05/16.
 */
public class GridDisplayDisplayable extends DisplayablePojo<GetStoreDisplays.EventImage> {

  private String storeTheme;
  private String tag;
  private StoreContext storeContext;
  private AptoideInstalledAppsRepository aptoideInstalledAppsRepository;
  private String storeName;

  public GridDisplayDisplayable() {
  }

  public GridDisplayDisplayable(GetStoreDisplays.EventImage pojo, String storeTheme, String tag,
      StoreContext storeContext, AptoideInstalledAppsRepository aptoideInstalledAppsRepository) {
    super(pojo);
    this.storeTheme = storeTheme;
    this.tag = tag;
    this.storeContext = storeContext;
    this.aptoideInstalledAppsRepository = aptoideInstalledAppsRepository;
  }

  @Override protected Configs getConfig() {
    return new Configs(2, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_display;
  }

  public AptoideInstalledAppsRepository getInstalledRepository() {
    return aptoideInstalledAppsRepository;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getStoreTheme() {
    return this.storeTheme;
  }

  public String getTag() {
    return this.tag;
  }

  public StoreContext getStoreContext() {
    return this.storeContext;
  }
}
