package cm.aptoide.pt.comments.refactor

import android.util.Log
import cm.aptoide.pt.comments.refactor.data.CommentsResponseModel
import cm.aptoide.pt.comments.refactor.network.CommentsDataSource
import cm.aptoide.pt.dataprovider.util.CommentType
import rx.Observable
import rx.Single
import rx.subjects.BehaviorSubject

class CommentsRepository(val dataSource: CommentsDataSource) {
  private val cache = HashMap<String, BehaviorSubject<CommentsResponseModel>>()
  private val offsetCache = HashMap<String, Int>()

  fun observeComments(id: Long, type: CommentType): Observable<CommentsResponseModel> {
    val cacheKey = getCacheKey(id, type)
    return cache[cacheKey]
        ?: Observable.merge(Observable.just(CommentsResponseModel(true)),
            dataSource.loadComments(id, type, false)
                .flatMapObservable { c ->
                  cache[cacheKey] = BehaviorSubject.create(c)
                  offsetCache[cacheKey] = c.offset
                  return@flatMapObservable cache[cacheKey]
                })
  }

  fun loadComments(id: Long, type: CommentType): Single<CommentsResponseModel> {
    return observeComments(id, type).toSingle()
  }

  fun loadMoreComments(id: Long, type: CommentType): Single<CommentsResponseModel> {
    val cacheKey = getCacheKey(id, type)
    val offset = offsetCache[cacheKey] ?: 0
    val value: BehaviorSubject<CommentsResponseModel>? = cache[cacheKey]
    if (offset == 0 || value == null) {
      return loadComments(id, type)
    }
    return value.first().toSingle()
        .flatMap { cached ->
          if (!cached.hasMore()) {
            return@flatMap Single.just(cached)
          }
          return@flatMap dataSource.loadNextComments(id, type, offset)
              .map { next ->
                val comments = mergeComments(cached, next)
                cache[cacheKey]?.onNext(comments)
                offsetCache[cacheKey] = next.offset
                return@map comments
              }
        }
        .doOnError { e -> Log.e("ERROR", e.message) }
  }

  private fun mergeComments(cachedComments: CommentsResponseModel,
                            nextComments: CommentsResponseModel): CommentsResponseModel {
    var commentList = ArrayList(cachedComments.comments)
    commentList.addAll(nextComments.comments)
    return CommentsResponseModel(commentList, nextComments.offset, nextComments.total)
  }

  private fun getCacheKey(id: Long, type: CommentType): String {
    return "${type.name}_$id"
  }
}