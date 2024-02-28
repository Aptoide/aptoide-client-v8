package cm.aptoide.pt.home.more.eskills

import android.view.View
import cm.aptoide.pt.R
import cm.aptoide.pt.home.more.apps.ListAppsMoreViewHolder
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.view.app.Application
import kotlinx.android.synthetic.main.displayable_grid_app.view.icon
import kotlinx.android.synthetic.main.displayable_grid_app.view.name
import kotlinx.android.synthetic.main.rating_label.view.rating_label
import java.text.DecimalFormat

class ListAppsEskillsViewHolder(
  view: View, private val decimalFormatter: DecimalFormat
) : ListAppsMoreViewHolder(view, decimalFormatter) {
  override fun bindApp(app: Application) {
    itemView.name.text = app.name
    ImageLoader.with(itemView.context)
      .loadWithRoundCorners(app.icon, 8, itemView.icon, R.attr.placeholder_square)
    if (app.rating == 0f) itemView.rating_label.setText(R.string.appcardview_title_no_stars)
    else itemView.rating_label.text = decimalFormatter.format(app.rating)
  }
}


