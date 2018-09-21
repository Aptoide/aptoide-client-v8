package cm.aptoide.pt.comment;

import java.util.List;
import rx.Single;

public class CommentsListManager {
  private final Comments comments;

  public CommentsListManager(Comments comments) {
    this.comments = comments;
  }

  public Single<List<String>> loadComments() {
    return comments.loadComments();
  }
}
