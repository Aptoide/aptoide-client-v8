package cm.aptoide.pt.comment;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

public interface CommentsView extends View {

  void showComments(CommentsListViewModel comments);

  void showLoading();

  void hideLoading();

  void showGeneralError();

  void hideRefreshLoading();

  void addComments(List<Comment> comments);

  void showLoadMore();

  void hideLoadMore();

  void hideKeyboard();

  void addLocalComment(Comment comment, Account account, long id);

  Observable<Void> refreshes();

  Observable<Object> reachesBottom();

  Observable<Comment> commentClick();

  Observable<Comment> commentPost();

  Observable<Long> userClickEvent();
}
