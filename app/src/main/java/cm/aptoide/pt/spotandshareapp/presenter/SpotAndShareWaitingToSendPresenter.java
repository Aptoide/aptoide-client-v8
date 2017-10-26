package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Build;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.AppModelToAndroidAppInfoMapper;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareWaitingToSendView;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 07-07-2017.
 */

public class SpotAndShareWaitingToSendPresenter implements Presenter {
  private boolean shouldCreateGroup;
  private final boolean noAppSelected;
  private SpotAndShareWaitingToSendView view;
  private SpotAndShare spotAndShare;
  private final AppModelToAndroidAppInfoMapper appModelToAndroidAppInfoMapper;
  private final PermissionManager permissionManager;
  private final PermissionService permissionService;
  private final CrashReport crashReport;

  public SpotAndShareWaitingToSendPresenter(boolean shouldCreateGroup, boolean noAppSelected,
      SpotAndShareWaitingToSendView view, SpotAndShare spotAndShare,
      AppModelToAndroidAppInfoMapper appModelToAndroidAppInfoMapper,
      PermissionManager permissionManager, PermissionService permissionService,
      CrashReport crashReport) {
    this.shouldCreateGroup = shouldCreateGroup;
    this.noAppSelected = noAppSelected;
    this.view = view;
    this.spotAndShare = spotAndShare;
    this.appModelToAndroidAppInfoMapper = appModelToAndroidAppInfoMapper;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
    this.crashReport = crashReport;
  }

  @Override public void present() {

    if (shouldCreateGroup) {
      view.getLifecycle()
          .filter(event -> event.equals(View.LifecycleEvent.CREATE))
          .delay(1, TimeUnit.SECONDS)
          .observeOn(AndroidSchedulers.mainThread())
          .flatMap(__ -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              return permissionManager.requestLocationAndExternalStoragePermission(
                  permissionService)
                  .flatMap(accessToLocation -> permissionManager.requestWriteSettingsPermission(
                      permissionService))
                  .flatMap(locationResult -> permissionManager.requestLocationEnabling(
                      permissionService))
                  .doOnError(throwable -> view.navigateBackWithStateLoss());
            } else {
              return permissionManager.requestLocationEnabling(permissionService);
            }
          })
          .flatMapSingle(lifecycleEvent -> {

            return createGroup().timeout(15, TimeUnit.SECONDS)
                .toSingleDefault(2);
            //// FIXME: 12-07-2017 should not pass this integer

          })
          .doOnError(throwable -> handleError(throwable))
          .doOnNext(__ -> startListeningToFriends())
          .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
          .subscribe(__ -> {
          }, err -> crashReport.log(err));
    } else {

      view.getLifecycle()
          .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
          .doOnNext(created -> startListeningToFriends())
          .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
          .subscribe(created -> {
          }, error -> crashReport.log(error));
    }

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

  private void startListeningToFriends() {
    spotAndShare.observeFriends()
        .filter(friendsList -> friendsList.size() > 0)
        .doOnNext(friendsList -> sendApp())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleError(Throwable throwable) {
    spotAndShare.leaveGroup(err -> view.onLeaveGroupError());
    if (throwable instanceof TimeoutException) {
      view.showTimeoutCreateGroupError();
    } else {
      view.showGeneralCreateGroupError();
    }
    view.navigateBack();
  }

  private Completable createGroup() {
    return spotAndShare.createGroup(uuid -> {
    }, throwable -> handleError(throwable), null);
  }

  private void sendApp() {
    if (!noAppSelected) {
      AndroidAppInfo androidAppInfo =
          appModelToAndroidAppInfoMapper.convertAppModelToAndroidAppInfo(view.getSelectedApp());
      AptoideUtils.ThreadU.runOnIoThread(
          () -> spotAndShare.sendApps(Collections.singletonList(androidAppInfo)));
    }
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

}
