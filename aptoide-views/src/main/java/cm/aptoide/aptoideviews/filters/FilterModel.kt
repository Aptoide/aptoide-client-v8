package cm.aptoide.aptoideviews.filters

import android.widget.CheckedTextView
import cm.aptoide.aptoideviews.R
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder

@EpoxyModelClass
abstract class FilterModel : EpoxyModelWithHolder<FilterModel.CardHolder>() {
  @EpoxyAttribute
  var filter: Filter? = null

  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var eventListener: FilterEventListener? = null

  override fun bind(holder: CardHolder) {
    super.bind(holder)

    filter?.let { f ->
      holder.button.text = f.name
      holder.button.isChecked = f.selected
      holder.button.setOnClickListener {
        eventListener?.onFilterEvent(FilterEventListener.EventType.FILTER_CLICK, f)
      }
    }
  }

  /**
   * For some weird reason @EpoxyModelClass(layout = R.layout.filter_item)
   * doesn't work in this module
   */
  override fun getDefaultLayout(): Int {
    return R.layout.filter_item
  }

  class CardHolder : BaseViewHolder() {
    val button by bind<CheckedTextView>(R.id.filter_button)

  }
}