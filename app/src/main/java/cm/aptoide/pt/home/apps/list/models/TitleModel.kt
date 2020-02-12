package cm.aptoide.pt.home.apps.list.models

import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import cm.aptoide.pt.R
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder
import rx.subjects.PublishSubject

@EpoxyModelClass(layout = R.layout.apps_header_item)
abstract class TitleModel : EpoxyModelWithHolder<TitleModel.Holder>() {

  @EpoxyAttribute
  @StringRes
  var title: Int? = null

  @EpoxyAttribute
  var shouldShowButton: Boolean = false

  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var eventSubject: PublishSubject<Void>? = null

  override fun bind(holder: Holder) {
    title?.let { titleText ->
      holder.title.text = holder.itemView.context.getString(titleText)
    }
    if (shouldShowButton) {
      holder.button.visibility = View.VISIBLE
    } else {
      holder.button.visibility = View.GONE
    }
    holder.button.setOnClickListener {
      eventSubject?.onNext(null)
    }
  }

  class Holder : BaseViewHolder() {
    val title by bind<TextView>(R.id.apps_header_title)
    val button by bind<TextView>(R.id.apps_header_button)
  }
}