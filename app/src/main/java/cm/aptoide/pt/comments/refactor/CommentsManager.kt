package cm.aptoide.pt.comments.refactor

import cm.aptoide.pt.comments.refactor.data.Comment
import cm.aptoide.pt.comments.refactor.data.CommentsResponseModel
import cm.aptoide.pt.dataprovider.util.CommentType
import cm.aptoide.pt.editorial.epoxy.comments.CommentFilters
import rx.Completable
import rx.Observable
import rx.Single

open class CommentsManager(val repository: CommentsRepository) {

  fun observeComments(id: Long, type: CommentType,
                      defaultFilters: CommentFilters): Observable<CommentsResponseModel> {
    return repository.observeComments(id, type, defaultFilters)
  }

  fun loadComments(id: Long, type: CommentType,
                   defaultFilters: CommentFilters): Single<CommentsResponseModel> {
    return repository.loadComments(id, type, defaultFilters)
  }

  fun loadFreshComments(id: Long, type: CommentType,
                        defaultFilters: CommentFilters): Single<CommentsResponseModel> {
    return repository.loadFreshComments(id, type, defaultFilters)
  }

  fun loadMoreComments(id: Long, type: CommentType,
                       defaultFilters: CommentFilters): Single<CommentsResponseModel> {
    return repository.loadMoreComments(id, type, defaultFilters)
  }

  fun showCommentReplies(comment: Comment, id: Long, type: CommentType): Completable {
    return repository.showCommentReplies(comment, id, type)
  }

  fun hideCommentReplies(comment: Comment, id: Long, type: CommentType): Completable {
    return repository.hideCommentReplies(comment, id, type)
  }

}