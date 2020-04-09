package cm.aptoide.pt.editorial;

import cm.aptoide.pt.comments.refactor.CommentsView;
import cm.aptoide.pt.presenter.View;
import rx.Observable;

/**
 * Created by D01 on 27/08/2018.
 */

public interface EditorialView extends View {

  void showLoading();

  void hideLoading();

  Observable<Void> retryClicked();

  Observable<EditorialEvent> appCardClicked(EditorialViewModel model);

  Observable<EditorialEvent> actionButtonClicked();

  void populateView(EditorialViewModel editorialViewModel);

  void showError(EditorialViewModel.Error error);

  Observable<Boolean> showRootInstallWarningPopup();

  void openApp(String packageName);

  Observable<EditorialDownloadEvent> installButtonClick(EditorialViewModel editorialViewModel);

  Observable<EditorialDownloadEvent> pauseDownload(EditorialViewModel editorialViewModel);

  Observable<EditorialDownloadEvent> resumeDownload(EditorialViewModel editorialViewModel);

  Observable<EditorialDownloadEvent> cancelDownload(EditorialViewModel editorialViewModel);

  Observable<Boolean> bottomCardVisibilityChange();

  void removeBottomCardAnimation();

  void addBottomCardAnimation();

  Observable<EditorialEvent> mediaContentClicked();

  Observable<EditorialEvent> mediaListDescriptionChanged();

  Observable<Boolean> showDowngradeMessage();

  Observable<Void> snackLoginClick();

  CommentsView getCommentsView();

  void populateCardContent(EditorialViewModel editorialViewModel);
}
