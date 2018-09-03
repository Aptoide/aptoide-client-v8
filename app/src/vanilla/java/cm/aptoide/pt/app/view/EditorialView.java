package cm.aptoide.pt.app.view;

import cm.aptoide.pt.app.DownloadAppViewModel;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.presenter.View;
import rx.Observable;

/**
 * Created by D01 on 27/08/2018.
 */

public interface EditorialView extends View {

  void showLoading();

  void hideLoading();

  Observable<Void> retryClicked();

  void setToolbarInfo(String title);

  Observable<DownloadModel.Action> installButtonClick();

  void populateView(EditorialViewModel editorialViewModel);

  void showError(EditorialViewModel.Error error);

  void showDownloadAppModel(DownloadAppViewModel model);

  Observable<Boolean> showRootInstallWarningPopup();

  void openApp(String packageName);

  Observable<Void> pauseDownload();

  Observable<Void> resumeDownload();

  Observable<Void> cancelDownload();

  Observable<Void> isAppViewReadyToDownload();

  void readyToDownload();

}
