package cm.aptoide.pt.comments.epoxy

import com.airbnb.epoxy.Typed2EpoxyController

class CommentsControler : Typed2EpoxyController<Boolean, List<String>>() {

  override fun buildModels(isAbletoComment: Boolean, data2: List<String>) {
  }
}