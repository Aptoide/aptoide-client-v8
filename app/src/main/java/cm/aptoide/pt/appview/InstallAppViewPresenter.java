package cm.aptoide.pt.appview;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.app.AppViewManager;
import cm.aptoide.pt.app.DownloadAppViewModel;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Observable;

/**
 * Created by filipegoncalves on 5/7/18.
 */

public class InstallAppViewPresenter implements Presenter {

  private final InstallAppView view;
  private final PermissionManager permissionManager;
  private final PermissionService permissionService;
  private AppViewManager appViewManager;
  private String md5 = "e900c63e3ca3da65f7c4aa4390b1304c";
  private String packageName = "de.autodoc.gmbh";
  private int versionCode = 149;

  public InstallAppViewPresenter(InstallAppView view, AppViewManager appViewManager,
      PermissionManager permissionManager, PermissionService permissionService) {
    this.view = view;
    this.appViewManager = appViewManager;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
  }

  @Override public void present() {
    getApp();
    installApp();
  }

  private void installApp() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(create -> view.installAppClick()
            .filter(action -> action.equals(DownloadAppViewModel.Action.INSTALL))
            .flatMap(__1 -> {
              if (appViewManager.showRootInstallWarningPopup()) {
                return view.showRootInstallWarningPopup()
                    .doOnNext(answer -> appViewManager.saveRootInstallWarning(answer));
              }
              return Observable.just(true);
            })
            .flatMap(rootDialog -> permissionManager.requestDownloadAccess(permissionService)
                .flatMap(success -> permissionManager.requestExternalStoragePermission(
                    permissionService))
                .doOnNext(__ -> appViewManager.downloadApp()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }

  public void getApp() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(__ -> appViewManager.getDownloadAppViewModel(md5, packageName, versionCode))
        .doOnNext(model -> view.showDownloadAppModel(model))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }
}
