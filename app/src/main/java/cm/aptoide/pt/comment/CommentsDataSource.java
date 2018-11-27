package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.CommentsResponseModel;
import cm.aptoide.pt.dataprovider.model.v7.SetComment;
import rx.Completable;
import rx.Single;

public interface CommentsDataSource {

  Single<CommentsResponseModel> loadComments(long storeId, boolean invalidateHttpCache);

  Single<CommentsResponseModel> loadFreshComments(long storeId);

  Single<CommentsResponseModel> loadNextComments(long storeId, int offset);

  Single<CommentDetailResponseModel> loadComment(long commentId);

  Single<SetComment> writeComment(long storeId, String message);

  Completable writeComment(long storeId, String message, long parentId);
}
