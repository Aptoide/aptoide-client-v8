package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import rx.Observable;
import rx.Single;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public interface AppsFragmentView extends View {

  Observable<App> installApp();

  Observable<App> cancelDownload();

  Observable<App> resumeDownload();

  Observable<App> pauseDownload();

  Observable<App> startDownloadInAppview();

  Observable<App> startDownload();

  Observable<Boolean> showRootWarning();

  Observable<Void> updateAll();

  Observable<App> updateLongClick();

  void showIgnoreUpdate();

  Single<RxAlertDialog.Result> showIgnoreUpdateDialog();

  Observable<Void> ignoreUpdate();

  void showUnknownErrorMessage();

  Observable<App> cardClick();

  void setUserImage(String userAvatarUrl);

  void showAvatar();

  Observable<Void> imageClick();

  void scrollToTop();

  Observable<Void> refreshApps();

  void hidePullToRefresh();

  void setDefaultUserImage();

  void showModel(AppsModel model);

  Observable<Void> onLoadAppcUpgradesSection();
}
