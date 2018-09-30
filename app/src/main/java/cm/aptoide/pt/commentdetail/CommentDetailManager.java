package cm.aptoide.pt.commentdetail;

import cm.aptoide.pt.comment.Comments;
import cm.aptoide.pt.comment.data.User;
import java.util.Date;
import rx.Single;

public class CommentDetailManager {
  private final Comments comments;
  private final long commentId;
  private final String message;
  private final User user;
  private final int repliesNumber;
  private final String commentTimestamp;

  public CommentDetailManager(Comments comments, long commentId, String message, User user,
      int repliesNumber, String commentTimestamp) {
    this.comments = comments;
    this.commentId = commentId;
    this.message = message;
    this.user = user;
    this.repliesNumber = repliesNumber;
    this.commentTimestamp = commentTimestamp;
  }

  public Single<CommentDetailViewModel> loadCommentModel() {
    return comments.loadComment(commentId)
        .map(commentResponseModel -> new CommentDetailViewModel(user.getName(), user.getAvatar(),
            message, repliesNumber, new Date(commentTimestamp), commentResponseModel.getReplies()));
  }
}
