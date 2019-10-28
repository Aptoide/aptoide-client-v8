package cm.aptoide.pt.home.apps.list.models

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import cm.aptoide.pt.R
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder
import kotlin.math.roundToInt
import androidx.constraintlayout.widget.ConstraintSet

@EpoxyModelClass(layout = R.layout.appc_apps_section_promo_header)
abstract class AppcHeaderModel : EpoxyModelWithHolder<AppcHeaderModel.Holder>() {

  @EpoxyAttribute
  var reward: Float = 0.0f

  override fun bind(holder: Holder) {
    val resources = holder.itemView.resources
    if (reward > 0) {
      holder.headerTitle.visibility = View.GONE
      holder.promotionTitle.visibility = View.VISIBLE
      holder.promotionMessage.visibility = View.VISIBLE
      holder.messageTextView.text =
          resources.getString(R.string.promo_update2appc_message).format(reward.roundToInt())

      val constraintSet = ConstraintSet()
      constraintSet.clone(holder.rootLayout)
      constraintSet.clear(R.id.appc_disclaimer_icon)
      constraintSet.connect(R.id.appc_disclaimer_icon, ConstraintSet.LEFT, ConstraintSet.PARENT_ID,
          ConstraintSet.LEFT)
      constraintSet.connect(R.id.appc_disclaimer_icon, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID,
          ConstraintSet.RIGHT)
      constraintSet.connect(R.id.appc_disclaimer_icon, ConstraintSet.TOP, R.id.message,
          ConstraintSet.BOTTOM)
      constraintSet.setHorizontalBias(R.id.appc_disclaimer_icon, 0.15f)
    } else {
      holder.headerTitle.visibility = View.VISIBLE
      holder.promotionTitle.visibility = View.GONE
      holder.promotionMessage.visibility = View.GONE

      val constraintSet = ConstraintSet()
      constraintSet.clone(holder.rootLayout)
      constraintSet.clear(R.id.appc_disclaimer_icon)
      constraintSet.connect(R.id.appc_disclaimer_icon, ConstraintSet.LEFT, R.id.title_1,
          ConstraintSet.LEFT)
      constraintSet.connect(R.id.appc_disclaimer_icon, ConstraintSet.TOP, R.id.title_1,
          ConstraintSet.BOTTOM)
    }

  }

  class Holder : BaseViewHolder() {
    val messageTextView by bind<TextView>(R.id.message)
    val headerTitle by bind<TextView>(R.id.title_1)
    val promotionTitle by bind<TextView>(R.id.title)
    val promotionMessage by bind<TextView>(R.id.message)
    val rootLayout by bind<ConstraintLayout>(R.id.root_layout)
  }
}
