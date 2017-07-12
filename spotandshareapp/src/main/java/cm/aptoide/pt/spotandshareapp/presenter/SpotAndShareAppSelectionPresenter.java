package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.AppModel;
import cm.aptoide.pt.spotandshareapp.InstalledRepositoryDummy;
import cm.aptoide.pt.spotandshareapp.SpotAndShareAppSelectionManager;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareAppSelectionView;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareAppSelectionPresenter implements Presenter {
  private final SpotAndShareAppSelectionView view;
  private final SpotAndShare spotAndShare;
  private boolean shouldCreateGroup;
  private InstalledRepositoryDummy installedRepositoryDummy;
  private List<AppModel> selectedApps;
  private SpotAndShareAppSelectionManager spotAndShareAppSelectionManager;

  public SpotAndShareAppSelectionPresenter(SpotAndShareAppSelectionView view,
      boolean shouldCreateGroup, InstalledRepositoryDummy installedRepositoryDummy,
      SpotAndShare spotAndShare, SpotAndShareAppSelectionManager spotAndShareAppSelectionManager) {
    this.view = view;
    this.shouldCreateGroup = shouldCreateGroup;
    this.installedRepositoryDummy = installedRepositoryDummy;
    this.spotAndShare = spotAndShare;
    this.spotAndShareAppSelectionManager = spotAndShareAppSelectionManager;
    selectedApps = new LinkedList<>();
  }

  @Override public void present() {

    //Observable<List<AppModel>> compose = view.getLifecycle()
    //    .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
    //    .doOnNext(lifecycleEvent -> view.showLoading())
    //    .observeOn(Schedulers.io())
    //    .map(lifecycleEvent -> installedRepositoryDummy.getInstalledApps())
    //    .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY));
    //
    //getInstaledAppsObservable();
    //getCreateGroupObservable();

    //Observable<View.LifecycleEvent> compose1 = view.getLifecycle()
    //    .filter(lifecycleEvent -> shouldCreateGroup && lifecycleEvent.equals(
    //        View.LifecycleEvent.CREATE))
    //    .doOnNext(lifecycleEvent -> view.showLoading())
    //    .doOnNext(created -> createGroup())
    //    .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY));

    Observable.zip(getInstaledAppsObservable(), getCreateGroupObservable(),
        (appModels, s) -> appModels)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(installedApps -> view.setupRecyclerView(installedApps))
        .subscribe(o -> view.hideLoading());

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
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.appSelection())
        .doOnNext(appModel -> selectedApp(appModel))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  private Observable<View.LifecycleEvent> getCreateGroupObservable() {
    return view.getLifecycle()
        .filter(lifecycleEvent -> shouldCreateGroup && lifecycleEvent.equals(
            View.LifecycleEvent.CREATE))
        .doOnNext(lifecycleEvent -> view.showLoading())
        .doOnNext(created -> createGroup())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY));
  }

  private Observable<List<AppModel>> getInstaledAppsObservable() {
    return view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(lifecycleEvent -> view.showLoading())
        .observeOn(Schedulers.io())
        .map(lifecycleEvent -> installedRepositoryDummy.getInstalledApps())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY));
  }

  private void createGroup() {
    spotAndShare.createGroup(uuid -> {
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
      String appName = appModel.getAppName();
      String packageName = appModel.getPackageName();
      File apk = new File(appModel.getFilePath());

      byte[] bitmapdata =
          spotAndShareAppSelectionManager.convertDrawableToBitmap(appModel.getAppIcon());

      AndroidAppInfo androidAppInfo =
          new AndroidAppInfo(appName, packageName, apk, null, null, bitmapdata);
      AptoideUtils.ThreadU.runOnIoThread(
          () -> spotAndShare.sendApps(Collections.singletonList(androidAppInfo)));

      view.openTransferRecord();
    } else {
      view.openWaitingToSendScreen();
    }
  }

  private boolean canSend() {
    //// TODO: 06-07-2017 filipe implement on spot&share class this verification - if has friends
    return spotAndShare.canSend();
  }

  private void leaveGroup() {
    spotAndShare.leaveGroup(view::navigateBack, err -> view.onLeaveGroupError());
  }
}
