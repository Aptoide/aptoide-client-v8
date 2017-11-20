package cm.aptoide.pt.presenter;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallManager;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DownloadsPresenter implements Presenter {

  private final DownloadsView view;
  private final InstallManager installManager;

  public DownloadsPresenter(DownloadsView downloadsView, InstallManager installManager) {
    this.view = downloadsView;
    this.installManager = installManager;
  }

  @Override public void present() {

    view.getLifecycleEvents()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.RESUME)
        .first()
        .observeOn(Schedulers.computation())
        .flatMap(created -> installManager.getInstallations()
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(installations -> {
              if (installations == null || installations.isEmpty()) {
                view.showEmptyDownloadList();
                return Observable.empty();
              }
              return Observable.merge(setActive(installations), setStandBy(installations),
                  setCompleted(installations));
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
          // does nothing
        }, err -> {
          CrashReport.getInstance()
              .log(err);
          view.showEmptyDownloadList();
        });
  }

  private Observable<Void> setActive(List<Install> downloads) {
    return Observable.from(downloads)
        .filter(d -> isInstalling(d))
        .toList()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(onGoingDownloads -> view.showActiveDownloads(onGoingDownloads))
        .map(__ -> null);
  }

  private Observable<Void> setStandBy(List<Install> downloads) {
    return Observable.from(downloads)
        .filter(d -> isStandingBy(d))
        .toList()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(onGoingDownloads -> view.showStandByDownloads(onGoingDownloads))
        .map(__ -> null);
  }

  private Observable<Void> setCompleted(List<Install> downloads) {
    return Observable.from(downloads)
        .filter(d -> !isInstalling(d) && !isStandingBy(d))
        .toList()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(onGoingDownloads -> view.showCompletedDownloads(onGoingDownloads))
        .map(__ -> null);
  }

  private boolean isInstalling(Install progress) {
    return progress.getState() == Install.InstallationStatus.INSTALLING;
  }

  private boolean isStandingBy(Install install) {
    return install.isFailed()
        || install.getState() == Install.InstallationStatus.PAUSED
        || install.getState() == Install.InstallationStatus.IN_QUEUE;
  }

  public void pauseInstall(DownloadsView.DownloadViewModel download) {
    installManager.stopInstallation(download.getAppMd5());
  }
}
