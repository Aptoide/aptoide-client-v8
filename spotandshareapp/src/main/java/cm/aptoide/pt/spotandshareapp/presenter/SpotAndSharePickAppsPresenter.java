package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.AppModel;
import cm.aptoide.pt.spotandshareapp.AppModelToAndroidAppInfoMapper;
import cm.aptoide.pt.spotandshareapp.DrawableBitmapMapper;
import cm.aptoide.pt.spotandshareapp.InstalledRepositoryDummy;
import cm.aptoide.pt.spotandshareapp.ObbsProvider;
import cm.aptoide.pt.spotandshareapp.view.SpotAndSharePickAppsView;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndSharePickAppsPresenter implements Presenter {
  private final SpotAndSharePickAppsView view;
  private final SpotAndShare spotAndShare;
  private boolean shouldCreateGroup;
  private InstalledRepositoryDummy installedRepositoryDummy;
  private List<AppModel> selectedApps;
  private DrawableBitmapMapper drawableBitmapMapper;
  private AppModelToAndroidAppInfoMapper appModelToAndroidAppInfoMapper;
  private ObbsProvider obbsProvider;

  public SpotAndSharePickAppsPresenter(SpotAndSharePickAppsView view, boolean shouldCreateGroup,
      InstalledRepositoryDummy installedRepositoryDummy, SpotAndShare spotAndShare,
      DrawableBitmapMapper drawableBitmapMapper,
      AppModelToAndroidAppInfoMapper appModelToAndroidAppInfoMapper) {
    this.view = view;
    this.shouldCreateGroup = shouldCreateGroup;
    this.installedRepositoryDummy = installedRepositoryDummy;
    this.spotAndShare = spotAndShare;
    this.drawableBitmapMapper = drawableBitmapMapper;
    this.appModelToAndroidAppInfoMapper = appModelToAndroidAppInfoMapper;
    selectedApps = new LinkedList<>();
  }

  @Override public void present() {

    Observable.zip(getInstaledAppsObservable(), getCreateGroupObservable(),
        (appModels, s) -> appModels)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(installedApps -> view.buildInstalledAppsList(installedApps))
        .subscribe(o -> view.hideLoading(), throwable -> throwable.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.selectedApp())
        .doOnNext(appModel -> selectedApp(appModel))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  private Observable<Integer> getCreateGroupObservable() {
    return view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(lifecycleEvent -> {
          if (shouldCreateGroup) {
            view.showLoading();
            return createGroup().timeout(10, TimeUnit.SECONDS)
                .toSingleDefault(2);
            //// FIXME: 12-07-2017 should not pass this integer
          }
          return Completable.complete()
              .toSingleDefault(2);
        })
        .doOnError(throwable -> handleCreateGroupError(throwable))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY));
  }

  private void handleCreateGroupError(Throwable throwable) {
    if (throwable instanceof TimeoutException) {
      spotAndShare.leaveGroup(err -> view.onLeaveGroupError());
      view.onCreateGroupError(throwable);
      view.navigateBack();
    }
  }

  private Observable<List<AppModel>> getInstaledAppsObservable() {
    return view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(lifecycleEvent -> view.showLoading())
        .observeOn(Schedulers.io())
        .map(lifecycleEvent -> installedRepositoryDummy.getInstalledApps())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY));
  }

  private Completable createGroup() {
    return spotAndShare.createGroup(uuid -> {
    }, view::onCreateGroupError, null);
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void selectedApp(AppModel appModel) {
    System.out.println("selected app " + appModel.getAppName());
    if (selectedApps.contains(appModel)) {
      selectedApps.remove(appModel);
    } else {
      selectedApps.add(appModel);
    }

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
    return spotAndShare.canSend();
  }

  private void leaveGroup() {
    spotAndShare.leaveGroup(err -> view.onLeaveGroupError());
  }
}
