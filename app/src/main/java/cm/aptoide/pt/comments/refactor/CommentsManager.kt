package cm.aptoide.pt.comments.refactor

import cm.aptoide.pt.comments.refactor.data.CommentsResponseModel
import rx.Single

class CommentsManager(val repository: CommentsRepository) {

  private lateinit var configuration: CommentsConfiguration

  fun setConfiguration(configuration: CommentsConfiguration) {
    this.configuration = configuration
  }

  fun loadComments(): Single<CommentsResponseModel> {
    return repository.loadComments(configuration.id, configuration.commentType)
  }
}