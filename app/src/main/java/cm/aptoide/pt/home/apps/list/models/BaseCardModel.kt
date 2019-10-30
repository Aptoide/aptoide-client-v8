package cm.aptoide.pt.home.apps.list.models

import cm.aptoide.pt.home.apps.App
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder

abstract class BaseCardModel<T : EpoxyHolder> : EpoxyModelWithHolder<T>() {
  @EpoxyAttribute
  var application: App? = null
}
