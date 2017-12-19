/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.updates.view.installed;

import cm.aptoide.pt.R;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

/**
 * Created by neuro on 17-05-2016.
 */
public class InstalledAppDisplayable extends DisplayablePojo<Installed> {

  private TimelineAnalytics timelineAnalytics;
  private InstalledRepository installedRepository;

  public InstalledAppDisplayable() {
  }

  public InstalledAppDisplayable(Installed pojo, TimelineAnalytics timelineAnalytics,
      InstalledRepository installedRepository) {
    super(pojo);
    this.timelineAnalytics = timelineAnalytics;
    this.installedRepository = installedRepository;
  }

  public InstalledRepository getInstalledRepository() {
    return installedRepository;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.installed_row;
  }

  public TimelineAnalytics getTimelineAnalytics() {
    return timelineAnalytics;
  }
}
