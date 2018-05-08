package cm.aptoide.pt.appview;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.app.AppViewManager;
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
        .flatMap(create -> view.installAppClick()
            .doOnNext(click -> appViewManager.increaseInstallClick())
            .flatMap(__1 -> {
              if (appViewManager.showRootInstallWarningPopup()) {
                return view.showRootInstallWarningPopup()
                    .doOnNext(answer -> appViewManager.saveRootInstallWarning(answer));
              }
              return Observable.just(true);
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
        });
  }
}
