/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

/**
 * Created by sithengineer on 29/04/16.
 */
public class FooterDisplayable extends DisplayablePojo<GetStoreWidgets.WSWidget> {

  @Getter private String tag;
  @Getter private StoreContext storeContext;

  public FooterDisplayable() {
  }

  public FooterDisplayable(GetStoreWidgets.WSWidget pojo, String tag, StoreContext storeContext) {
    super(pojo);
    this.tag = tag;
    this.storeContext = storeContext;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_footer;
  }
}
