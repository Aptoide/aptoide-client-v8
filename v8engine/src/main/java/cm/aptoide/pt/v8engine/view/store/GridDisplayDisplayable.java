/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/05/2016.
 */

package cm.aptoide.pt.v8engine.view.store;

import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

/**
 * Created on 02/05/16.
 */
public class GridDisplayDisplayable extends DisplayablePojo<GetStoreDisplays.EventImage> {

  @Getter private String storeTheme;
  @Getter private String tag;
  @Getter private StoreContext storeContext;
  private InstalledRepository installedRepository;

  public GridDisplayDisplayable() {
  }

  public GridDisplayDisplayable(GetStoreDisplays.EventImage pojo) {
    super(pojo);
  }

  public GridDisplayDisplayable(GetStoreDisplays.EventImage pojo, String storeTheme, String tag,
      StoreContext storeContext, InstalledRepository installedRepository) {
    super(pojo);
    this.storeTheme = storeTheme;
    this.tag = tag;
    this.storeContext = storeContext;
    this.installedRepository = installedRepository;
  }

  @Override protected Configs getConfig() {
    return new Configs(2, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_display;
  }

  public InstalledRepository getInstalledRepository() {
    return installedRepository;
  }
}
