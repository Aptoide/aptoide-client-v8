package cm.aptoide.pt.home.apps;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
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

    getAvailableUpdatesList();

    getAvailableAppcUpgradesList();

    getInstalledApps();

    getDownloads();

    handleAppcUpgradesSeeMoreClick();

    handlePauseDownloadClick();

    handleResumeDownloadClick();

    handleCancelDownloadClick();

    handleInstallAppClick();

    handleUpdateAllClick();

    handleUpdateAppClick();

    handlePauseUpdateClick();

    handleCancelUpdateClick();

    handleResumeUpdateClick();

    handleUpdateCardClick();

    observeUpdatesList();

    observeAppcUpgradesList();

    handleUpdateCardLongClick();

    observeExcludedUpdates();

    observeExcludedAppcUpgrades();

    loadUserImage();

    handleUserAvatarClick();

    observeDownloadInstallations();

    handleBottomNavigationEvents();

    handleRefreshApps();
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

  private void observeDownloadInstallations() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(ioScheduler)
        .flatMap(__ -> appsManager.getInstalledDownloads())
        .observeOn(viewScheduler)
        .doOnNext(installedDownloadsList -> view.removeInstalledDownloads(installedDownloadsList))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleUpdateCardClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(__ -> view.updateClick())
        .doOnNext(app -> appsNavigator.navigateToAppView(((UpdateApp) app).getAppId(),
            ((UpdateApp) app).getPackageName()))
        .doOnNext(__ -> appsManager.setAppViewAnalyticsEvent())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void observeExcludedUpdates() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(ioScheduler)
        .flatMap(__ -> appsManager.getUpdatesList(true))
        .distinctUntilChanged()
        .filter(excludedUpdatesList -> !excludedUpdatesList.isEmpty())
        .observeOn(viewScheduler)
        .doOnNext(excludedUpdatesList -> view.removeExcludedUpdates(excludedUpdatesList))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          crashReport.log(error);
        });
  }

  private void observeExcludedAppcUpgrades() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(ioScheduler)
        .flatMap(__ -> appsManager.getAppcUpgradesList(true))
        .distinctUntilChanged()
        .filter(excludedUpdatesList -> !excludedUpdatesList.isEmpty())
        .observeOn(viewScheduler)
        .doOnNext(excludedUpdatesList -> view.removeExcludedAppcUpgrades(excludedUpdatesList))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          crashReport.log(error);
        });
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

  private void observeUpdatesList() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(ioScheduler)
        .flatMap(__ -> appsManager.getUpdateDownloadsList())
        .observeOn(viewScheduler)
        .doOnNext(list -> view.showUpdatesDownloadList(list))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void observeAppcUpgradesList() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(ioScheduler)
        .flatMap(__ -> appsManager.getAppcUpgradeDownloadsList())
        .observeOn(viewScheduler)
        .doOnNext(list -> view.showAppcUpgradesDownloadList(list))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleResumeUpdateClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> Observable.merge(view.resumeUpdate(), view.retryUpdate()))
        .doOnNext(this::setStandbyState)
        .observeOn(ioScheduler)
        .flatMapCompletable(
            appClickEventWrapper -> appsManager.resumeUpdate(appClickEventWrapper.getApp()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void setStandbyState(AppClickEventWrapper appClickEventWrapper) {
    App app = appClickEventWrapper.getApp();
    if (appClickEventWrapper.isAppcUpgrade()) {
      view.setAppcStandbyState(app);
    } else {
      view.setStandbyState(app);
    }
  }

  private void handleCancelUpdateClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.cancelUpdate())
        .doOnNext(this::removeCanceledAppDownload)
        .observeOn(ioScheduler)
        .doOnNext(appClickEventWrapper -> appsManager.cancelUpdate(appClickEventWrapper.getApp()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void removeCanceledAppDownload(AppClickEventWrapper appClickEventWrapper) {
    App app = appClickEventWrapper.getApp();
    if (appClickEventWrapper.isAppcUpgrade()) {
      view.removeAppcCanceledAppDownload(app);
    } else {
      view.removeCanceledAppDownload(app);
    }
  }

  private void handlePauseUpdateClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.pauseUpdate())
        .doOnNext(appClickEventWrapper -> {
          App app = appClickEventWrapper.getApp();
          if (appClickEventWrapper.isAppcUpgrade()) {
            view.setAppcPausingDownloadState(app);
          } else {
            view.setPausingDownloadState(app);
          }
        })
        .observeOn(ioScheduler)
        .flatMapCompletable(
            appClickEventWrapper -> appsManager.pauseUpdate(appClickEventWrapper.getApp()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleUpdateAppClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.updateApp()
            .flatMap(appClickEventWrapper -> permissionManager.requestExternalStoragePermission(
                permissionService)
                .flatMap(success -> {
                  if (appsManager.showWarning()) {
                    return view.showRootWarning()
                        .doOnNext(answer -> appsManager.storeRootAnswer(answer));
                  }
                  return Observable.just(true);
                })
                .flatMap(__2 -> permissionManager.requestDownloadAccess(permissionService))
                .doOnNext(__ -> setStandbyState(appClickEventWrapper))
                .observeOn(ioScheduler)
                .flatMapCompletable(__3 -> appsManager.updateApp(appClickEventWrapper.getApp(),
                    appClickEventWrapper.isAppcUpgrade())))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          crashReport.log(error);
        });
  }

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
        .doOnNext(__ -> view.showIndeterminateAllUpdates())
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
        .doOnNext(app -> view.removeCanceledAppDownload(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleResumeDownloadClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> Observable.merge(view.resumeDownload(), view.retryDownload())
            .doOnNext(app -> view.setStandbyState(app))
            .observeOn(viewScheduler)
            .flatMap(app -> permissionManager.requestExternalStoragePermission(permissionService)
                .flatMap(success -> permissionManager.requestDownloadAccess(permissionService))
                .observeOn(ioScheduler)
                .flatMapCompletable(__ -> appsManager.resumeDownload(app)))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handlePauseDownloadClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.pauseDownload())
        .doOnNext(app -> view.setPausingDownloadState(app))
        .observeOn(ioScheduler)
        .flatMapCompletable(app -> appsManager.pauseDownload(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void getDownloads() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(ioScheduler)
        .flatMap(__ -> appsManager.getDownloadApps())
        .observeOn(viewScheduler)
        .doOnNext(list -> view.showDownloadsList(list))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void getInstalledApps() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(ioScheduler)
        .flatMap(__ -> appsManager.getInstalledApps())
        .observeOn(viewScheduler)
        .doOnNext(installedApps -> view.showInstalledApps(installedApps))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void getAvailableUpdatesList() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(ioScheduler)
        .flatMap(__ -> appsManager.getUpdatesList(false))
        .observeOn(viewScheduler)
        .doOnNext(list -> view.showUpdatesList(list))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void getAvailableAppcUpgradesList() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(ioScheduler)
        .flatMap(__ -> appsManager.getAppcUpgradesList(false))
        .observeOn(viewScheduler)
        .doOnNext(list -> view.showAppcUpgradesList(list))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleAppcUpgradesSeeMoreClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(list -> view.moreAppcClick()
            .doOnNext(__ -> appsNavigator.navigateToSeeMoreAppc())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

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
