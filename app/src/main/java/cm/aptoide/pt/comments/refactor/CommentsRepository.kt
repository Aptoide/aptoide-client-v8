package cm.aptoide.pt.comments.refactor

import cm.aptoide.pt.comments.refactor.data.CommentsResponseModel
import cm.aptoide.pt.comments.refactor.network.CommentsDataSource
import cm.aptoide.pt.dataprovider.util.CommentType
import rx.Single

class CommentsRepository(val dataSource: CommentsDataSource) {
  private val cache = HashMap<String, CommentsResponseModel>()

  fun loadComments(id: Long, type: CommentType): Single<CommentsResponseModel> {
    val cacheKey = getCacheKey(id, type)
    val cachedComments = cache[cacheKey]
    return if (cachedComments != null) {
      Single.just(cachedComments)
    } else {
      dataSource.loadComments(id, type, false)
          .doOnSuccess { c -> cache[cacheKey] = c }
    }
  }

  private fun getCacheKey(id: Long, type: CommentType): String {
    return "${type.name}_$id"
  }
}