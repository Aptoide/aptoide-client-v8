package cm.aptoide.pt.comment;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.CommentsResponseModel;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class CommentsListManager {
  private final AptoideAccountManager accountManager;
  private final Comments comments;
  private final long storeId;
  private int offset;

  public CommentsListManager(AptoideAccountManager accountManager, long storeId, Comments comments,
      int offset) {
    this.accountManager = accountManager;
    this.storeId = storeId;
    this.comments = comments;
    this.offset = offset;
  }

  public Single<CommentsListViewModel> loadComments() {
    return comments.loadComments(storeId)
        .doOnSuccess(commentsResponseModel -> offset = commentsResponseModel.getOffset())
        .flatMap(this::map);
  }

  public Single<CommentsListViewModel> loadFreshComments() {
    return comments.loadFreshComments(storeId)
        .doOnSuccess(commentsResponseModel -> offset = commentsResponseModel.getOffset())
        .flatMap(this::map);
  }

  public Single<CommentsListViewModel> loadMoreComments() {
    return comments.loadNextComments(storeId, offset)
        .doOnSuccess(commentsResponseModel -> offset = commentsResponseModel.getOffset())
        .flatMap(this::map);
  }

  private Single<CommentsListViewModel> map(CommentsResponseModel commentsResponseModel) {
    return accountManager.accountStatus()
        .first()
        .flatMap(account -> Observable.just(
            new CommentsListViewModel(account.getAvatar(), commentsResponseModel.getComments(),
                commentsResponseModel.isLoading())))
        .toSingle();
  }

  public Completable postComment(Comment comment) {
    return comments.writeComment(storeId, comment.getMessage());
  }

  public Completable replyComment(Comment comment, long parentId) {
    return comments.writeComment(storeId, comment.getMessage(), parentId);
  }
}
