/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.view.recycler.displayable;

import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.R;
import lombok.Getter;

/**
 * Created on 29/04/16.
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
