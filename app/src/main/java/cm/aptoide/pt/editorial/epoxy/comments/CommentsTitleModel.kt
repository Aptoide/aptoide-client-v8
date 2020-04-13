package cm.aptoide.pt.editorial.epoxy.comments

import android.view.View
import android.widget.Spinner
import android.widget.TextView
import cm.aptoide.pt.R
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder

@EpoxyModelClass(layout = R.layout.comments_title_item)
abstract class CommentsTitleModel : EpoxyModelWithHolder<CommentsTitleModel.CardHolder>() {

  @EpoxyAttribute
  var title: String? = null
  @EpoxyAttribute
  var count: Int? = null

  override fun bind(holder: CardHolder) {
    holder.title.text = title
    count?.let { c ->
      if (c < 0) {
        holder.count.visibility = View.INVISIBLE
      } else {
        holder.count.visibility = View.VISIBLE
        holder.count.text = "$count"
      }
    }

  }

  class CardHolder : BaseViewHolder() {
    val title by bind<TextView>(R.id.title)
    val count by bind<TextView>(R.id.count)
    val filter by bind<Spinner>(R.id.textBody)
  }
}