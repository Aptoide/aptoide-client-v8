/*
 * Copyright (c) 2016.
 * Modified on 24/06/2016.
 */

package cm.aptoide.pt.view.recycler.displayable;

import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.AptoideNavigationTracker;
import cm.aptoide.pt.database.realm.MinimalAd;
import lombok.Getter;

/**
 * Created by neuro on 20-06-2016.
 */
public class GridAdDisplayable extends DisplayablePojo<MinimalAd> {

  @Getter private String tag;
  private AptoideNavigationTracker aptoideNavigationTracker;

  public GridAdDisplayable() {
  }

  public GridAdDisplayable(MinimalAd minimalAd, String tag,
      AptoideNavigationTracker aptoideNavigationTracker) {
    super(minimalAd);
    this.tag = tag;
    this.aptoideNavigationTracker = aptoideNavigationTracker;
  }

  @Override protected Configs getConfig() {
    return new Configs(3, false);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_sponsored;
  }

  public AptoideNavigationTracker getAptoideNavigationTracker() {
    return aptoideNavigationTracker;
  }
}
