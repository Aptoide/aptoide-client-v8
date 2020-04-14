package cm.aptoide.pt.editorial.epoxy.comments

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import cm.aptoide.pt.R
import cm.aptoide.pt.comments.refactor.data.Comment
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.utils.AptoideUtils
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder

@EpoxyModelClass(layout = R.layout.comment_item)
abstract class CommentModel : EpoxyModelWithHolder<CommentModel.CardHolder>() {

  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var dateUtils: AptoideUtils.DateTimeU? = null

  @EpoxyAttribute
  var comment: Comment? = null

  override fun bind(holder: CardHolder) {
    comment?.let { c ->
      holder.username.text = c.user?.name
      c.user?.avatar?.let { a ->
        ImageLoader.with(holder.itemView.context).loadUsingCircleTransform(a, holder.avatar)
      }
      holder.body.text = c.message
      val dateDiff: String =
          dateUtils?.getTimeDiffAll(holder.itemView.context, c.date?.time ?: 0,
              holder.itemView.context
                  .resources) ?: ""
      holder.timestamp.text = dateDiff
      if (c.repliesNr > 0) {
        holder.repliesButton.text = "${c.repliesNr} replies"
        holder.repliesButton.visibility = View.VISIBLE
      } else {
        holder.repliesButton.visibility = View.GONE
      }

    }
  }

  class CardHolder : BaseViewHolder() {
    val username by bind<TextView>(R.id.username)
    val avatar by bind<ImageView>(R.id.user_icon)
    val body by bind<TextView>(R.id.textBody)
    val timestamp by bind<TextView>(R.id.timestamp)
    val repliesButton by bind<Button>(R.id.toggleRepliesButton)
  }
}