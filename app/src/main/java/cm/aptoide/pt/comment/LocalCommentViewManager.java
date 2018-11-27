package cm.aptoide.pt.comment;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.CommentsResponseModel;
import rx.Completable;
import rx.Single;

public class LocalCommentViewManager {

  private final Comments comments;
  private final AptoideAccountManager accountManager;
  private LocalCommentWrapper localComment;

  public LocalCommentViewManager(Comments comments, AptoideAccountManager accountManager) {
    this.comments = comments;
    this.accountManager = accountManager;
  }

  public Completable postComment(Comment comment, long storeId) {
    return accountManager.accountStatus()
        .map(account -> {
          saveLocalComment(comment);
          return account.isLoggedIn();
        })
        .filter(isLoggedIn -> isLoggedIn)
        .flatMapSingle(__ -> comments.writeComment(storeId, comment.getMessage()))
        .toCompletable();
  }

  public Single<CommentsResponseModel> loadComments(long storeId) {
    return comments.loadComments(storeId);
  }

  private void saveLocalComment(Comment comment) {
    // localComment = new LocalCommentWrapper(comment, false);
  }
}
