package cm.aptoide.pt.editorial.epoxy.comments

import cm.aptoide.pt.R
import cm.aptoide.pt.comments.refactor.data.Comment
import cm.aptoide.pt.utils.AptoideUtils
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelGroup
import rx.subjects.PublishSubject
import java.util.*

class CommentGroupModel(private val dateUtils: AptoideUtils.DateTimeU,
                        private val comment: Comment,
                        private val commentEventSubject: PublishSubject<CommentEvent>) :
    EpoxyModelGroup(R.layout.comment_model_container,
        buildModels(comment, dateUtils, commentEventSubject)) {

  companion object {
    fun buildModels(comment: Comment,
                    dateUtils: AptoideUtils.DateTimeU,
                    commentEventSubject: PublishSubject<CommentEvent>): List<EpoxyModel<*>> {
      val models = ArrayList<EpoxyModel<*>>()
      models.add(
          CommentModel_()
              .id(comment.id)
              .comment(comment)
              .eventSubject(commentEventSubject)
              .dateUtils(dateUtils)
      )

      return models
    }
  }
}