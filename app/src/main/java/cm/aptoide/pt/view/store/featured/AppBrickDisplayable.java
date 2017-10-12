/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
 */

package cm.aptoide.pt.view.store.featured;

import android.support.annotation.LayoutRes;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.AptoideNavigationTracker;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

/**
 * Created by neuro on 09-05-2016.
 */
public class AppBrickDisplayable extends DisplayablePojo<App> {

  @Getter private String tag;
  private AptoideNavigationTracker aptoideNavigationTracker;

  public AppBrickDisplayable() {
  }

  public AppBrickDisplayable(App pojo, String tag,
      AptoideNavigationTracker aptoideNavigationTracker) {
    super(pojo);
    this.tag = tag;
    this.aptoideNavigationTracker = aptoideNavigationTracker;
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

  public AptoideNavigationTracker getAptoideNavigationTracker() {
    return aptoideNavigationTracker;
  }
}
