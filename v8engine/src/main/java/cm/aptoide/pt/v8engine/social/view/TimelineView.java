package cm.aptoide.pt.v8engine.social.view;

import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.Post;
import java.util.List;
import rx.Observable;

/**
 * Created by jdandrade on 31/05/2017.
 */

public interface TimelineView extends View {

  void showCards(List<Post> cards);

  void showProgressIndicator();

  void hideProgressIndicator();

  void hideRefresh();

  void showMoreCards(List<Post> cards);

  void showGenericError();

  Observable<Void> refreshes();

  Observable<Void> reachesBottom();

  Observable<CardTouchEvent> postClicked();

  Observable<Post> shareConfirmation();

  Observable<Void> retry();

  void showLoadMoreProgressIndicator();

  void hideLoadMoreProgressIndicator();

  boolean isNewRefresh();

  void showRootAccessDialog();

  void updateInstallProgress(Post card, int cardPosition);

  void showStoreSubscribedMessage(String storeName);

  void showStoreUnsubscribedMessage(String storeName);

  void showSharePreview(Post post);

  void showShareSuccessMessage();
}
