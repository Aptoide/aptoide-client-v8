package cm.aptoide.pt.editorial.epoxy.comments

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cm.aptoide.aptoideviews.common.AnimatedImageView
import cm.aptoide.pt.R
import cm.aptoide.pt.comments.refactor.data.Comment
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.utils.AptoideUtils
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder
import rx.subjects.PublishSubject

@EpoxyModelClass(layout = R.layout.comment_item)
abstract class CommentModel : EpoxyModelWithHolder<CommentModel.CardHolder>() {

  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var dateUtils: AptoideUtils.DateTimeU? = null

  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var eventSubject: PublishSubject<CommentEvent>? = null

  @EpoxyAttribute
  var comment: Comment? = null

  var isExpanded = false

  override fun bind(holder: CardHolder) {
    comment?.let { c ->
      holder.username.text = c.user?.name
      c.user?.avatar?.let { a ->
        ImageLoader.with(holder.itemView.context).loadUsingCircleTransform(a, holder.avatar)
      }
      holder.body.maxLines = 4
      holder.body.text = c.message
      holder.body.setOnClickListener {
        if (isExpanded) {
          holder.body.maxLines = 4
        } else {
          holder.body.maxLines = Integer.MAX_VALUE
        }
        isExpanded = !isExpanded
      }
      val dateDiff: String =
          dateUtils?.getTimeDiffAll(holder.itemView.context, c.date?.time ?: 0,
              holder.itemView.context
                  .resources) ?: ""
      holder.timestamp.text = dateDiff
      if (c.repliesNr > 0) {
        holder.showRepliesText.text =
            holder.itemView.context.getString(R.string.reviews_expand_button, c.repliesNr)
        holder.showRepliesButton.visibility = View.VISIBLE
      } else {
        holder.showRepliesButton.visibility = View.GONE
      }
      setListeners(holder, c)
    }
  }

  private fun setListeners(holder: CardHolder, c: Comment) {
    holder.replyButton.setOnClickListener {
      eventSubject?.onNext(CommentEvent(c, CommentEvent.Type.REPLY_CLICK))
    }
    holder.userSection.setOnClickListener {
      eventSubject?.onNext(CommentEvent(c, CommentEvent.Type.USER_PROFILE_CLICK))
    }
    holder.showRepliesButton.setOnClickListener {
      if (c.repliesToShowNr > 0) {
        holder.showRepliesIcon.play()
        eventSubject?.onNext(CommentEvent(c, CommentEvent.Type.HIDE_REPLIES_CLICK))
      } else {
        holder.showRepliesIcon.playReverse()
        eventSubject?.onNext(CommentEvent(c, CommentEvent.Type.SHOW_REPLIES_CLICK))
      }
    }
  }

  class CardHolder : BaseViewHolder() {
    val username by bind<TextView>(R.id.username)
    val avatar by bind<ImageView>(R.id.user_icon)
    val body by bind<TextView>(R.id.textBody)
    val timestamp by bind<TextView>(R.id.timestamp)
    val showRepliesButton by bind<View>(R.id.toggleRepliesButton)
    val showRepliesText by bind<TextView>(R.id.toggleRepliesText)
    val showRepliesIcon by bind<AnimatedImageView>(R.id.toggleRepliesIcon)
    val replyButton by bind<View>(R.id.replyButton)
    val userSection by bind<View>(R.id.user_clickable_section)
  }
}