/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 14/06/2016.
 */

package cm.aptoide.pt.v8engine.view.downloads.scheduled;

import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 14/06/16.
 */
public class ScheduleDownloadDisplayable extends DisplayablePojo<GetApp> {

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_scheduled_download;
  }
}
