package cm.aptoide.pt.v8engine.social.view;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.Post;
import cm.aptoide.pt.v8engine.social.data.PostComment;
import cm.aptoide.pt.v8engine.social.data.SocialAction;
import cm.aptoide.pt.v8engine.social.data.SocialCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.share.ShareEvent;
import java.util.List;
import rx.Completable;
import rx.Observable;

/**
 * Created by jdandrade on 31/05/2017.
 */

public interface TimelineView extends View {

  void showCards(List<Post> cards);

  void showGeneralProgressIndicator();

  void hideGeneralProgressIndicator();

  void hideRefresh();

  void showMoreCards(List<Post> cards);

  void showGenericViewError();

  Observable<Void> refreshes();

  Observable<Void> reachesBottom();

  Observable<CardTouchEvent> postClicked();

  Observable<ShareEvent> shareConfirmation();

  Observable<PostComment> commentPosted();

  Observable<Void> retry();

  void showLoadMoreProgressIndicator();

  void hideLoadMoreProgressIndicator();

  Observable<Void> floatingActionButtonClicked();

  Completable showFloatingActionButton();

  Completable hideFloatingActionButton();

  Observable<Direction> scrolled();

  void showRootAccessDialog();

  void updatePost(int cardPosition);

  void swapPost(Post post, int postPosition);

  void showStoreSubscribedMessage(String storeName);

  void showStoreUnsubscribedMessage(String storeName);

  void showSharePreview(Post post, Account account);

  void showShareSuccessMessage();

  void showCommentDialog(SocialCardTouchEvent touchEvent);

  void showGenericError();

  void showLoginPromptWithAction();

  Observable<Void> loginActionClick();

  void showCreateStoreMessage(SocialAction socialAction);

  void showPostProgressIndicator();

  void hidePostProgressIndicator();
}
