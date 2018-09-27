package cm.aptoide.pt.commentdetail;

import cm.aptoide.pt.comment.Comments;
import rx.Single;

public class CommentDetailManager {
  private final Comments comments;
  private final long commentId;

  public CommentDetailManager(Comments comments, long commentId) {
    this.comments = comments;
    this.commentId = commentId;
  }

  public Single<CommentDetailViewModel> loadCommentModel() {
    return comments.loadComment(commentId)
        .map(commentResponseModel -> new CommentDetailViewModel(commentResponseModel.getComment()
            .getUser()
            .getName(), commentResponseModel.getComment()
            .getUser()
            .getAvatar(), commentResponseModel.getComment()
            .getMessage(), commentResponseModel.getComment()
            .getReplies() + "", commentResponseModel.getReplies()));
  }
}
