package cm.aptoide.pt.install;

import android.util.Log;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by tiagopedrinho on 10/10/2018.
 */

public class DownloadsNotificationsPresenter {

  private static final String TAG = DownloadsNotificationsPresenter.class.getSimpleName();
  private DownloadsNotification service;
  private InstallManager installManager;
  private CompositeSubscription subscriptions;

  public DownloadsNotificationsPresenter(DownloadsNotification service,
      InstallManager installManager) {
    this.service = service;
    this.installManager = installManager;
    subscriptions = new CompositeSubscription();
  }

  public void setupSubscriptions() {
    handleOpenAppView();
    handleOpenDownloadManager();
    handleCurrentInstallation();
  }

  private void handleOpenAppView() {

    subscriptions.add(service.handleOpenAppView()
        .doOnNext(md5 -> service.openAppView(md5))
        .subscribe());
  }

  private void handleOpenDownloadManager() {
    subscriptions.add(service.handleOpenDownloadManager()
        .doOnNext(openDownloadManagerView -> service.openDownloadManager())
        .subscribe());
  }

  private void handleCurrentInstallation() {
    subscriptions.add(installManager.getCurrentInstallation()
        .subscribe(installation -> {
          if (!installation.isIndeterminate()) {
            String md5 = installation.getMd5();
            service.setupNotification(md5, installation.getAppName(), installation.getProgress(),
                installation.isIndeterminate());
          }
        }, throwable -> {
          Log.e(TAG, "Error on handleOpenDownloadManager");
          service.removeNotificationAndStop();
        }));
  }

  public void onDestroy() {
    subscriptions.unsubscribe();
  }
}
