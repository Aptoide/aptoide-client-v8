package cm.aptoide.pt.comments.refactor

import cm.aptoide.pt.comments.refactor.data.CommentsResponseModel
import cm.aptoide.pt.utils.AptoideUtils
import com.airbnb.epoxy.Typed2EpoxyController

class CommentsControler(val dateUtils: AptoideUtils.DateTimeU) :
    Typed2EpoxyController<Boolean, CommentsResponseModel>() {

  override fun buildModels(isAbletoComment: Boolean, responseModel: CommentsResponseModel) {
    for (comment in responseModel.comments) {
      CommentModel_()
          .id(comment.id)
          .comment(comment)
          .dateUtils(dateUtils)
          .addTo(this)
    }
  }
}