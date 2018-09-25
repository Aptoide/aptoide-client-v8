package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

public interface CommentsView extends View {

  void showComments(List<Comment> comments);

  void showLoading();

  void hideLoading();

  void showGeneralError();

  void hideRefreshLoading();

  void addComments(List<Comment> comments);

  void showLoadMore();

  void hideLoadMore();

  Observable<Void> refreshes();

  Observable<Object> reachesBottom();
}
