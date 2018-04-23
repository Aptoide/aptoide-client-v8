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

  Observable<App> retryUpdate();

  Observable<App> updateApp();

  Observable<App> pauseUpdate();

  Observable<App> cancelUpdate();

  Observable<App> resumeUpdate();

  Observable<Boolean> showRootWarning();

  void showUpdatesDownloadList(List<App> updatesDownloadList);

  Observable<Void> updateAll();

  Observable<App> updateLongClick();

  void showIgnoreUpdate();

  Observable<Void> ignoreUpdate();

  void showUnknownErrorMessage();

  void removeExcludedUpdates(List<App> excludedUpdatesList);

  Observable<App> updateClick();

  void setUserImage(String userAvatarUrl);

  void showAvatar();

  Observable<Void> imageClick();

  void removeInstalledDownloads(List<App> installedDownloadsList);

  void scrollToTop();

  void removeInstalledUpdates(List<App> installedUpdatesList);

  Observable<Void> refreshApps();

  void hidePullToRefresh();

  void removeCanceledDownload(App app);
}
