/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.view.updates.installed;

import cm.aptoide.pt.R;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

/**
 * Created by neuro on 17-05-2016.
 */
public class InstalledAppDisplayable extends DisplayablePojo<Installed> {

  private TimelineAnalytics timelineAnalytics;
  @Getter private InstalledRepository installedRepository;

  public InstalledAppDisplayable() {
  }

  public InstalledAppDisplayable(Installed pojo, TimelineAnalytics timelineAnalytics,
      InstalledRepository installedRepository) {
    super(pojo);
    this.timelineAnalytics = timelineAnalytics;
    this.installedRepository = installedRepository;
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
