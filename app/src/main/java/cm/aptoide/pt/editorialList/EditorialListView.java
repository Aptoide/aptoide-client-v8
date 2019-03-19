package cm.aptoide.pt.editorialList;

import cm.aptoide.pt.home.EditorialHomeEvent;
import cm.aptoide.pt.presenter.View;
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

  void populateView(EditorialListViewModel editorialListViewModel);

  Observable<EditorialListEvent> visibleCards();

  void showLoadMore();

  void hideLoadMore();

  void update(List<CurationCard> editorialListViewModel);
}
