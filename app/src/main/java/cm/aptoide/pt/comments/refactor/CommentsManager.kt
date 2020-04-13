package cm.aptoide.pt.comments.refactor

import cm.aptoide.pt.comments.refactor.data.CommentsResponseModel
import cm.aptoide.pt.dataprovider.util.CommentType
import rx.Observable
import rx.Single

class CommentsManager(val repository: CommentsRepository) {

  fun observeComments(id: Long, type: CommentType): Observable<CommentsResponseModel> {
    return repository.observeComments(id, type)
  }

  fun loadComments(id: Long, type: CommentType): Single<CommentsResponseModel> {
    return repository.loadComments(id, type)
  }

  fun loadMoreComments(id: Long, type: CommentType): Single<CommentsResponseModel> {
    return repository.loadMoreComments(id, type)
  }

}