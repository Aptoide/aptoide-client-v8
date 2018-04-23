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
  private final Scheduler computation;
  private final CrashReport crashReport;
  private final PermissionManager permissionManager;
  private final PermissionService permissionService;
  private final AptoideAccountManager accountManager;
  private final AppsNavigator appsNavigator;

  public AppsPresenter(AppsFragmentView view, AppsManager appsManager, Scheduler viewScheduler,
      Scheduler computation, CrashReport crashReport, PermissionManager permissionManager,
      PermissionService permissionService, AptoideAccountManager accountManager,
      AppsNavigator appsNavigator) {
    this.view = view;
    this.appsManager = appsManager;
    this.viewScheduler = viewScheduler;
    this.computation = computation;
    this.crashReport = crashReport;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
    this.accountManager = accountManager;
    this.appsNavigator = appsNavigator;
  }

  @Override public void present() {

    getAvailableUpdatesList();

    getInstalledApps();

    getDownloads();

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

    handleUpdateCardLongClick();

    observeExcludedUpdates();

    loadUserImage();

    handleUserAvatarClick();

    observeDownloadInstallations();

    handleBottomNavigationEvents();

    handleRefreshApps();

    removeInstalledUpdates();
  }

  private void removeInstalledUpdates() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(computation)
        .flatMap(__ -> appsManager.getInstalledUpdateApps())
        .observeOn(viewScheduler)
        .filter(installedUpdatesList -> !installedUpdatesList.isEmpty())
        .doOnNext(installedUpdatesList -> view.removeInstalledUpdates(installedUpdatesList))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          crashReport.log(error);
        });
  }

  private void handleRefreshApps() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(created -> view.refreshApps()
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
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(computation)
        .flatMap(__ -> appsManager.getInstalledDownloads())
        .observeOn(viewScheduler)
        .doOnNext(installedDownloadsList -> view.removeInstalledDownloads(installedDownloadsList))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleUpdateCardClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.updateClick())
        .doOnNext(app -> appsNavigator.navigateToAppView(((UpdateApp) app).getAppId(),
            ((UpdateApp) app).getPackageName()))
        .doOnNext(__ -> appsManager.setAppViewAnalyticsEvent())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void observeExcludedUpdates() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(computation)
        .flatMap(__ -> appsManager.getUpdatesList(true))
        .observeOn(viewScheduler)
        .doOnNext(excludedUpdatesList -> view.removeExcludedUpdates(excludedUpdatesList))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          crashReport.log(error);
        });
  }

  private void handleUpdateCardLongClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.updateLongClick())
        .doOnNext(app -> view.showIgnoreUpdate())
        .flatMap(app -> view.ignoreUpdate()
            .flatMap(__ -> appsManager.excludeUpdate(app)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          view.showUnknownErrorMessage();
          crashReport.log(error);
        });
  }

  private void observeUpdatesList() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(computation)
        .flatMap(__ -> appsManager.getUpdateDownloadsList())
        .observeOn(viewScheduler)
        .doOnNext(list -> view.showUpdatesDownloadList(list))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleResumeUpdateClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> Observable.merge(view.resumeUpdate(), view.retryUpdate()))
        .flatMapCompletable(app -> appsManager.resumeUpdate(app))
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

  private void handleUpdateAppClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.updateApp()
            .flatMap(app -> permissionManager.requestExternalStoragePermission(permissionService)
                .flatMap(success -> {
                  if (appsManager.showWarning()) {
                    return view.showRootWarning()
                        .doOnNext(answer -> appsManager.storeRootAnswer(answer));
                  }
                  return Observable.just(true);
                })
                .flatMap(__2 -> permissionManager.requestDownloadAccess(permissionService))
                .flatMapCompletable(__3 -> appsManager.updateApp(app)))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          crashReport.log(error);
        });
  }

  private void handleBottomNavigationEvents() {
    view.getLifecycle()
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
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> view.updateAll()
            .flatMap(__ -> permissionManager.requestExternalStoragePermission(permissionService))
            .retry())
        .observeOn(computation)
        .flatMapCompletable(app -> appsManager.updateAll())
        .observeOn(viewScheduler)
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
        .doOnNext(app -> view.removeCanceledDownload(app))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleResumeDownloadClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(created -> Observable.merge(view.resumeDownload(), view.retryDownload())
            .flatMap(app -> permissionManager.requestExternalStoragePermission(permissionService)
                .flatMap(success -> permissionManager.requestDownloadAccess(permissionService))
                .flatMapCompletable(__ -> appsManager.resumeDownload(app)))
            .retry())
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

  private void getAvailableUpdatesList() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(__ -> appsManager.getUpdatesList(false))
        .observeOn(viewScheduler)
        .doOnNext(list -> view.showUpdatesList(list))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void loadUserImage() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> accountManager.accountStatus()
            .first())
        .flatMap(account -> getUserAvatar(account))
        .observeOn(viewScheduler)
        .doOnNext(userAvatarUrl -> {
          if (userAvatarUrl != null) {
            view.setUserImage(userAvatarUrl);
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
    view.getLifecycle()
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
