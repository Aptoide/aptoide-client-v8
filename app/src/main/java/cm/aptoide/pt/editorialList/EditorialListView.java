package cm.aptoide.pt.editorialList;

import cm.aptoide.pt.home.EditorialHomeEvent;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.reactions.ReactionsHomeEvent;
import java.util.List;
import rx.Observable;

public interface EditorialListView extends View {
  Observable<EditorialHomeEvent> editorialCardClicked();

  Observable<EditorialHomeEvent> reactionsButtonClicked();

  void showLoading();

  void hideLoading();

  void showGenericError();

  void showNetworkError();

  Observable<Void> retryClicked();

  Observable<Void> refreshes();

  void hideRefresh();

  Observable<Void> imageClick();

  void showAvatar();

  void setDefaultUserImage();

  void setUserImage(String userAvatarUrl);

  Observable<Object> reachesBottom();

  void populateView(List<CurationCard> curationCards);

  Observable<EditorialListEvent> visibleCards();

  void showLoadMore();

  void hideLoadMore();

  void update(List<CurationCard> curationCards);

  Observable<ReactionsHomeEvent> reactionClicked();

  Observable<EditorialHomeEvent> reactionButtonLongPress();

  void showReactionsPopup(String cardId, String groupId, int bundlePosition);

  void showLogInDialog();

  Observable<Void> snackLogInClick();

  void showErrorToast();

  void updateEditorialCard(CurationCard curationCard, String cardId);

  void showNetworkErrorToast();
}
