/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.store.view.featured;

import android.support.annotation.LayoutRes;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

/**
 * Created by neuro on 09-05-2016.
 */
public class AppBrickListDisplayable extends DisplayablePojo<App> {

  @Getter private String tag;
  private NavigationTracker navigationTracker;
  private StoreContext storeContext;

  public AppBrickListDisplayable() {
  }

  public AppBrickListDisplayable(App pojo, String tag, NavigationTracker navigationTracker,
      StoreContext storeContext) {
    super(pojo);
    this.tag = tag;
    this.navigationTracker = navigationTracker;
    this.storeContext = storeContext;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @LayoutRes @Override public int getViewLayout() {
    return R.layout.brick_app_item_list;
  }

  public NavigationTracker getNavigationTracker() {
    return navigationTracker;
  }

  public StoreContext getStoreContext() {
    return storeContext;
  }
}
