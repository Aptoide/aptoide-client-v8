package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Observable;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsPresenter implements Presenter {

  private AppsFragmentView view;
  private AppsManager appsManager;
  private Scheduler viewScheduler;
  private Scheduler computation;
  private CrashReport crashReport;
  private PermissionManager permissionManager;
  private PermissionService permissionService;

  public AppsPresenter(AppsFragmentView view, AppsManager appsManager, Scheduler viewScheduler,
      Scheduler computation, CrashReport crashReport, PermissionManager permissionManager,
      PermissionService permissionService) {
    this.view = view;
    this.appsManager = appsManager;
    this.viewScheduler = viewScheduler;
    this.computation = computation;
    this.crashReport = crashReport;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
  }

  @Override public void present() {

    getUpdatesList();

    getInstalledApps();

    getDownloads();

    handlePauseDownloadClick();

    handleResumeDownloadClick();

    handleCancelDownloadClick();

    handleInstallAppClick();

    handleUpdateAllClick();

    handleUpdateClick();

    handlePauseUpdateClick();

    handleCancelUpdateClick();

    handleResumeUpdateClick();

    handleRetryUpdateClick();
  }

  private void handleRetryUpdateClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.retryUpdate())
        .doOnNext(app -> appsManager.retryUpdate(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleResumeUpdateClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.resumeUpdate())
        .doOnNext(app -> appsManager.resumeUpdate(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleCancelUpdateClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.cancelUpdate())
        .doOnNext(app -> appsManager.cancelUpdate(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handlePauseUpdateClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.pauseUpdate())
        .doOnNext(app -> appsManager.pauseUpdate(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleUpdateClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.updateApp()
            .flatMap(app -> permissionManager.requestExternalStoragePermission(permissionService)
                .flatMap(__ -> {
                  if (appsManager.showWarning()) {
                    return view.showRootWarning()
                        .doOnNext(answer -> appsManager.storeRootAnswer(answer));
                  }
                  return Observable.just(true);
                })
                .flatMap(__2 -> permissionManager.requestDownloadAccess(permissionService))
                .flatMapCompletable(__3 -> appsManager.updateApp(app))))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private void handleUpdateAllClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.retryUpdate())
        .doOnNext(app -> appsManager.retryUpdate(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleInstallAppClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.installApp())
        .flatMapCompletable(app -> appsManager.installApp(app))
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
        .flatMap(created -> Observable.merge(view.resumeDownload(), view.retryDownload()))
        .flatMap(app -> permissionManager.requestExternalStoragePermission(permissionService)
            .flatMap(success -> permissionManager.requestDownloadAccess(permissionService))
            .flatMapCompletable(__ -> appsManager.resumeDownload(app)))
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
        .observeOn(viewScheduler)
        .doOnNext(list -> view.showDownloadsList(list))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void getInstalledApps() {
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
        .observeOn(viewScheduler)
        .doOnNext(list -> view.showUpdatesList(list))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          crashReport.log(err);
        });
  }
}
