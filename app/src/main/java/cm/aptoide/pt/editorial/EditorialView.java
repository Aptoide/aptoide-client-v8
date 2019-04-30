package cm.aptoide.pt.editorial;

import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.reactions.ReactionEvent;
import cm.aptoide.pt.reactions.data.TopReaction;
import java.util.List;
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

  void showDownloadModel(EditorialDownloadModel model);

  Observable<Boolean> showRootInstallWarningPopup();

  void openApp(String packageName);

  Observable<EditorialDownloadEvent> installButtonClick(EditorialViewModel editorialViewModel);

  Observable<EditorialDownloadEvent> pauseDownload(EditorialViewModel editorialViewModel);

  Observable<EditorialDownloadEvent> resumeDownload(EditorialViewModel editorialViewModel);

  Observable<EditorialDownloadEvent> cancelDownload(EditorialViewModel editorialViewModel);

  Observable<Void> isViewReady();

  Observable<ScrollEvent> placeHolderVisibilityChange();

  void removeBottomCardAnimation();

  void addBottomCardAnimation();

  Observable<EditorialEvent> mediaContentClicked();

  void managePlaceHolderVisibity();

  Observable<EditorialEvent> mediaListDescriptionChanged();

  void manageMediaListDescriptionAnimationVisibility(EditorialEvent editorialEvent);

  void setMediaListDescriptionsVisible(EditorialEvent editorialEvent);

  Observable<Boolean> handleMovingCollapse();

  Observable<Boolean> showDowngradeMessage();

  void showDowngradingMessage();

  Observable<Void> reactionsButtonClicked();

  Observable<Void> reactionsButtonLongPressed();

  void setReactions(String userReaction, List<TopReaction> reactions, int numberOfReactions);

  void showReactionsPopup(String cardId, String groupId);

  Observable<ReactionEvent> reactionClicked();

  void setUserReaction(String reaction);

  void showLoginDialog();

  Observable<Void> snackLoginClick();

  void showGenericErrorToast();

  void showNetworkErrorToast();
}
