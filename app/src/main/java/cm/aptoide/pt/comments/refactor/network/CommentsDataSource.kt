package cm.aptoide.pt.comments.refactor.network

import cm.aptoide.pt.comments.refactor.data.CommentsResponseModel
import cm.aptoide.pt.dataprovider.util.CommentType
import rx.Completable
import rx.Single

interface CommentsDataSource {

  fun loadComments(id: Long, type: CommentType,
                   invalidateHttpCache: Boolean): Single<CommentsResponseModel>

  fun loadFreshComments(id: Long, type: CommentType): Single<CommentsResponseModel>

  fun loadNextComments(id: Long, type: CommentType, offset: Int): Single<CommentsResponseModel>

  fun loadReplies(commentId: Long, offset: Int): Single<CommentsResponseModel>

  fun writeComment(id: Long, type: CommentType, message: String): Completable

  fun writeReply(storeId: Long, message: String,
                 parentId: Long): Completable
}