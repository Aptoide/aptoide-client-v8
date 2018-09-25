package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.Comment;
import java.util.List;
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

  public Single<List<Comment>> loadComments() {
    return comments.loadComments(storeId)
        .doOnSuccess(commentsResponseModel -> offset = commentsResponseModel.getOffset())
        .map(commentsResponseModel -> commentsResponseModel.getComments());
  }

  public Single<List<Comment>> loadFreshComments() {
    return comments.loadFreshComments(storeId)
        .doOnSuccess(commentsResponseModel -> offset = commentsResponseModel.getOffset())
        .map(commentsResponseModel -> commentsResponseModel.getComments());
  }

  public Single<List<Comment>> loadMoreComments() {
    return comments.loadNextComments(storeId, offset)
        .doOnSuccess(commentsResponseModel -> offset = commentsResponseModel.getOffset())
        .map(commentsResponseModel -> commentsResponseModel.getComments());
  }
}
