package cm.aptoide.pt.editorial.epoxy.comments

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

@EpoxyModelClass(layout = R.layout.reply_item)
abstract class ReplyModel : EpoxyModelWithHolder<ReplyModel.CardHolder>() {
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var dateUtils: AptoideUtils.DateTimeU? = null

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
    }
  }

  class CardHolder : BaseViewHolder() {
    val username by bind<TextView>(R.id.username)
    val avatar by bind<ImageView>(R.id.user_icon)
    val body by bind<TextView>(R.id.textBody)
    val timestamp by bind<TextView>(R.id.timestamp)
  }
}