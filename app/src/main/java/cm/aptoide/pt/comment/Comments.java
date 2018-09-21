package cm.aptoide.pt.comment;

import java.util.List;
import rx.Single;

public class Comments {
  private final CommentsRepository commentsRepository;

  public Comments(CommentsRepository commentsRepository) {
    this.commentsRepository = commentsRepository;
  }

  public Single<List<String>> loadComments() {
    return commentsRepository.loadComments();
  }
}
