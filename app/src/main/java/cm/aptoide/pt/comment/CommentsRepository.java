package cm.aptoide.pt.comment;

import java.util.List;
import rx.Single;

public class CommentsRepository {
  private final CommentsDataSource dataSource;

  public CommentsRepository(CommentsDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Single<List<String>> loadComments() {
    return dataSource.loadComments();
  }
}
