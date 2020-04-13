package cm.aptoide.pt.editorial.epoxy.comments

import cm.aptoide.pt.R
import cm.aptoide.pt.comments.refactor.data.Comment
import cm.aptoide.pt.utils.AptoideUtils
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelGroup
import java.util.*

class CommentGroupModel(private val dateUtils: AptoideUtils.DateTimeU,
                        private val comment: Comment) :
    EpoxyModelGroup(R.layout.comment_model_container, buildModels(comment, dateUtils)) {

  companion object {
    fun buildModels(comment: Comment,
                    dateUtils: AptoideUtils.DateTimeU): List<EpoxyModel<*>> {
      val models = ArrayList<EpoxyModel<*>>()
      models.add(
          CommentModel_()
              .id(comment.id)
              .comment(comment)
              .dateUtils(dateUtils)
      )

      return models
    }
  }
}