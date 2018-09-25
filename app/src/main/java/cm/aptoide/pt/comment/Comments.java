package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.CommentsResponseModel;
import rx.Single;

public class Comments {
  private final CommentsRepository commentsRepository;

  public Comments(CommentsRepository commentsRepository) {
    this.commentsRepository = commentsRepository;
  }

  public Single<CommentsResponseModel> loadComments(long storeId) {
    return commentsRepository.loadComments(storeId);
  }

  public Single<CommentsResponseModel> loadFreshComments(long storeId) {
    return commentsRepository.loadFreshComments(storeId);
  }

  public Single<CommentsResponseModel> loadNextComments(long storeId, int offset) {
    return commentsRepository.loadNextComments(storeId, offset);
  }
}
