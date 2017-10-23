package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Build;
import android.os.Bundle;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareWaitingToReceiveView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareWaitingToReceivePresenter implements Presenter {

  private SpotAndShareWaitingToReceiveView view;
  private SpotAndShare spotAndShare;
  private final PermissionManager permissionManager;
  private final PermissionService permissionService;
  private final CrashReport crashReport;

  public SpotAndShareWaitingToReceivePresenter(SpotAndShareWaitingToReceiveView view,
      SpotAndShare spotAndShare, PermissionManager permissionManager,
      PermissionService permissionService, CrashReport crashReport) {
    this.view = view;
    this.spotAndShare = spotAndShare;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
    this.crashReport = crashReport;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(__ -> {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return permissionManager.requestLocationAndExternalStoragePermission(permissionService)
                .flatMap(accessToLocation -> permissionManager.requestWriteSettingsPermission(
                    permissionService))
                .flatMap(__1 -> permissionManager.requestLocationEnabling(permissionService))
                .doOnError(throwable -> view.navigateBackAllowingStateLoss());
          } else {
            return permissionManager.requestLocationEnabling(permissionService);
          }
        })
        .doOnNext(__ -> joinGroup())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.backButtonEvent())
        .doOnNext(click -> view.showExitWarning())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.exitEvent())
        .doOnNext(clicked -> leaveGroup())
        .doOnNext(__ -> view.navigateBack())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void leaveGroup() {
    spotAndShare.leaveGroup(err -> view.onLeaveGroupError());
  }

  private void joinGroup() {
    view.joinGroup();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
