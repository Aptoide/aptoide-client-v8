package cm.aptoide.pt.home.more.apps

import android.view.View
import cm.aptoide.pt.R
import cm.aptoide.pt.ads.data.AptoideNativeAd
import cm.aptoide.pt.home.bundles.apps.EskillsApp
import cm.aptoide.pt.home.more.base.ListAppsViewHolder
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.view.app.Application
import kotlinx.android.synthetic.main.app_home_item.view.*
import kotlinx.android.synthetic.main.appc_label.view.*
import kotlinx.android.synthetic.main.displayable_grid_app.view.icon
import kotlinx.android.synthetic.main.displayable_grid_app.view.name
import kotlinx.android.synthetic.main.rating_label.view.*
import java.text.DecimalFormat

class ListAppsMoreViewHolder(val view: View,
                             private val decimalFormatter: DecimalFormat) :
    ListAppsViewHolder<Application>(view) {
  override fun bindApp(app: Application) {
    itemView.name.text = app.name
    ImageLoader.with(itemView.context)
        .loadWithRoundCorners(app.icon, 8, itemView.icon, R.attr.placeholder_square)
    if (app is EskillsApp) {
      itemView.eskills_label.visibility = View.VISIBLE
      itemView.appc_info_layout.visibility = View.GONE
      itemView.rating_info_layout.visibility = View.GONE
      itemView.ad_label.visibility = View.GONE
    } else {
      itemView.eskills_label.visibility = View.GONE
      if (app.hasAppcBilling()) {
        itemView.appc_info_layout.visibility = View.VISIBLE
        itemView.appc_text.setText(R.string.appc_card_short)
        itemView.rating_info_layout.visibility = View.GONE
        itemView.ad_label.visibility = View.GONE
      } else {
        if (app is AptoideNativeAd) {
          itemView.ad_label.visibility = View.VISIBLE
          itemView.rating_info_layout.visibility = View.VISIBLE
          itemView.appc_info_layout.visibility = View.GONE
          itemView.rating_label.text = decimalFormatter.format(app.stars)
        } else {
          if (app.rating == 0f)
            itemView.rating_label.setText(R.string.appcardview_title_no_stars)
          else
            itemView.rating_label.text = decimalFormatter.format(app.rating)
          itemView.rating_info_layout.visibility = View.VISIBLE
          itemView.appc_info_layout.visibility = View.GONE
          itemView.ad_label.visibility = View.GONE

        }
      }
    }
  }
}