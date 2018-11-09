package cm.aptoide.pt.commentdetail;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.comment.CommentDetailResponseModel;
import cm.aptoide.pt.comment.Comments;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.User;
import java.util.Date;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class CommentDetailManager {
  private final AptoideAccountManager accountManager;
  private final Comments comments;
  private final long commentId;
  private final String message;
  private final User user;
  private final int repliesNumber;
  private final Date commentTimestamp;
  private final long storeId;

  public CommentDetailManager(AptoideAccountManager accountManager, Comments comments,
      long commentId, String message, User user, int repliesNumber, Date commentTimestamp,
      long storeId) {
    this.accountManager = accountManager;
    this.comments = comments;
    this.commentId = commentId;
    this.message = message;
    this.user = user;
    this.repliesNumber = repliesNumber;
    this.commentTimestamp = commentTimestamp;
    this.storeId = storeId;
  }

  public Single<CommentDetailViewModel> loadCommentModel() {
    return comments.loadComment(commentId)
        .flatMap(this::map);
  }

  public Completable replyComment(Comment comment) {
    return comments.writeComment(storeId, comment.getMessage(), commentId);
  }

  private Single<CommentDetailViewModel> map(CommentDetailResponseModel response) {
    return accountManager.accountStatus()
        .first()
        .flatMap(account -> Observable.just(
            new CommentDetailViewModel(user.getName(), user.getAvatar(), message,
                account.getAvatar(), repliesNumber, commentTimestamp, response.getReplies())))
        .toSingle();
  }
}
