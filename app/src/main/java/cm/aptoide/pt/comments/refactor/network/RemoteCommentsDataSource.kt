package cm.aptoide.pt.comments.refactor.network

import android.content.SharedPreferences
import cm.aptoide.pt.comments.refactor.data.Comment
import cm.aptoide.pt.comments.refactor.data.CommentsResponseModel
import cm.aptoide.pt.comments.refactor.data.User
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator
import cm.aptoide.pt.dataprovider.model.v7.ListComments
import cm.aptoide.pt.dataprovider.util.CommentType
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest
import cm.aptoide.pt.dataprovider.ws.v7.Order
import cm.aptoide.pt.editorial.epoxy.comments.CommentFilters
import okhttp3.OkHttpClient
import retrofit2.Converter
import rx.Completable
import rx.Single

class RemoteCommentsDataSource(private val bodyInterceptor: BodyInterceptor<BaseBody>,
                               private val okHttpClient: OkHttpClient,
                               private val converterFactory: Converter.Factory,
                               private val tokenInvalidator: TokenInvalidator,
                               private val sharedPreferences: SharedPreferences) :
    CommentsDataSource {

  private var loadingComments: Boolean = false

  private fun loadComments(id: Long, type: CommentType,
                           invalidateHttpCache: Boolean, filters: CommentFilters,
                           offset: Int): Single<CommentsResponseModel> {
    return if (loadingComments) {
      Single.just(CommentsResponseModel(true, filters))
    } else ListCommentsRequest(
        ListCommentsRequest.Body(id, 5, Order.desc, 3, offset, type,
            filters.getActiveFilter().value),
        bodyInterceptor, okHttpClient, converterFactory, tokenInvalidator,
        sharedPreferences).observe(invalidateHttpCache)
        .cast(ListComments::class.java)
        .doOnSubscribe { loadingComments = true }
        .doOnUnsubscribe { loadingComments = false }
        .doOnTerminate { loadingComments = false }
        .flatMapSingle { response ->
          if (response.isOk) {
            return@flatMapSingle Single.just(
                CommentsResponseModel(mapComments(response.dataList.list), response.dataList.next,
                    response.dataList.total, filters))
          }
          return@flatMapSingle Single.error<CommentsResponseModel>(
              IllegalArgumentException(response.error.description))
        }
        .toSingle()
  }

  private fun mapComments(
      listComments: List<cm.aptoide.pt.dataprovider.model.v7.Comment>): List<Comment> {
    val comments: ArrayList<Comment> = ArrayList()
    val replies: HashMap<Long, ArrayList<Comment>> = HashMap()

    // First iterate through every comment from WS and map it to comment or (grouped) reply
    for (comment in listComments) {
      val mappedComment = Comment(comment.id, comment.body,
          User(comment.user.id, comment.user.avatar, comment.user.name), ArrayList(),
          comment.stats.comments, comment.added)
      if (comment.parent != null && comment.parent.id > 0) {
        replies[comment.parent.id]?.add(mappedComment)
        if (replies[comment.parent.id] == null) {
          val commentList = ArrayList<Comment>()
          commentList.add(mappedComment)
          replies[comment.parent.id] = commentList
        }
      } else {
        comments.add(mappedComment)
      }
    }

    // Merge replies with comments
    for (comment in comments) {
      replies[comment.id]?.let { r ->
        comment.setReplies(r)
      }
    }

    return comments
  }

  override fun loadComments(id: Long, type: CommentType, filters: CommentFilters,
                            invalidateHttpCache: Boolean): Single<CommentsResponseModel> {
    return loadComments(id, type, invalidateHttpCache, filters, 0)
  }

  override fun loadFreshComments(id: Long, type: CommentType,
                                 filters: CommentFilters): Single<CommentsResponseModel> {
    return loadComments(id, type, filters, true)

  }

  override fun loadNextComments(id: Long, type: CommentType, filters: CommentFilters,
                                offset: Int): Single<CommentsResponseModel> {
    return loadComments(id, type, false, filters, offset)

  }

  // TODO
  override fun loadReplies(commentId: Long, offset: Int): Single<CommentsResponseModel> {
    return Single.just(null)
  }

  // TODO
  override fun writeComment(id: Long, type: CommentType, message: String): Completable {
    return Completable.complete()
  }

  // TODO
  override fun writeReply(storeId: Long, message: String, parentId: Long): Completable {
    return Completable.complete()
  }

}