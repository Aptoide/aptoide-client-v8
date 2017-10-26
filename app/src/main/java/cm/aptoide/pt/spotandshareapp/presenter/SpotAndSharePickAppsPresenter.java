package cm.aptoide.pt.spotandshareapp.presenter;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.AppModel;
import cm.aptoide.pt.spotandshareapp.AppModelToAndroidAppInfoMapper;
import cm.aptoide.pt.spotandshareapp.SpotAndShareAppProvider;
import cm.aptoide.pt.spotandshareapp.view.SpotAndSharePickAppsView;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.Collections;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndSharePickAppsPresenter implements Presenter {
  private final SpotAndSharePickAppsView view;
  private final SpotAndShare spotAndShare;
  private boolean shouldCreateGroup;
  private SpotAndShareAppProvider spotandShareAppProvider;
  private AppModelToAndroidAppInfoMapper appModelToAndroidAppInfoMapper;
  private final CrashReport crashReport;

  public SpotAndSharePickAppsPresenter(SpotAndSharePickAppsView view, boolean shouldCreateGroup,
      SpotAndShareAppProvider spotandShareAppProvider, SpotAndShare spotAndShare,
      AppModelToAndroidAppInfoMapper appModelToAndroidAppInfoMapper, CrashReport crashReport) {
    this.view = view;
    this.shouldCreateGroup = shouldCreateGroup;
    this.spotandShareAppProvider = spotandShareAppProvider;
    this.spotAndShare = spotAndShare;
    this.appModelToAndroidAppInfoMapper = appModelToAndroidAppInfoMapper;
    this.crashReport = crashReport;
  }

  @Override public void present() {
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

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.selectedApp())
        .doOnNext(appModel -> selectedApp(appModel))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
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
}
