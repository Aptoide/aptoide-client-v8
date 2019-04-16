package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

interface SeeMoreAppcView extends View {
  void showAppcUpgradesList(List<App> list);

  Observable<Void> refreshApps();

  void hidePullToRefresh();

  Observable<App> upgradeAppcApp();

  Observable<App> resumeAppcUpgrade();

  Observable<App> retryAppcUpgrade();

  Observable<App> cancelAppcUpgrade();

  Observable<App> pauseAppcUpgrade();

  Observable<Boolean> showRootWarning();

  void setAppcStandbyState(App app);

  void removeAppcCanceledAppDownload(App app);

  void setAppcPausingDownloadState(App app);

  void showAppcUpgradesDownloadList(List<App> list);
}
