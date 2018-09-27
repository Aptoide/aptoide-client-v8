package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.Comment;
import java.util.List;

public class CommentDetailResponseModel {
  private final Comment comment;
  private final List<Comment> replies;

  public CommentDetailResponseModel(Comment comment, List<Comment> replies) {
    this.comment = comment;
    this.replies = replies;
  }

  public Comment getComment() {
    return comment;
  }

  public List<Comment> getReplies() {
    return replies;
  }
}
