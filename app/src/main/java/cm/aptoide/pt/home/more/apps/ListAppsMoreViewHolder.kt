package cm.aptoide.pt.home.more.apps

import android.view.View
import cm.aptoide.pt.R
import cm.aptoide.pt.home.more.base.ListAppsViewHolder
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.view.app.Application
import kotlinx.android.synthetic.main.appc_label.view.*
import kotlinx.android.synthetic.main.displayable_grid_app.view.*
import kotlinx.android.synthetic.main.rating_label.view.*
import java.text.DecimalFormat

class ListAppsMoreViewHolder(val view: View,
                             private val decimalFormatter: DecimalFormat) :
    ListAppsViewHolder<Application>(view) {
  override fun bindApp(app: Application) {
    itemView.name.text = app.name
    ImageLoader.with(itemView.context)
        .loadWithRoundCorners(app.icon, 8, itemView.icon, R.drawable.placeholder_square)
    if (app.hasAppcBilling()) {
      itemView.appc_info_layout.visibility = View.VISIBLE
      itemView.appc_text.setText(R.string.appc_card_short)
      itemView.rating_info_layout.visibility = View.GONE
    } else {
      if (app.rating == 0f)
        itemView.rating_label.setText(R.string.appcardview_title_no_stars)
      else
        itemView.rating_label.text = decimalFormatter.format(app.rating)
      itemView.rating_info_layout.visibility = View.VISIBLE
      itemView.appc_info_layout.visibility = View.GONE
    }
    val cardHeight = itemView.height
    itemView.icon.layoutParams.height = cardHeight / 2
  }
}