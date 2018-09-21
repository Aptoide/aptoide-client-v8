package cm.aptoide.pt.comment.mock;

import cm.aptoide.pt.comment.CommentsDataSource;
import java.util.ArrayList;
import java.util.List;
import rx.Single;

public class CommentsFakeDataSource implements CommentsDataSource {
  @Override public Single<List<String>> loadComments() {
    return getFakeComments();
  }

  private Single<List<String>> getFakeComments() {
    List<String> comments = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      comments.add("comment " + i);
    }
    return Single.just(comments);
  }
}
