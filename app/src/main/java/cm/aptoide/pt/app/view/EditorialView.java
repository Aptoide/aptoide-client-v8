package cm.aptoide.pt.app.view;

import android.support.v7.graphics.Palette;
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

  Observable<DownloadModel.Action> installButtonClick();

  Observable<EditorialEvent> appCardClicked();

  Observable<EditorialEvent> actionButtonClicked();

  void populateView(EditorialViewModel editorialViewModel);

  void showError(EditorialViewModel.Error error);

  void showDownloadModel(DownloadModel model);

  Observable<Boolean> showRootInstallWarningPopup();

  void openApp(String packageName);

  Observable<EditorialEvent> pauseDownload();

  Observable<EditorialEvent> resumeDownload();

  Observable<EditorialEvent> cancelDownload();

  Observable<Void> isViewReady();

  void readyToDownload();

  Observable<ScrollEvent> placeHolderVisibilityChange();

  void removeBottomCardAnimation();

  void addBottomCardAnimation();

  Observable<EditorialEvent> mediaContentClicked();

  void managePlaceHolderVisibity();

  Observable<Palette.Swatch> paletteSwatchExtracted();

  void applyPaletteSwatch(Palette.Swatch swatch);

  Observable<EditorialEvent> mediaListDescriptionChanged();

  void manageMediaListDescriptionAnimationVisibility(EditorialEvent editorialEvent);

  void setMediaListDescriptionsVisible(EditorialEvent editorialEvent);
}
