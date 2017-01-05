package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.support.v4.app.FragmentActivity;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.reports.DownloadReport;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.reports.DownloadReportConverter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import lombok.Getter;
import rx.Observable;

/**
 * Created by neuro on 02-08-2016.
 */
public class UpdatesHeaderDisplayable extends Displayable {

  @Getter private String label;
  private Analytics analytics;
  private DownloadReportConverter converter;
  @Getter private InstallManager installManager;

  public UpdatesHeaderDisplayable() {
  }

  public UpdatesHeaderDisplayable(InstallManager installManager, String label, Analytics analytics,
      DownloadReportConverter downloadReportConverter) {
    this.installManager = installManager;
    this.label = label;
    this.analytics = analytics;
    this.converter = downloadReportConverter;
  }

  @Override public int getViewLayout() {
    return R.layout.updates_header_row;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  public Observable<Progress<Download>> install(FragmentActivity context, Download download) {
    if (installManager.showWarning()) {
      GenericDialogs.createGenericYesNoCancelMessage(context, null,
          AptoideUtils.StringU.getFormattedString(R.string.root_access_dialog))
          .subscribe(eResponse -> {
            switch (eResponse) {
              case YES:
                installManager.rootInstallAllowed(true);
                break;
              case NO:
                installManager.rootInstallAllowed(false);
                break;
            }
          });
    }
    return installManager.install(context, download)
        .doOnSubscribe(() -> setupDownloadEvent(download));
  }

  public void setupDownloadEvent(Download download) {
    DownloadReport report =
        converter.create(download, DownloadReport.Action.CLICK, DownloadReport.AppContext.updatetab,
            DownloadReport.Origin.update_all);
    analytics.save(download.getPackageName() + download.getVersionCode(), report);
  }
}
