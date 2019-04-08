package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public interface AppsFragmentView extends View {

  void showUpdatesList(List<App> list);

  void showInstalledApps(List<App> installedApps);

  void showDownloadsList(List<App> list);

  Observable<App> retryDownload();

  Observable<App> installApp();

  Observable<App> cancelDownload();

  Observable<App> resumeDownload();

  Observable<App> pauseDownload();

  Observable<AppClickEventWrapper> retryUpdate();

  Observable<AppClickEventWrapper> updateApp();

  Observable<AppClickEventWrapper> pauseUpdate();

  Observable<AppClickEventWrapper> cancelUpdate();

  Observable<AppClickEventWrapper> resumeUpdate();

  Observable<Boolean> showRootWarning();

  void showUpdatesDownloadList(List<App> updatesDownloadList);

  void showAppcUpgradesDownloadList(List<App> updatesDownloadList);

  Observable<Void> updateAll();

  Observable<App> updateLongClick();

  void showIgnoreUpdate();

  Observable<Void> ignoreUpdate();

  void showUnknownErrorMessage();

  void removeExcludedUpdates(List<App> excludedUpdatesList);

  Observable<Void> moreAppcClick();

  Observable<App> updateClick();

  void setUserImage(String userAvatarUrl);

  void showAvatar();

  Observable<Void> imageClick();

  void removeInstalledDownloads(List<App> installedDownloadsList);

  void scrollToTop();

  Observable<Void> refreshApps();

  void hidePullToRefresh();

  void removeCanceledAppDownload(App app);

  void removeAppcCanceledAppDownload(App app);

  void setStandbyState(App app);

  void setAppcStandbyState(App app);

  void showIndeterminateAllUpdates();

  void setDefaultUserImage();

  void setPausingDownloadState(App app);

  void setAppcPausingDownloadState(App app);

  void showAppcUpgradesList(List<App> list);

  void removeExcludedAppcUpgrades(List<App> excludedUpdatesList);
}
