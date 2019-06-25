package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Observable;
import rx.Scheduler;

public class SeeMoreAppcPresenter implements Presenter {

  private final SeeMoreAppcView view;
  private final Scheduler viewScheduler;
  private final Scheduler ioScheduler;
  private final CrashReport crashReport;
  private final SeeMoreAppcManager seeMoreAppcManager;
  private final PermissionManager permissionManager;
  private final PermissionService permissionService;

  public SeeMoreAppcPresenter(SeeMoreAppcView view, Scheduler viewScheduler, Scheduler ioScheduler,
      CrashReport crashReport, PermissionManager permissionManager,
      PermissionService permissionService, SeeMoreAppcManager seeMoreAppcManager) {
    this.view = view;
    this.viewScheduler = viewScheduler;
    this.ioScheduler = ioScheduler;
    this.crashReport = crashReport;
    this.seeMoreAppcManager = seeMoreAppcManager;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
  }

  @Override public void present() {
    handleRefreshApps();

    observeAppcUpgradesList();

    getAvailableAppcUpgradesList();

    handleAppcUpgradeAppClick();

    handlePauseAppcUpgradeClick();

    handleCancelAppcUpgradeClick();

    handleResumeAppcUpgradeClick();
  }

  private void handleRefreshApps() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.refreshApps()
            .observeOn(ioScheduler)
            .flatMapCompletable(__ -> seeMoreAppcManager.refreshAllUpdates()
                .observeOn(viewScheduler)
                .doOnCompleted(() -> view.hidePullToRefresh())
                .doOnError(throwable -> {
                  view.hidePullToRefresh();
                  throwable.printStackTrace();
                }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
          view.hidePullToRefresh();
        }, error -> {
          view.hidePullToRefresh();
          crashReport.log(error);
        });
  }

  private void getAvailableAppcUpgradesList() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(ioScheduler)
        .flatMap(__ -> seeMoreAppcManager.getAppcUpgradesList(false))
        .observeOn(viewScheduler)
        .doOnNext(list -> view.showAppcUpgradesList(list))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void observeAppcUpgradesList() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(ioScheduler)
        .flatMap(__ -> seeMoreAppcManager.getAppcUpgradeDownloadsList())
        .observeOn(viewScheduler)
        .doOnNext(list -> view.showAppcUpgradesDownloadList(list))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleAppcUpgradeAppClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.upgradeAppcApp()
            .flatMap(app -> permissionManager.requestExternalStoragePermission(permissionService)
                .flatMap(success -> {
                  if (seeMoreAppcManager.showWarning()) {
                    return view.showRootWarning()
                        .doOnNext(answer -> seeMoreAppcManager.storeRootAnswer(answer));
                  }
                  return Observable.just(true);
                })
                .flatMap(__2 -> permissionManager.requestDownloadAccess(permissionService))
                .doOnNext(__ -> view.setAppcStandbyState(app))
                .observeOn(ioScheduler)
                .flatMapCompletable(__3 -> seeMoreAppcManager.updateApp(app)))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          crashReport.log(error);
        });
  }

  private void handleResumeAppcUpgradeClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> Observable.merge(view.resumeAppcUpgrade(), view.retryAppcUpgrade()))
        .doOnNext(app -> view.setAppcStandbyState(app))
        .observeOn(ioScheduler)
        .flatMapCompletable(app -> seeMoreAppcManager.resumeUpdate(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleCancelAppcUpgradeClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.cancelAppcUpgrade())
        .doOnNext(app -> view.removeAppcCanceledAppDownload(app))
        .observeOn(ioScheduler)
        .doOnNext(app -> seeMoreAppcManager.cancelUpdate(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handlePauseAppcUpgradeClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.pauseAppcUpgrade())
        .doOnNext(app -> view.setAppcPausingDownloadState(app))
        .observeOn(ioScheduler)
        .flatMapCompletable(app -> seeMoreAppcManager.pauseUpdate(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }
}
