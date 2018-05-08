package cm.aptoide.pt.appview;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.app.AppViewManager;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;

/**
 * Created by filipegoncalves on 5/7/18.
 */

public class InstallAppViewPresenter implements Presenter {

  private final InstallAppView view;
  private final PermissionManager permissionManager;
  private final PermissionService permissionService;
  private AppViewManager appViewManager;

  public InstallAppViewPresenter(InstallAppView view, AppViewManager appViewManager,
      PermissionManager permissionManager, PermissionService permissionService) {
    this.view = view;
    this.appViewManager = appViewManager;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
  }

  @Override public void present() {
    installApp();
  }

  private void installApp() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.installAppClick()
            .doOnNext(__1 -> appViewManager.increaseInstallClick())
            //verify on install manager if should show
            .doOnNext(__2 -> view.showRootInstallWarningPopup())

            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }
}
