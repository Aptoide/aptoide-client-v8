package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.CommentsResponseModel;
import rx.Completable;
import rx.Single;

public class CommentsRepository {
  private final CommentsDataSource dataSource;

  public CommentsRepository(CommentsDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Single<CommentsResponseModel> loadComments(long storeId) {
    return dataSource.loadComments(storeId, false);
  }

  public Single<CommentsResponseModel> loadFreshComments(long storeId) {
    return dataSource.loadFreshComments(storeId);
  }

  public Single<CommentsResponseModel> loadNextComments(long storeId, int offset) {
    return dataSource.loadNextComments(storeId, offset);
  }

  public Single<CommentDetailResponseModel> loadComment(long commentId) {
    return dataSource.loadComment(commentId);
  }

  public Completable writeComment(long storeId, String message) {
    return dataSource.writeComment(storeId, message);
  }

  public Completable writeComment(long storeId, String message, long parentId) {
    return dataSource.writeComment(storeId, message, parentId);
  }
}
