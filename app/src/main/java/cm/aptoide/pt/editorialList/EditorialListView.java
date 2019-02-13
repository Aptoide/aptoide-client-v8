package cm.aptoide.pt.editorialList;

import cm.aptoide.pt.home.EditorialHomeEvent;
import cm.aptoide.pt.presenter.View;
import rx.Observable;

public interface EditorialListView extends View {
  Observable<EditorialHomeEvent> editorialCardClicked();

  void showLoading();

  void hideLoading();

  void showGenericError();

  void showNetworkError();

  Observable<Void> retryClicked();

  Observable<Void> imageClick();

  void showAvatar();

  void setDefaultUserImage();

  void setUserImage(String userAvatarUrl);

  Observable<Object> reachesBottom();

  void populateView(EditorialListViewModel editorialListViewModel);

  void showError(EditorialListViewModel.Error error);
}
