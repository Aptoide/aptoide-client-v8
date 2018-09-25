package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.Comment;
import java.util.List;
import rx.Single;

public class CommentsRepository {
  private final CommentsDataSource dataSource;

  public CommentsRepository(CommentsDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Single<List<Comment>> loadComments(long storeId) {
    return dataSource.loadComments(storeId);
  }

  public Single<List<Comment>> loadFreshComments(long storeId) {
    return dataSource.loadFreshComments(storeId);
  }
}
