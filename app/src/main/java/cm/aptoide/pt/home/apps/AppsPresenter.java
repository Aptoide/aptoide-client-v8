package cm.aptoide.pt.home.apps;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.home.apps.model.AppcUpdateApp;
import cm.aptoide.pt.home.apps.model.DownloadApp;
import cm.aptoide.pt.home.apps.model.UpdateApp;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Observable;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsPresenter implements Presenter {

  private final AppsFragmentView view;
  private final AppsManager appsManager;
  private final Scheduler viewScheduler;
  private final Scheduler ioScheduler;
  private final CrashReport crashReport;
  private final PermissionManager permissionManager;
  private final PermissionService permissionService;
  private final AptoideAccountManager accountManager;
  private final AppsNavigator appsNavigator;

  public AppsPresenter(AppsFragmentView view, AppsManager appsManager, Scheduler viewScheduler,
      Scheduler ioScheduler, CrashReport crashReport, PermissionManager permissionManager,
      PermissionService permissionService, AptoideAccountManager accountManager,
      AppsNavigator appsNavigator) {
    this.view = view;
    this.appsManager = appsManager;
    this.viewScheduler = viewScheduler;
    this.ioScheduler = ioScheduler;
    this.crashReport = crashReport;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
    this.accountManager = accountManager;
    this.appsNavigator = appsNavigator;
  }

  @Override public void present() {

    observeAppModelState();

    //handleAppcUpgradesSeeMoreClick();

    handlePauseDownloadClick();

    //handleResumeDownloadClick();

    //handleCancelDownloadClick();

    handleInstallAppClick();

    handleUpdateAllClick();

    //handleUpdateAppClick();

    //handlePauseUpdateClick();

    //handleCancelUpdateClick();

    //handleResumeUpdateClick();

    handleUpdateCardClick();

    handleUpdateCardLongClick();

    loadUserImage();

    handleUserAvatarClick();

    handleBottomNavigationEvents();

    handleRefreshApps();

    handleNavigateToAppViewWithDownload();
  }

  private void handleNavigateToAppViewWithDownload() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.startDownloadInAppview())
        .doOnNext(app -> appsNavigator.navigateToAppViewAndInstall(((UpdateApp) app).getAppId(),
            ((UpdateApp) app).getPackageName()))
        .doOnNext(__ -> appsManager.setMigrationAppViewAnalyticsEvent())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleRefreshApps() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.refreshApps()
            .observeOn(ioScheduler)
            .flatMapCompletable(__ -> appsManager.refreshAllUpdates()
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

  private void handleUpdateCardClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(__ -> view.cardClick())
        .doOnNext(app -> {
          if (app.getType() == App.Type.DOWNLOAD) {
            appsNavigator.navigateToAppView(((DownloadApp) app).getMd5());
          } else {
            appsNavigator.navigateToAppView(((UpdateApp) app).getMd5());
          }
        })
        .doOnNext(app -> {
          if (app instanceof AppcUpdateApp) {
            appsManager.setMigrationAppViewAnalyticsEvent();
          } else {
            appsManager.setAppViewAnalyticsEvent();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleUpdateCardLongClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(__ -> view.updateLongClick())
        .doOnNext(app -> view.showIgnoreUpdate())
        .flatMap(app -> view.ignoreUpdate()
            .observeOn(ioScheduler)
            .flatMap(__ -> appsManager.excludeUpdate(app)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          view.showUnknownErrorMessage();
          crashReport.log(error);
        });
  }

  //private void handleResumeUpdateClick() {
  //  view.getLifecycleEvent()
  //      .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
  //      .observeOn(viewScheduler)
  //      .flatMap(created -> Observable.merge(view.resumeUpdate(), view.retryUpdate()))
  //      .doOnNext(this::setStandbyState)
  //      .observeOn(ioScheduler)
  //      .flatMapCompletable(
  //          appClickEventWrapper -> appsManager.resumeUpdate(appClickEventWrapper.getApp()))
  //      .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
  //      .subscribe(created -> {
  //      }, error -> crashReport.log(error));
  //}

  //private void handleCancelUpdateClick() {
  //  view.getLifecycleEvent()
  //      .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
  //      .observeOn(viewScheduler)
  //      .flatMap(created -> view.cancelUpdate())
  //      .doOnNext(this::removeCanceledAppDownload)
  //      .observeOn(ioScheduler)
  //      .doOnNext(appClickEventWrapper -> appsManager.cancelUpdate(appClickEventWrapper.getApp()))
  //      .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
  //      .subscribe(created -> {
  //      }, error -> crashReport.log(error));
  //}

  //private void handlePauseUpdateClick() {
  //  view.getLifecycleEvent()
  //      .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
  //      .observeOn(viewScheduler)
  //      .flatMap(created -> view.pauseUpdate())
  //      .observeOn(ioScheduler)
  //      .flatMapCompletable(
  //          appClickEventWrapper -> appsManager.pauseUpdate(appClickEventWrapper.getApp()))
  //      .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
  //      .subscribe(created -> {
  //      }, error -> crashReport.log(error));
  //}

  //private void handleUpdateAppClick() {
  //  view.getLifecycleEvent()
  //      .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
  //      .observeOn(viewScheduler)
  //      .flatMap(created -> view.updateApp()
  //          .flatMap(appClickEventWrapper -> permissionManager.requestExternalStoragePermission(
  //              permissionService)
  //              .flatMap(success -> {
  //                if (appsManager.showWarning()) {
  //                  return view.showRootWarning()
  //                      .doOnNext(answer -> appsManager.storeRootAnswer(answer));
  //                }
  //                return Observable.just(true);
  //              })
  //              .flatMap(__2 -> permissionManager.requestDownloadAccess(permissionService))
  //              .doOnNext(__ -> setStandbyState(appClickEventWrapper))
  //              .observeOn(ioScheduler)
  //              .flatMapCompletable(__3 -> appsManager.updateApp(appClickEventWrapper.getApp(),
  //                  appClickEventWrapper.isAppcUpgrade())))
  //          .retry())
  //      .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
  //      .subscribe(created -> {
  //      }, error -> {
  //        crashReport.log(error);
  //      });
  //}

  private void handleBottomNavigationEvents() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> appsNavigator.bottomNavigation())
        .observeOn(viewScheduler)
        .doOnNext(navigated -> view.scrollToTop())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleUpdateAllClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.updateAll()
            .flatMap(__ -> permissionManager.requestExternalStoragePermission(permissionService))
            .retry())
        //.doOnNext(__ -> view.showIndeterminateAllUpdates())
        .observeOn(ioScheduler)
        .flatMapCompletable(app -> appsManager.updateAll())
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleInstallAppClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.installApp())
        .observeOn(ioScheduler)
        .flatMapCompletable(app -> appsManager.installApp(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleCancelDownloadClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.cancelDownload())
        .observeOn(ioScheduler)
        .doOnNext(app -> appsManager.cancelDownload(app))
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  //private void handleResumeDownloadClick() {
  //  view.getLifecycleEvent()
  //      .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
  //      .flatMap(created -> Observable.merge(view.resumeDownload(), view.retryDownload())
  //          .observeOn(viewScheduler)
  //          .flatMap(app -> permissionManager.requestExternalStoragePermission(permissionService)
  //              .flatMap(success -> permissionManager.requestDownloadAccess(permissionService))
  //              .observeOn(ioScheduler)
  //              .flatMapCompletable(__ -> appsManager.resumeDownload(app)))
  //          .retry())
  //      .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
  //      .subscribe(created -> {
  //      }, error -> crashReport.log(error));
  //}

  private void handlePauseDownloadClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.pauseDownload())
        .observeOn(ioScheduler)
        .flatMapCompletable(app -> appsManager.pauseDownload(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void observeAppModelState() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(ioScheduler)
        .flatMap(__ -> getAppsModel())
        .observeOn(viewScheduler)
        .doOnNext(view::showModel)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private Observable<AppsModel> getAppsModel() {
    return Observable.combineLatest(appsManager.getDownloadApps(), appsManager.getInstalledApps(),
        appsManager.getUpdatesList(), appsManager.getAppcUpgradesList(),
        (downloadApps, installedApps, updateApps, appcApps) -> new AppsModel(updateApps,
            installedApps, appcApps, downloadApps));
  }

  //private void handleAppcUpgradesSeeMoreClick() {
  //  view.getLifecycleEvent()
  //      .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
  //      .flatMap(list -> view.moreAppcClick()
  //          .doOnNext(__ -> appsNavigator.navigateToSeeMoreAppc())
  //          .retry())
  //      .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
  //      .subscribe(__ -> {
  //      }, throwable -> {
  //        throw new OnErrorNotImplementedException(throwable);
  //      });
  //}

  private void loadUserImage() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> accountManager.accountStatus())
        .flatMap(account -> getUserAvatar(account))
        .observeOn(viewScheduler)
        .doOnNext(userAvatarUrl -> {
          if (userAvatarUrl != null) {
            view.setUserImage(userAvatarUrl);
          } else {
            view.setDefaultUserImage();
          }
          view.showAvatar();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleUserAvatarClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.imageClick()
            .observeOn(viewScheduler)
            .doOnNext(account -> appsNavigator.navigateToMyAccount())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private Observable<String> getUserAvatar(Account account) {
    String userAvatarUrl = null;
    if (account != null && account.isLoggedIn()) {
      userAvatarUrl = account.getAvatar();
    }
    return Observable.just(userAvatarUrl);
  }
}
