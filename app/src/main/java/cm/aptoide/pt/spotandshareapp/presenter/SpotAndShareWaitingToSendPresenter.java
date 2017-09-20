package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Build;
import android.os.Bundle;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.AppModelToAndroidAppInfoMapper;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareWaitingToSendView;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.Collections;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 07-07-2017.
 */

public class SpotAndShareWaitingToSendPresenter implements Presenter {
  private SpotAndShareWaitingToSendView view;
  private SpotAndShare spotAndShare;
  private final AppModelToAndroidAppInfoMapper appModelToAndroidAppInfoMapper;
  private final PermissionManager permissionManager;
  private final PermissionService permissionService;

  public SpotAndShareWaitingToSendPresenter(SpotAndShareWaitingToSendView view,
      SpotAndShare spotAndShare, AppModelToAndroidAppInfoMapper appModelToAndroidAppInfoMapper,
      PermissionManager permissionManager, PermissionService permissionService) {
    this.view = view;
    this.spotAndShare = spotAndShare;
    this.appModelToAndroidAppInfoMapper = appModelToAndroidAppInfoMapper;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(__ -> {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return permissionManager.requestLocationAndExternalStoragePermission(permissionService)
                .flatMap(accessToLocation -> permissionManager.requestWriteSettingsPermission(
                    permissionService));
          } else {
            return Observable.empty();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> err.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.backButtonEvent())
        .doOnNext(click -> view.showExitWarning())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.exitEvent())
        .doOnNext(clicked -> leaveGroup())
        .doOnNext(__ -> view.navigateBack())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> spotAndShare.observeFriends())
        .filter(friendsList -> friendsList.size() > 0)
        .doOnNext(friendsList -> sendApp())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  private void sendApp() {
    AndroidAppInfo androidAppInfo =
        appModelToAndroidAppInfoMapper.convertAppModelToAndroidAppInfo(view.getSelectedApp());
    AptoideUtils.ThreadU.runOnIoThread(
        () -> spotAndShare.sendApps(Collections.singletonList(androidAppInfo)));

    view.openTransferRecord();
  }

  private void canSend() {
    if (spotAndShare.canSend()) {
      sendApp();
    }
  }

  private void leaveGroup() {
    spotAndShare.leaveGroup(err -> view.onLeaveGroupError());
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
