package cm.aptoide.pt.editorial;

import cm.aptoide.pt.comments.refactor.data.CommentsResponseModel;
import cm.aptoide.pt.editorial.epoxy.comments.ChangeFilterEvent;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.reviews.LanguageFilterHelper;
import java.util.List;
import rx.Observable;

/**
 * Created by D01 on 27/08/2018.
 */

public interface EditorialView extends View {

  Observable<Object> reachesBottom();

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

  void populateCardContent(EditorialViewModel editorialViewModel,
      CommentsResponseModel commentsResponseModel);

  Observable<ChangeFilterEvent> filterEventChange();

  List<LanguageFilterHelper.LanguageFilter> getLanguageFilters();
}
