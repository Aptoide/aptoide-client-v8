/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.downloads.scheduled;

import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.ScheduledAccessor;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SelectableDisplayablePojo;
import lombok.Getter;

/**
 * created by SithEngineer
 */
public class ScheduledDownloadDisplayable extends SelectableDisplayablePojo<Scheduled> {

  private static final String TAG = ScheduledDownloadDisplayable.class.getSimpleName();
  @Getter private InstallManager installManager;

  public ScheduledDownloadDisplayable() {
  }

  public ScheduledDownloadDisplayable(Scheduled pojo, InstallManager installManager) {
    super(pojo);
    this.installManager = installManager;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_scheduled_download_row;
  }

  public void removeFromDatabase() {
    ((ScheduledAccessor) AccessorFactory.getAccessorFor(Scheduled.class)).delete(
        getPojo().getMd5());
  }
}
