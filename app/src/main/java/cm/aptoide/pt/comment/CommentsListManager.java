package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.Comment;
import java.util.List;
import rx.Single;

public class CommentsListManager {
  private final Comments comments;

  public CommentsListManager(Comments comments) {
    this.comments = comments;
  }

  public Single<List<Comment>> loadComments() {
    return comments.loadComments();
  }
}
