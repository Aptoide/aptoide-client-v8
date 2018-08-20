package cm.aptoide.pt.appview;

import cm.aptoide.pt.app.DownloadAppViewModel;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.app.view.AppBoughClickEvent;
import cm.aptoide.pt.presenter.View;
import rx.Observable;

/**
 * Created by filipegoncalves on 5/7/18.
 */

public interface InstallAppView extends View {

  Observable<DownloadModel.Action> installAppClick();

  Observable<Boolean> showRootInstallWarningPopup();

  void showDownloadAppModel(DownloadAppViewModel model);

  void openApp(String packageName);

  Observable<Boolean> showDowngradeMessage();

  void showDowngradingMessage();

  Observable<Void> pauseDownload();

  Observable<Void> resumeDownload();

  Observable<Void> cancelDownload();

  Observable<Void> isAppViewReadyToDownload();

  void readyToDownload();

  void showRecommendsDialog();

  Observable<Void> continueLoggedInRecommendsDialogClick();

  void showRecommendsThanksMessage();

  Observable<Void> skipLoggedInRecommendsDialogClick();

  Observable<Void> dontShowAgainLoggedInRecommendsDialogClick();

  Observable<AppBoughClickEvent> appBought();
}
