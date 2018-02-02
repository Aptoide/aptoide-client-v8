package cm.aptoide.pt.updates.view;

import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.InstallType;
import cm.aptoide.pt.download.Origin;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.view.recycler.displayable.Displayable;

/**
 * Created by neuro on 02-08-2016.
 */
public class UpdatesHeaderDisplayable extends Displayable {

  private String label;
  private InstallManager installManager;
  private DownloadAnalytics downloadAnalytics;
  private InstallAnalytics installAnalytics;

  public UpdatesHeaderDisplayable() {
  }

  public UpdatesHeaderDisplayable(InstallManager installManager, String label,
      DownloadAnalytics downloadAnalytics, InstallAnalytics installAnalytics) {
    this.installManager = installManager;
    this.label = label;
    this.downloadAnalytics = downloadAnalytics;
    this.installAnalytics = installAnalytics;
  }

  public String getLabel() {
    return label;
  }

  public InstallManager getInstallManager() {
    return installManager;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.updates_header_row;
  }

  public void setupDownloadEvent(Download download) {
    downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
        DownloadAnalytics.AppContext.UPDATE_TAB);

    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        InstallType.UPDATE, AnalyticsManager.Action.INSTALL,
        AppContext.UPDATE_TAB, Origin.UPDATE_ALL);
  }
}
