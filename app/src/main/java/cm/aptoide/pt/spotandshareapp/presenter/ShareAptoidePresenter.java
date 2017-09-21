package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Build;
import android.os.Bundle;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.ShareApkSandbox;
import cm.aptoide.pt.spotandshareapp.view.ShareAptoideView;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;

/**
 * Created by filipe on 12-09-2017.
 */

public class ShareAptoidePresenter implements Presenter {

  private ShareAptoideView view;
  private SpotAndShare spotAndShare;
  private ShareApkSandbox shareApkSandbox;
  private final PermissionManager permissionManager;
  private final PermissionService permissionService;

  public ShareAptoidePresenter(ShareAptoideView view, SpotAndShare spotAndShare,
      ShareApkSandbox shareApkSandbox, PermissionManager permissionManager,
      PermissionService permissionService) {
    this.view = view;
    this.spotAndShare = spotAndShare;
    this.shareApkSandbox = shareApkSandbox;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return permissionManager.requestLocationAndExternalStoragePermission(permissionService)
                .flatMap(accessToLocation -> permissionManager.requestWriteSettingsPermission(
                    permissionService));
          } else {
            return Observable.empty();
          }
        })
        .flatMapSingle(created -> createGroup().timeout(10, TimeUnit.SECONDS)
            .toSingleDefault(2))
        .doOnError(throwable -> view.onCreateGroupError(throwable))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());

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
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.DESTROY))
        .doOnNext(__ -> shareApkSandbox.stop())
        .subscribe(__ -> {
        }, error -> error.printStackTrace());
  }

  private Completable createGroup() {
    return spotAndShare.createOpenGroup(success -> {
      try {
        shareApkSandbox.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  private void leaveGroup() {
    spotAndShare.leaveGroup(error -> view.onLeaveGroupError());
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
