package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.Comment;
import java.util.List;
import rx.Single;

public class CommentsListManager {
  private final Comments comments;
  private final long storeId;

  public CommentsListManager(long storeId, Comments comments) {
    this.storeId = storeId;
    this.comments = comments;
  }

  public Single<List<Comment>> loadComments() {
    return comments.loadComments(storeId);
  }

  public Single<List<Comment>> loadFreshComments() {
    return loadComments();
  }
}
