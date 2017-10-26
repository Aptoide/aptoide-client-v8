package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Build;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.AppModel;
import cm.aptoide.pt.spotandshareapp.AppModelToAndroidAppInfoMapper;
import cm.aptoide.pt.spotandshareapp.SpotAndShareAppProvider;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareAppSelectionView;
import cm.aptoide.pt.utils.AptoideUtils;
import com.jakewharton.rxrelay.BehaviorRelay;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by filipe on 28-07-2017.
 */

public class SpotAndShareAppSelectionPresenter implements Presenter {

  private final SpotAndShareAppSelectionView view;
  private final SpotAndShare spotAndShare;
  private final boolean shouldCreateGroup;
  private final SpotAndShareAppProvider spotandShareAppProvider;
  private final AppModelToAndroidAppInfoMapper appModelToAndroidAppInfoMapper;
  private final PermissionManager permissionManager;
  private final PermissionService permissionService;
  private final CrashReport crashReport;
  private final BehaviorRelay<Boolean> isGroupCreatedBehaviour;

  public SpotAndShareAppSelectionPresenter(SpotAndShareAppSelectionView view,
      SpotAndShare spotAndShare, boolean shouldCreateGroup,
      SpotAndShareAppProvider spotandShareAppProvider,
      AppModelToAndroidAppInfoMapper appModelToAndroidAppInfoMapper,
      PermissionManager permissionManager, PermissionService permissionService,
      CrashReport crashReport, BehaviorRelay<Boolean> isGroupCreatedBehaviour) {
    this.view = view;
    this.spotAndShare = spotAndShare;
    this.shouldCreateGroup = shouldCreateGroup;
    this.spotandShareAppProvider = spotandShareAppProvider;
    this.appModelToAndroidAppInfoMapper = appModelToAndroidAppInfoMapper;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
    this.crashReport = crashReport;
    this.isGroupCreatedBehaviour = isGroupCreatedBehaviour;
  }

  @Override public void present() {
    createGroup();

    buildInstalledAppsList();

    handleBackButtonClick();

    handleExitEvent();

    handleSkipClick();
  }

  private void handleExitEvent() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.exitEvent())
        .doOnNext(clicked -> leaveGroup())
        .doOnNext(__ -> view.navigateBack())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleBackButtonClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.backButtonEvent())
        .doOnNext(click -> view.showExitWarning())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void buildInstalledAppsList() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(lifecycleEvent -> view.showLoading())
        .observeOn(Schedulers.io())
        .map(lifecycleEvent -> spotandShareAppProvider.getInstalledApps())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(installedApps -> view.buildInstalledAppsList(installedApps))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void handleSkipClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.skipButtonClick())
        .flatMap(__ -> isGroupCreated())
        .filter(isGroupCreated -> isGroupCreated)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.openWaitingToSendScreen())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private Observable<Boolean> isGroupCreated() {
    return isGroupCreatedBehaviour;
  }

  private void createGroup() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .delay(1, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(__ -> {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return permissionManager.requestLocationAndExternalStoragePermission(permissionService)
                .flatMap(accessToLocation -> permissionManager.requestWriteSettingsPermission(
                    permissionService))
                .flatMap(
                    locationResult -> permissionManager.requestLocationEnabling(permissionService))
                .doOnError(throwable -> view.navigateBackWithStateLoss());
          } else {
            return permissionManager.requestLocationEnabling(permissionService);
          }
        })
        .doOnNext(__ -> listenToSelectedApp())
        .flatMapCompletable(lifecycleEvent -> spotAndShare.createGroup(uuid -> {
        }, throwable -> handleError(throwable), null)
            .doOnCompleted(() -> isGroupCreatedBehaviour.call(true))
            .timeout(15, TimeUnit.SECONDS))
        .doOnError(throwable -> {
          handleError(throwable);
          isGroupCreatedBehaviour.call(false);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void listenToSelectedApp() {
    view.selectedApp()
        .doOnNext(appModel -> selectedApp(appModel))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void leaveGroup() {
    spotAndShare.leaveGroup(err -> view.onLeaveGroupError());
  }

  private void selectedApp(AppModel appModel) {
    System.out.println("selected app " + appModel.getAppName());

    if (canSend()) {
      AndroidAppInfo androidAppInfo =
          appModelToAndroidAppInfoMapper.convertAppModelToAndroidAppInfo(appModel);
      AptoideUtils.ThreadU.runOnIoThread(
          () -> spotAndShare.sendApps(Collections.singletonList(androidAppInfo)));

      view.openTransferRecord();
    } else {
      view.openWaitingToSendScreen(appModel);
    }
  }

  private boolean canSend() {
    if (shouldCreateGroup) {
      return false;
    }
    return spotAndShare.canSend();
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
}
