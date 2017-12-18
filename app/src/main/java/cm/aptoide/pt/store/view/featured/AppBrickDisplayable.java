/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
 */

package cm.aptoide.pt.store.view.featured;

import android.support.annotation.LayoutRes;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

/**
 * Created by neuro on 09-05-2016.
 */
public class AppBrickDisplayable extends DisplayablePojo<App> {

  private String tag;
  private NavigationTracker navigationTracker;

  public AppBrickDisplayable() {
  }

  public AppBrickDisplayable(App pojo, String tag, NavigationTracker navigationTracker) {
    super(pojo);
    this.tag = tag;
    this.navigationTracker = navigationTracker;
  }

  /*
  @Override
	public boolean isFixedPerLineCount() {
		return true;
	}
	*/

  @Override protected Configs getConfig() {
    return new Configs(2, true);
  }

  @LayoutRes @Override public int getViewLayout() {
    return R.layout.brick_app_item;
  }

  public NavigationTracker getNavigationTracker() {
    return navigationTracker;
  }

  public String getTag() {
    return this.tag;
  }
}
