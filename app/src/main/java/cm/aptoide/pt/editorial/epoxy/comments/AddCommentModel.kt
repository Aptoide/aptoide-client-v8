package cm.aptoide.pt.editorial.epoxy.comments

import android.widget.ImageView
import android.widget.TextView
import cm.aptoide.pt.R
import cm.aptoide.pt.comments.refactor.data.User
import cm.aptoide.pt.networking.image.ImageLoader
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder
import rx.subjects.PublishSubject

@EpoxyModelClass(layout = R.layout.add_a_comment_layout)
abstract class AddCommentModel : EpoxyModelWithHolder<AddCommentModel.CardHolder>() {

  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var eventSubject: PublishSubject<CommentEvent>? = null

  @EpoxyAttribute
  var user: User? = null

  override fun bind(holder: CardHolder) {
    super.bind(holder)
    user?.avatar?.let { a ->
      ImageLoader.with(holder.itemView.context).loadUsingCircleTransform(a, holder.avatar)
    }
    //holder.commentEditText.set
  }

  class CardHolder : BaseViewHolder() {
    val avatar by bind<ImageView>(R.id.user_icon)
    val commentEditText by bind<TextView>(R.id.add_comment_text)
  }
}