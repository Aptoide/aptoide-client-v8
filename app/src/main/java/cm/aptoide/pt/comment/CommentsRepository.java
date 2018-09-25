package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.CommentsResponseModel;
import rx.Single;

public class CommentsRepository {
  private final CommentsDataSource dataSource;

  public CommentsRepository(CommentsDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Single<CommentsResponseModel> loadComments(long storeId) {
    return dataSource.loadComments(storeId);
  }

  public Single<CommentsResponseModel> loadFreshComments(long storeId) {
    return dataSource.loadFreshComments(storeId);
  }

  public Single<CommentsResponseModel> loadNextComments(long storeId, int offset) {
    return dataSource.loadNextComments(storeId, offset);
  }
}
