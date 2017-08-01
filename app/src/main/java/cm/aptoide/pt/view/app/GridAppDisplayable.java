/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 09/05/2016.
 */

package cm.aptoide.pt.view.app;

import android.support.annotation.LayoutRes;
import cm.aptoide.pt.dataprovider.model.v7.Type;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

/**
 * Created on 28/04/16.
 *
 * @author SithEngineer
 */
public class GridAppDisplayable extends DisplayablePojo<App> {

  @Getter private String tag;
  @Getter private boolean totalDownloads;

  public GridAppDisplayable() {
  }

  public GridAppDisplayable(App pojo, String tag, boolean totalDownloads) {
    super(pojo);
    this.tag = tag;
    this.totalDownloads = totalDownloads;
  }

  @Override protected Configs getConfig() {
    return new Configs(Type.APPS_GROUP.getDefaultPerLineCount(),
        Type.APPS_GROUP.isFixedPerLineCount());
  }

  @LayoutRes @Override public int getViewLayout() {
    return R.layout.displayable_grid_app;
  }
}
