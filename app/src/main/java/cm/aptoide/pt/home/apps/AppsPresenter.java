package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Scheduler;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsPresenter implements Presenter {

  private AppsFragmentView view;
  private AppsManager appsManager;
  private Scheduler viewScheduler;
  private Scheduler computation;
  private CrashReport crashReport;

  public AppsPresenter(AppsFragmentView view, AppsManager appsManager, Scheduler viewScheduler,
      Scheduler computation, CrashReport crashReport) {
    this.view = view;
    this.appsManager = appsManager;
    this.viewScheduler = viewScheduler;
    this.computation = computation;
    this.crashReport = crashReport;
  }

  @Override public void present() {

    getUpdatesList();

    getInstalls();

    getDownloads();

    handlePauseDownloadClick();

    handleResumeDownloadClick();

    handleCancelDownloadClick();

    handleInstallAppClick();

    handleRetryDownloadClick();
  }

  private void handleRetryDownloadClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.retryDownload())
        .doOnNext(app -> appsManager.retryDownload(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleInstallAppClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.installApp())
        .doOnNext(app -> appsManager.installApp(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleCancelDownloadClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.cancelDownload())
        .doOnNext(app -> appsManager.cancelDownload(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleResumeDownloadClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.resumeDownload())
        .doOnNext(app -> appsManager.resumeDownload(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handlePauseDownloadClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.pauseDownload())
        .doOnNext(app -> appsManager.pauseDownload(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void getDownloads() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(computation)
        .flatMap(__ -> appsManager.getDownloadApps())
        .doOnNext(list -> view.showDownloadsList(list))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void getInstalls() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(computation)
        .flatMap(__ -> appsManager.getInstalledApps())
        .observeOn(viewScheduler)
        .doOnNext(installedApps -> view.showInstalledApps(installedApps))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void getUpdatesList() {

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(__ -> appsManager.getUpdatesList())
        .doOnNext(list -> view.showUpdatesList(list))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          crashReport.log(err);
        });
  }
}
