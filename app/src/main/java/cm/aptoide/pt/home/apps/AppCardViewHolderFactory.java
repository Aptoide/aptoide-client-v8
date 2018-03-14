package cm.aptoide.pt.home.apps;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.home.apps.AppsAdapter.COMPLETED_DOWNLOAD;
import static cm.aptoide.pt.home.apps.AppsAdapter.ERROR_DOWNLOAD;
import static cm.aptoide.pt.home.apps.AppsAdapter.HEADER;
import static cm.aptoide.pt.home.apps.AppsAdapter.INSTALLED;
import static cm.aptoide.pt.home.apps.AppsAdapter.UPDATE;

/**
 * Created by filipegoncalves on 3/12/18.
 */

public class AppCardViewHolderFactory {

  private PublishSubject<App> pauseDownload;
  private PublishSubject<App> cancelDownload;
  private PublishSubject<App> resumeDownload;
  private PublishSubject<App> installApp;
  private PublishSubject<App> retryDownload;

  public AppCardViewHolderFactory(PublishSubject<App> pauseDownload,
      PublishSubject<App> cancelDownload, PublishSubject<App> resumeDownload,
      PublishSubject<App> installApp, PublishSubject<App> retryDownload) {
    this.pauseDownload = pauseDownload;
    this.cancelDownload = cancelDownload;
    this.resumeDownload = resumeDownload;
    this.installApp = installApp;
    this.retryDownload = retryDownload;
  }

  public AppsViewHolder createViewHolder(int viewType, ViewGroup parent) {
    AppsViewHolder appViewHolder;
    switch (viewType) {
      case HEADER:
        appViewHolder = new HeaderViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_header_item, parent, false));
        break;
      case AppsAdapter.ACTIVE_DOWNLOAD:
        appViewHolder = new ActiveAppDownloadViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_active_download_app_item, parent, false), pauseDownload);
        break;
      case AppsAdapter.STANDBY_DOWNLOAD:
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
            .inflate(R.layout.apps_header_item, parent, false));
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
