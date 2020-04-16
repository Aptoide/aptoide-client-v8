package cm.aptoide.pt.editorial.epoxy.comments

import android.animation.LayoutTransition
import android.widget.LinearLayout
import cm.aptoide.pt.R
import cm.aptoide.pt.comments.refactor.data.Comment
import cm.aptoide.pt.utils.AptoideUtils
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelGroup
import com.airbnb.epoxy.ModelGroupHolder
import rx.subjects.PublishSubject
import java.util.*


class CommentGroupModel(private val dateUtils: AptoideUtils.DateTimeU,
                        private val comment: Comment,
                        private val commentEventSubject: PublishSubject<CommentEvent>) :
    EpoxyModelGroup(R.layout.comment_model_container,
        buildModels(comment, dateUtils, commentEventSubject)) {

  override fun bind(holder: ModelGroupHolder) {
    val lt = LayoutTransition()
    lt.disableTransitionType(LayoutTransition.CHANGE_APPEARING)
    lt.disableTransitionType(LayoutTransition.CHANGING)
    lt.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING)
    holder.rootView.findViewById<LinearLayout>(R.id.epoxy_model_group_child_container)
        .layoutTransition = lt
    super.bind(holder)
  }

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

      val repliesToShowNr = comment.getReplies().size.coerceAtMost(comment.repliesToShowNr)
      val repliesLeftToShow = comment.repliesNr - repliesToShowNr
      if (repliesLeftToShow > 0) {
        // TODO: Add See more
      }
      for (i in 0 until repliesToShowNr) {
        val reply = comment.getReplies()[i]
        models.add(
            ReplyModel_()
                .id(reply.id)
                .dateUtils(dateUtils)
                .comment(reply)
        )
      }

      return models
    }
  }
}