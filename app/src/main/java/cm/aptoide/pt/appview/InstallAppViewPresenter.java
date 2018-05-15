package cm.aptoide.pt.appview;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.app.AppViewManager;
import cm.aptoide.pt.app.DownloadAppViewModel;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;

/**
 * Created by filipegoncalves on 5/7/18.
 */

public class InstallAppViewPresenter implements Presenter {

  private final InstallAppView view;
  private final PermissionManager permissionManager;
  private final PermissionService permissionService;
  private Scheduler viewScheduler;
  private AppViewManager appViewManager;
  private String md5 = "e900c63e3ca3da65f7c4aa4390b1304c";
  private String packageName = "de.autodoc.gmbh";
  private int versionCode = 149;
  private long appId = 37032862;

  public InstallAppViewPresenter(InstallAppView view, AppViewManager appViewManager,
      PermissionManager permissionManager, PermissionService permissionService,
      Scheduler viewScheduler) {
    this.view = view;
    this.appViewManager = appViewManager;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {
    getApp();
    handleAppButtonClick();
    pauseDownload();
  }

  private void pauseDownload() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.pauseDownload()
            .flatMapCompletable(__ -> appViewManager.pauseDownload(md5))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }

  private void handleAppButtonClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.installAppClick()
            .flatMapCompletable(action -> {
              Completable completable = null;
              switch (action) {
                case INSTALL:
                case UPDATE:
                  completable = downloadApp(action);
                  break;
                case OPEN:
                  completable = openInstalledApp();
                  break;
                case DOWNGRADE:
                  completable = downgradeApp(action);
                  break;
                default:
                  completable =
                      Completable.error(new IllegalArgumentException("Invalid type of action"));
              }
              return completable;
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }

  private Completable downgradeApp(DownloadAppViewModel.Action action) {
    return view.showDowngradeMessage()
        .filter(downgrade -> downgrade)
        .doOnNext(__ -> view.showDowngradingMessage())
        .flatMapCompletable(__ -> downloadApp(action))
        .toCompletable();
  }

  private Completable openInstalledApp() {
    return Completable.fromAction(() -> view.openApp(packageName));
  }

  private Completable downloadApp(DownloadAppViewModel.Action action) {
    return Observable.defer(() -> {
      if (appViewManager.showRootInstallWarningPopup()) {
        return view.showRootInstallWarningPopup()
            .doOnNext(answer -> appViewManager.saveRootInstallWarning(answer))
            .map(__ -> action);
      }
      return Observable.just(action);
    })
        .flatMap(__ -> permissionManager.requestDownloadAccess(permissionService)
            .flatMap(
                success -> permissionManager.requestExternalStoragePermission(permissionService))
            .flatMapCompletable(__1 -> appViewManager.downloadApp(action, packageName, appId)))
        .toCompletable();
  }

  public void getApp() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(__ -> appViewManager.getDownloadAppViewModel(md5, packageName, versionCode)
            .observeOn(viewScheduler)
            .doOnNext(model -> view.showDownloadAppModel(model))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }
}
