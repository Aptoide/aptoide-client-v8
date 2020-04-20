package cm.aptoide.pt.comments.refactor

import cm.aptoide.pt.comments.refactor.data.Comment
import cm.aptoide.pt.comments.refactor.data.CommentsResponseModel
import cm.aptoide.pt.comments.refactor.network.CommentsDataSource
import cm.aptoide.pt.dataprovider.util.CommentType
import cm.aptoide.pt.editorial.epoxy.comments.CommentFilters
import rx.Completable
import rx.Observable
import rx.Single
import rx.subjects.BehaviorSubject

class CommentsRepository(val dataSource: CommentsDataSource) {
  private val cache = HashMap<String, BehaviorSubject<CommentsResponseModel>>()
  private val offsetCache = HashMap<String, Int>()

  fun observeComments(id: Long, type: CommentType,
                      defaultFilters: CommentFilters): Observable<CommentsResponseModel> {
    val cacheKey = getCacheKey(id, type)
    return cache[cacheKey]
        ?: Observable.merge(Observable.just(CommentsResponseModel(true, defaultFilters)),
            dataSource.loadComments(id, type, defaultFilters, false)
                .flatMapObservable { c ->
                  cache[cacheKey] = BehaviorSubject.create(c)
                  offsetCache[cacheKey] = c.offset
                  return@flatMapObservable cache[cacheKey]
                })
  }

  fun loadComments(id: Long, type: CommentType,
                   defaultFilters: CommentFilters): Single<CommentsResponseModel> {
    return observeComments(id, type, defaultFilters).toSingle()
  }

  fun loadFreshComments(id: Long, type: CommentType,
                        filters: CommentFilters): Single<CommentsResponseModel> {
    val cacheKey = getCacheKey(id, type)
    return dataSource.loadComments(id, type, filters, true)
        .doOnSuccess { c ->
          cache[cacheKey].let { cachedValue ->
            if (cachedValue != null) {
              cache[cacheKey]?.onNext(c)
            } else {
              cache[cacheKey] = BehaviorSubject.create(c)
            }
          }
          offsetCache[cacheKey] = c.offset
        }
  }

  fun loadMoreComments(id: Long, type: CommentType,
                       defaultFilters: CommentFilters): Single<CommentsResponseModel> {
    val cacheKey = getCacheKey(id, type)
    val offset = offsetCache[cacheKey] ?: 0
    val value: BehaviorSubject<CommentsResponseModel>? = cache[cacheKey]
    if (offset == 0 || value == null) {
      return loadComments(id, type, defaultFilters)
    }
    return value.first().toSingle()
        .flatMap { cached ->
          if (!cached.hasMore()) {
            return@flatMap Single.just(cached)
          }
          return@flatMap dataSource.loadNextComments(id, type, cached.filters, offset)
              .flatMap { next ->
                // We load the latest cache value again before merge to avoid sync issues between
                // the WS request and response (the state might've changed)
                cache[cacheKey]?.first()?.toSingle()?.map { cached2 ->
                  val comments: CommentsResponseModel = mergeComments(cached2, next)
                  cache[cacheKey]?.onNext(comments)
                  offsetCache[cacheKey] = next.offset
                  return@map comments
                }
              }
        }
  }

  fun loadMoreReplies(comment: Comment, id: Long,
                      type: CommentType, nrRepliesToLoad: Int): Single<CommentsResponseModel> {
    val cacheKey = getCacheKey(id, type)
    val value: BehaviorSubject<CommentsResponseModel> = cache[cacheKey]
        ?: return Single.just(CommentsResponseModel(true))

    return value.first().toSingle().flatMap { cached ->
      val position = getCommentPosition(comment, cached.comments)
      if (position >= 0 && cached.comments[position].getReplies().size >= cached.comments[position].repliesNr) {
        return@flatMap Single.just(cached)
      }
      // We change repliesToShow BEFORE the WS. This is important because if we change after the WS
      // response it may cause sync issues (e.g. between the request and response,
      // the user might want to hide the comments)
      return@flatMap setCommentRepliesToLoading(comment.repliesToShowNr + nrRepliesToLoad, comment,
          id, type)
          .andThen(dataSource.loadReplies(comment.id, comment.getReplies().size - 1, cached.filters,
              type, nrRepliesToLoad)
              .flatMap { response ->
                // We load the latest cache value again before merge to avoid sync issues between
                // the WS request and response (the state might've changed)
                cache[cacheKey]?.first()?.toSingle()?.map { cached2 ->
                  val comments: CommentsResponseModel = mergeReplies(cached2, comment, response)
                  cache[cacheKey]?.onNext(comments)
                  return@map comments
                }
              })
    }
  }

  fun showCommentReplies(comment: Comment, id: Long, type: CommentType): Completable {
    return showCommentReplies(3, comment, id, type)
  }

  fun hideCommentReplies(comment: Comment, id: Long, type: CommentType): Completable {
    return showCommentReplies(0, comment, id, type)
  }

  private fun setCommentRepliesToLoading(nrReplies: Int, comment: Comment, id: Long,
                                         type: CommentType): Completable {
    val cacheKey = getCacheKey(id, type)
    val cachedComment = cache[cacheKey] ?: return Completable.complete()
    return cachedComment.doOnNext { responseModel ->
      val position = getCommentPosition(comment, responseModel.comments)
      if (position >= 0) {
        val newList = ArrayList(responseModel.comments)
        newList[position] = Comment(responseModel.comments[position], nrReplies, true)
        cache[cacheKey]?.onNext(
            CommentsResponseModel(newList, responseModel.offset, responseModel.total,
                responseModel.filters, responseModel.loading))
      }
    }.first().toSingle().toCompletable()
  }

  private fun showCommentReplies(nrReplies: Int, comment: Comment, id: Long,
                                 type: CommentType): Completable {
    val cacheKey = getCacheKey(id, type)
    val cachedComment = cache[cacheKey] ?: return Completable.complete()
    return cachedComment.doOnNext { responseModel ->
      val position = getCommentPosition(comment, responseModel.comments)
      if (position >= 0) {
        val newList = ArrayList(responseModel.comments)
        newList[position] = Comment(responseModel.comments[position], nrReplies)
        cache[cacheKey]?.onNext(
            CommentsResponseModel(newList, responseModel.offset, responseModel.total,
                responseModel.filters, responseModel.loading))
      }
    }.first().toSingle().toCompletable()
  }


  private fun getCommentPosition(comment: Comment, list: List<Comment>): Int {
    for ((index, c) in list.withIndex()) {
      if (c.id == comment.id) {
        return index
      }
    }
    return -1
  }

  private fun mergeComments(cachedComments: CommentsResponseModel,
                            nextComments: CommentsResponseModel): CommentsResponseModel {
    val commentList = ArrayList(cachedComments.comments)
    commentList.addAll(nextComments.comments)
    return CommentsResponseModel(commentList, nextComments.offset, nextComments.total,
        nextComments.filters)
  }

  private fun mergeReplies(cachedComments: CommentsResponseModel,
                           parentComment: Comment,
                           replies: CommentsResponseModel): CommentsResponseModel {
    val position = getCommentPosition(parentComment, cachedComments.comments)
    if (position < 0) {
      return cachedComments
    }
    val commentsCopy = ArrayList(cachedComments.comments)
    if (replies.loading) {
      commentsCopy[position] = Comment(commentsCopy[position], true)
    } else {
      val parentCommentReplies = ArrayList(cachedComments.comments[position].getReplies())
      parentCommentReplies.addAll(0, replies.comments)
      commentsCopy[position] = Comment(commentsCopy[position], parentCommentReplies, false)
    }

    return CommentsResponseModel(commentsCopy, cachedComments.offset,
        cachedComments.total,
        cachedComments.filters)
  }

  private fun getCacheKey(id: Long, type: CommentType): String {
    return "${type.name}_$id"
  }
}