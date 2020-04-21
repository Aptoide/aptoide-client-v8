package cm.aptoide.pt.comments.refactor

import cm.aptoide.accountmanager.AptoideAccountManager
import cm.aptoide.accountmanager.LocalAccount
import cm.aptoide.pt.comments.refactor.data.CommentsWrapperModel
import cm.aptoide.pt.comments.refactor.data.User
import cm.aptoide.pt.dataprovider.util.CommentType
import cm.aptoide.pt.editorial.epoxy.comments.CommentFilters
import rx.Observable
import rx.Single

open class CommentsManager(val repository: CommentsRepository,
                           val accountManager: AptoideAccountManager) {

  fun observeComments(id: Long, type: CommentType,
                      defaultFilters: CommentFilters): Observable<CommentsWrapperModel> {
    return getUser().doOnError { throwable -> throwable.printStackTrace() }
        .flatMapObservable { user ->
          repository.observeComments(id, type, defaultFilters)
              .map { commentsResponseModel -> CommentsWrapperModel(commentsResponseModel, user) }
        }
  }

  fun loadComments(id: Long, type: CommentType,
                   defaultFilters: CommentFilters): Single<CommentsWrapperModel> {
    return getUser().flatMap { user ->
      repository.loadComments(id, type, defaultFilters)
          .map { commentsResponseModel -> CommentsWrapperModel(commentsResponseModel, user) }
    }
  }

  fun loadFreshComments(id: Long, type: CommentType,
                        defaultFilters: CommentFilters): Single<CommentsWrapperModel> {
    return getUser().flatMap { user ->
      repository.loadFreshComments(id, type, defaultFilters)
          .map { commentsResponseModel -> CommentsWrapperModel(commentsResponseModel, user) }
    }
  }

  fun loadMoreComments(id: Long, type: CommentType,
                       defaultFilters: CommentFilters): Single<CommentsWrapperModel> {
    return getUser().flatMap { user ->
      repository.loadMoreComments(id, type, defaultFilters)
          .map { commentsResponseModel -> CommentsWrapperModel(commentsResponseModel, user) }
    }
  }

  private fun getUser(): Single<User> {
    return accountManager.accountStatus().first().toSingle()
        .map { account ->
          if (account is LocalAccount) {
            User(account.avatar, account.nickname)
          } else {
            User(account.id.toLong(), account.avatar, account.nickname)
          }
        }
  }

}