package cm.aptoide.pt.comments.refactor.network

import cm.aptoide.pt.comments.refactor.data.CommentsResponseModel
import cm.aptoide.pt.dataprovider.util.CommentType
import cm.aptoide.pt.editorial.epoxy.comments.CommentFilters
import rx.Completable
import rx.Single

interface CommentsDataSource {

  fun loadComments(id: Long, type: CommentType, filters: CommentFilters,
                   invalidateHttpCache: Boolean): Single<CommentsResponseModel>

  fun loadFreshComments(id: Long, type: CommentType,
                        filters: CommentFilters): Single<CommentsResponseModel>

  fun loadNextComments(id: Long, type: CommentType, filters: CommentFilters,
                       offset: Int): Single<CommentsResponseModel>

  fun loadReplies(commentId: Long, offset: Int, filters: CommentFilters,
                  type: CommentType, limit: Int): Single<CommentsResponseModel>

  fun writeComment(id: Long, type: CommentType, message: String): Completable

  fun writeReply(storeId: Long, message: String,
                 parentId: Long): Completable
}