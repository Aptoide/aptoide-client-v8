package cm.aptoide.pt.install;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.Presenter;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by tiagopedrinho on 10/10/2018.
 */

public class DownloadsNotificationsPresenter implements Presenter {

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

  private void handleCurrentInstallation() {
    subscriptions.add(installManager.getCurrentInstallation()
        .doOnError(throwable -> {
          throwable.printStackTrace();
          Logger.getInstance()
              .d(TAG, "error on getCurrentInstallation");
        })
        .doOnNext(installation -> {
          if (!installation.isIndeterminate()) {
            String md5 = installation.getMd5();
            service.setupNotification(md5, installation.getAppName(), installation.getProgress(),
                installation.isIndeterminate());
          }
        })
        .distinctUntilChanged(Install::getState)
        .flatMap(install -> installManager.getDownloadState(install.getMd5())
            .first())
        .doOnNext(installationStatus -> {
          if (installationStatus != Install.InstallationStatus.DOWNLOADING) {
            service.removeNotificationAndStop();
          }
        })
        .subscribe(__ -> {
        }, throwable -> {
          service.removeNotificationAndStop();
          throwable.printStackTrace();
        }));
  }

  public void onDestroy() {
    subscriptions.unsubscribe();
  }

  @Override public void present() {
    handleCurrentInstallation();
  }
}
