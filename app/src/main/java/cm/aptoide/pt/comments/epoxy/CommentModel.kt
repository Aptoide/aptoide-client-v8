package cm.aptoide.pt.comments.epoxy

import cm.aptoide.pt.R
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder

@EpoxyModelClass(layout = R.layout.comment_item)
abstract class CommentModel : EpoxyModelWithHolder<CommentModel.CardHolder>() {

  class CardHolder : BaseViewHolder() {

  }
}