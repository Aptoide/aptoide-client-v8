/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

/**
 * Created by sithengineer on 29/04/16.
 */
public class StoreGridHeaderDisplayable extends DisplayablePojo<GetStoreWidgets.WSWidget> {

  @Getter private String storeTheme;
  @Getter private String tag;

  public StoreGridHeaderDisplayable() { }

  public StoreGridHeaderDisplayable(GetStoreWidgets.WSWidget pojo) {
    super(pojo);
  }

  public StoreGridHeaderDisplayable(GetStoreWidgets.WSWidget pojo, String storeTheme, String tag) {
    super(pojo);
    this.storeTheme = storeTheme;
    this.tag = tag;
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_header;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }
}
