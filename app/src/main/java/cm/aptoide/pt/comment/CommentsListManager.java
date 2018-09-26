package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.CommentsResponseModel;
import rx.Single;

public class CommentsListManager {
  private final Comments comments;
  private final long storeId;
  private int offset;

  public CommentsListManager(long storeId, Comments comments, int offset) {
    this.storeId = storeId;
    this.comments = comments;
    this.offset = offset;
  }

  public Single<CommentsResponseModel> loadComments() {
    return comments.loadComments(storeId)
        .doOnSuccess(commentsResponseModel -> offset = commentsResponseModel.getOffset());
  }

  public Single<CommentsResponseModel> loadFreshComments() {
    return comments.loadFreshComments(storeId)
        .doOnSuccess(commentsResponseModel -> offset = commentsResponseModel.getOffset());
  }

  public Single<CommentsResponseModel> loadMoreComments() {
    return comments.loadNextComments(storeId, offset)
        .doOnSuccess(commentsResponseModel -> offset = commentsResponseModel.getOffset());
  }
}
