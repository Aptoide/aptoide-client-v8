package cm.aptoide.pt.home.apps;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.home.apps.AppsAdapter.ACTIVE_DOWNLOAD;
import static cm.aptoide.pt.home.apps.AppsAdapter.COMPLETED_DOWNLOAD;
import static cm.aptoide.pt.home.apps.AppsAdapter.ERROR_DOWNLOAD;
import static cm.aptoide.pt.home.apps.AppsAdapter.ERROR_UPDATE;
import static cm.aptoide.pt.home.apps.AppsAdapter.HEADER;
import static cm.aptoide.pt.home.apps.AppsAdapter.HEADER_UPDATES;
import static cm.aptoide.pt.home.apps.AppsAdapter.INSTALLED;
import static cm.aptoide.pt.home.apps.AppsAdapter.STANDBY_DOWNLOAD;
import static cm.aptoide.pt.home.apps.AppsAdapter.STANDBY_UPDATE;
import static cm.aptoide.pt.home.apps.AppsAdapter.UPDATE;
import static cm.aptoide.pt.home.apps.AppsAdapter.UPDATING;

/**
 * Created by filipegoncalves on 3/12/18.
 */

public class AppCardViewHolderFactory {

  private PublishSubject<App> pauseDownload;
  private PublishSubject<App> cancelDownload;
  private PublishSubject<App> resumeDownload;
  private PublishSubject<App> installApp;
  private PublishSubject<App> retryDownload;
  private PublishSubject<App> updateAllApps;
  private PublishSubject<App> updateApp;
  private PublishSubject<App> pauseUpdate;
  private PublishSubject<App> cancelUpdate;
  private PublishSubject<App> resumeUpdate;

  public AppCardViewHolderFactory(PublishSubject<App> pauseDownload,
      PublishSubject<App> cancelDownload, PublishSubject<App> resumeDownload,
      PublishSubject<App> installApp, PublishSubject<App> retryDownload,
      PublishSubject<App> updateAllApps, PublishSubject<App> updateApp,
      PublishSubject<App> pauseUpdate, PublishSubject<App> cancelUpdate,
      PublishSubject<App> resumeUpdate) {
    this.pauseDownload = pauseDownload;
    this.cancelDownload = cancelDownload;
    this.resumeDownload = resumeDownload;
    this.installApp = installApp;
    this.retryDownload = retryDownload;
    this.updateAllApps = updateAllApps;
    this.updateApp = updateApp;
    this.pauseUpdate = pauseUpdate;
    this.cancelUpdate = cancelUpdate;
    this.resumeUpdate = resumeUpdate;
  }

  public AppsViewHolder createViewHolder(int viewType, ViewGroup parent) {
    AppsViewHolder appViewHolder;
    switch (viewType) {
      case HEADER:
        appViewHolder = new HeaderViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_header_item, parent, false));
        break;
      case HEADER_UPDATES:
        appViewHolder = new UpdatesHeaderViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_header_updates_item, parent, false), updateAllApps);
        break;
      case ACTIVE_DOWNLOAD:
        appViewHolder = new ActiveAppDownloadViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_active_download_app_item, parent, false), pauseDownload);
        break;
      case STANDBY_DOWNLOAD:
        appViewHolder = new StandByAppDownloadViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_standby_download_app_item, parent, false), cancelDownload,
            resumeDownload);
        break;
      case COMPLETED_DOWNLOAD:
        appViewHolder = new CompletedAppDownloadViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_completed_download_app_item, parent, false), installApp);
        break;
      case ERROR_DOWNLOAD:
        appViewHolder = new ErrorAppDownloadViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_error_download_app_item, parent, false), retryDownload);
        break;
      case UPDATE:
        appViewHolder = new UpdateAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_update_app_item, parent, false), updateApp);
        break;
      case UPDATING:
        appViewHolder = new UpdatingAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_updating_app_item, parent, false), pauseUpdate);
        break;
      case STANDBY_UPDATE:
        appViewHolder = new StandByUpdateAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_standby_update_app_item, parent, false), cancelUpdate,
            resumeUpdate);
        break;
      case ERROR_UPDATE:
        appViewHolder = new UpdateAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_header_item, parent, false), updateApp);
        break;
      case INSTALLED:
        appViewHolder = new InstalledAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_installed_app_item, parent, false));
        break;
      default:
        throw new IllegalStateException("Wrong cardType" + viewType);
    }

    return appViewHolder;
  }
}
