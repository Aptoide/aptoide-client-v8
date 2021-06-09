package cm.aptoide.pt.home.bundles.promotional

import android.view.View
import cm.aptoide.aptoideviews.skeleton.Skeleton
import cm.aptoide.pt.home.bundles.base.EditorialActionBundle
import cm.aptoide.pt.home.bundles.base.HomeBundle
import cm.aptoide.pt.home.bundles.base.HomeEvent
import cm.aptoide.pt.home.bundles.editorial.EditorialHomeEvent
import cm.aptoide.pt.home.bundles.editorial.EditorialViewHolder
import cm.aptoide.pt.networking.image.ImageLoader
import kotlinx.android.synthetic.main.card_new_package.view.app_background_image
import kotlinx.android.synthetic.main.card_new_package.view.card_title_label
import kotlinx.android.synthetic.main.card_new_package.view.card_title_label_skeletonview
import kotlinx.android.synthetic.main.card_new_package.view.card_title_label_text
import kotlinx.android.synthetic.main.card_news.view.*
import rx.subjects.PublishSubject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NewsViewHolder(val view: View,
                     val uiEventsListener: PublishSubject<HomeEvent>) :
    EditorialViewHolder(view) {

  private var skeleton: Skeleton? = null

  override fun setBundle(homeBundle: HomeBundle?, position: Int) {
    (homeBundle as? EditorialActionBundle)?.let {
      if (homeBundle.content == null) {
        toggleSkeleton(true)
      } else {
        toggleSkeleton(false)
        itemView.card_title_label_text.text = homeBundle.title
        ImageLoader.with(itemView.context)
            .load(homeBundle.actionItem.icon, itemView.app_background_image)
        itemView.news_title.text = homeBundle.actionItem.title
        itemView.news_date.text = homeBundle.actionItem.date
        itemView.news_summary.text = homeBundle.actionItem.subTitle
        setupCalendarDateString(homeBundle.actionItem.date)
        itemView.setOnClickListener {
          uiEventsListener.onNext(
              EditorialHomeEvent(homeBundle.actionItem.cardId, homeBundle.actionItem.type,
                  homeBundle, position, HomeEvent.Type.EDITORIAL))
        }
      }
    }
  }

  private fun setupCalendarDateString(date: String) {
    val dateSplitted = date.split(" ").toTypedArray()
    val newFormatDate = dateSplitted[0].replace("-", "/")
    val dateFormat = SimpleDateFormat("yyyy/MM/dd")
    var newDate: Date? = null
    val formattedDate: String

    newDate = dateFormat.parse(newFormatDate)

    if (newDate != null) {
      formattedDate = DateFormat.getDateInstance(DateFormat.SHORT)
          .format(newDate)
      itemView.news_date.text = formattedDate
    }
  }

  private fun toggleSkeleton(showSkeleton: Boolean) {
    if (showSkeleton) {
      skeleton?.showSkeleton()
      itemView.card_title_label_skeletonview.visibility = View.VISIBLE
      itemView.card_title_label.visibility = View.INVISIBLE
      itemView.news_title_skeletonview.visibility = View.VISIBLE
      itemView.news_title.visibility = View.INVISIBLE
      itemView.news_date_skeletonview.visibility = View.VISIBLE
      itemView.news_date.visibility = View.INVISIBLE
      itemView.news_summary.visibility = View.INVISIBLE
      itemView.news_summary_skeletonview_1.visibility = View.VISIBLE
      itemView.news_summary_skeletonview_2.visibility = View.VISIBLE
    } else {
      skeleton?.showOriginal()
      itemView.card_title_label_skeletonview.visibility = View.INVISIBLE
      itemView.card_title_label.visibility = View.VISIBLE
      itemView.news_title_skeletonview.visibility = View.INVISIBLE
      itemView.news_title.visibility = View.VISIBLE
      itemView.news_date_skeletonview.visibility = View.INVISIBLE
      itemView.news_date.visibility = View.VISIBLE
      itemView.news_summary.visibility = View.VISIBLE
      itemView.news_summary_skeletonview_1.visibility = View.INVISIBLE
      itemView.news_summary_skeletonview_2.visibility = View.INVISIBLE
    }
  }
}