package cm.aptoide.pt.editorial.epoxy.comments

import android.widget.TextView
import cm.aptoide.aptoideviews.safeLet
import cm.aptoide.pt.R
import cm.aptoide.pt.comments.refactor.data.Comment
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder
import rx.subjects.PublishSubject

@EpoxyModelClass(layout = R.layout.see_more_item)
abstract class SeeMoreModel : EpoxyModelWithHolder<SeeMoreModel.CardHolder>() {

  @EpoxyAttribute
  var comment: Comment? = null
  @EpoxyAttribute
  var repliesLeft: Int? = null

  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var eventSubject: PublishSubject<CommentEvent>? = null

  override fun bind(holder: CardHolder) {
    safeLet(repliesLeft, comment) { r, c ->
      var text = holder.itemView.context.getString(R.string.see_more) + " ($r)"
      if (c.loadingReplies) {
        text = "Loading..." // TODO: Hardcoded
      }
      holder.seeMoreTextView.text = text
      holder.itemView.setOnClickListener {
        eventSubject?.onNext(CommentEvent(c, CommentEvent.Type.SEE_MORE_CLICK))
      }
    }
  }

  class CardHolder : BaseViewHolder() {
    val seeMoreTextView by bind<TextView>(R.id.seeMoreTextView)
  }
}