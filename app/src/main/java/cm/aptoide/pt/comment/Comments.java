package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.Comment;
import java.util.List;
import rx.Single;

public class Comments {
  private final CommentsRepository commentsRepository;

  public Comments(CommentsRepository commentsRepository) {
    this.commentsRepository = commentsRepository;
  }

  public Single<List<Comment>> loadComments() {
    return commentsRepository.loadComments();
  }
}
