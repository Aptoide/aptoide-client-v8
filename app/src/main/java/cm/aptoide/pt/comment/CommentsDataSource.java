package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.CommentsResponseModel;
import rx.Single;

public interface CommentsDataSource {

  Single<CommentsResponseModel> loadComments(long storeId);

  Single<CommentsResponseModel> loadFreshComments(long storeId);

  Single<CommentsResponseModel> loadNextComments(long storeId, int offset);
}
