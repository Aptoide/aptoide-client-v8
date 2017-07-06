package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.AppModel;
import cm.aptoide.pt.spotandshareapp.InstalledRepositoryDummy;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareAppSelectionView;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareAppSelectionPresenter implements Presenter {
  private final SpotAndShareAppSelectionView view;
  private InstalledRepositoryDummy installedRepositoryDummy;
  private final SpotAndShare spotAndShare;
  private List<AppModel> selectedApps;

  public SpotAndShareAppSelectionPresenter(SpotAndShareAppSelectionView view,
      InstalledRepositoryDummy installedRepositoryDummy, SpotAndShare spotAndShare) {
    this.view = view;
    this.installedRepositoryDummy = installedRepositoryDummy;
    this.spotAndShare = spotAndShare;
    selectedApps = new LinkedList<>();
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.setupRecyclerView(installedRepositoryDummy.getInstalledApps()))
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

  private void selectedApp(AppModel appModel) {
    System.out.println("selected app " + appModel.getAppName());
    if (selectedApps.contains(appModel)) {
      selectedApps.remove(appModel);
    } else {
      selectedApps.add(appModel);
    }
    //// FIXME: 06-07-2017 filipe check connection status;
    String appName = appModel.getAppName();
    String packageName = appModel.getPackageName();
    File apk = new File(appModel.getFilePath());

    AndroidAppInfo androidAppInfo = new AndroidAppInfo(appName, packageName, apk);
    spotAndShare.sendApps(Collections.singletonList(androidAppInfo));

    view.openTransferRecord();
  }

  private void leaveGroup() {
    spotAndShare.leaveGroup(success -> view.navigateBack(), err -> view.onLeaveGroupError());
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
