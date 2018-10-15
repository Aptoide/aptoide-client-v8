package cm.aptoide.pt.install;

import android.util.Log;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by tiagopedrinho on 10/10/2018.
 */

public class DownloadsNotificationsPresenter {

  private DownloadsNotification view;
  private InstallManager installManager;
  private CompositeSubscription subscriptions;

  public DownloadsNotificationsPresenter(DownloadsNotification view,
      InstallManager installManager) {
    this.view = view;
    this.installManager = installManager;
    subscriptions = new CompositeSubscription();
  }

  public void onCreate() {
    handleOpenAppView();
    handleOpenDownloadManager();
    getCurrentInstallation();
  }

  private void handleOpenAppView() {

    subscriptions.add(view.handleOpenAppView()
        .doOnNext(md5 -> view.openAppView(md5))
        .subscribe());
  }

  private void handleOpenDownloadManager() {
    subscriptions.add(view.handleOpenDownloadManager()
        .doOnNext(openDownloadManagerView -> view.openDownloadManager())
        .subscribe());
  }

  private void getCurrentInstallation(){
    subscriptions.add(installManager.getCurrentInstallation()
        .subscribe(installation -> {
          if (!installation.isIndeterminate()) {
            String md5 = installation.getMd5();
            view.setupNotification(md5, installation.getAppName(), installation.getProgress(), installation.isIndeterminate());
          }
        }, throwable -> {
          Log.e("TAG", "Error on handleOpenDownloadManager");
          view.removeNotificationAndStop();
        }));
  }

  public void onDestroy() {
    subscriptions.unsubscribe();
  }
}
