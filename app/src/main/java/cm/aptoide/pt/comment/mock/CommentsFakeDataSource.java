package cm.aptoide.pt.comment.mock;

import cm.aptoide.pt.comment.CommentsDataSource;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import rx.Single;

public class CommentsFakeDataSource implements CommentsDataSource {
  @Override public Single<List<Comment>> loadComments() {
    return getFakeComments();
  }

  private Single<List<Comment>> getFakeComments() {
    List<Comment> comments = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      comments.add(new Comment(i, "comment " + i, new User(i, null, "user " + i), i, new Date()));
    }
    return Single.just(comments);
  }
}
