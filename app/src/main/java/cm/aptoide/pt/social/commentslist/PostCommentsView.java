package cm.aptoide.pt.social.commentslist;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

/**
 * Created by jdandrade on 28/09/2017.
 */

public interface PostCommentsView extends View {

  Observable<Object> reachesBottom();

  Observable<Void> refreshes();

  Observable<Long> repliesComment();

  Observable<Void> repliesPost();

  void showLoadMoreProgressIndicator();

  void hideLoadMoreProgressIndicator();

  void showComments(List<Comment> comments);

  void hideRefresh();

  void showMoreComments(List<Comment> comments);

  void showLoading();

  void hideLoading();
}
