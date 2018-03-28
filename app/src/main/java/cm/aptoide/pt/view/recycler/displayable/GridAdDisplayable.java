/*
 * Copyright (c) 2016.
 * Modified on 24/06/2016.
 */

package cm.aptoide.pt.view.recycler.displayable;

import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.database.realm.MinimalAd;

/**
 * Created by neuro on 20-06-2016.
 */
public class GridAdDisplayable extends DisplayablePojo<MinimalAd> {

  private String tag;
  private NavigationTracker navigationTracker;

  public GridAdDisplayable() {
  }

  public GridAdDisplayable(MinimalAd minimalAd, String tag, NavigationTracker navigationTracker) {
    super(minimalAd);
    this.tag = tag;
    this.navigationTracker = navigationTracker;
  }

  @Override protected Configs getConfig() {
    return new Configs(3, false);
  }

  @Override public int getViewLayout() {
    return R.layout.app_home_item;
  }

  public NavigationTracker getNavigationTracker() {
    return navigationTracker;
  }

  public String getTag() {
    return this.tag;
  }
}
