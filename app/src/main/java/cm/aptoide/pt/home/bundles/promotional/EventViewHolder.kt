package cm.aptoide.pt.home.bundles.promotional

import android.view.View
import cm.aptoide.aptoideviews.skeleton.Skeleton
import cm.aptoide.pt.home.bundles.base.EditorialActionBundle
import cm.aptoide.pt.home.bundles.base.HomeBundle
import cm.aptoide.pt.home.bundles.base.HomeEvent
import cm.aptoide.pt.home.bundles.editorial.EditorialHomeEvent
import cm.aptoide.pt.home.bundles.editorial.EditorialViewHolder
import cm.aptoide.pt.networking.image.ImageLoader
import kotlinx.android.synthetic.main.card_event.view.*
import rx.subjects.PublishSubject

class EventViewHolder(val view: View,
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
        itemView.event_title.text = homeBundle.actionItem.title
        itemView.event_summary.text = homeBundle.actionItem.summary
        itemView.setOnClickListener {
          uiEventsListener.onNext(
              EditorialHomeEvent(homeBundle.actionItem.cardId, homeBundle.actionItem.type,
                  homeBundle, position, HomeEvent.Type.EDITORIAL))
        }
      }
    }
  }

  private fun toggleSkeleton(showSkeleton: Boolean) {
    if (showSkeleton) {
      skeleton?.showSkeleton()
      itemView.card_title_label_skeletonview.visibility = View.VISIBLE
      itemView.card_title_label.visibility = View.INVISIBLE
      itemView.event_title_skeletonview.visibility = View.VISIBLE
      itemView.event_title.visibility = View.INVISIBLE
      itemView.event_ongoing.visibility = View.INVISIBLE
      itemView.event_summary_skeletonview.visibility = View.VISIBLE
      itemView.event_on_going_skeletonview.visibility = View.VISIBLE
    } else {
      skeleton?.showOriginal()
      itemView.card_title_label_skeletonview.visibility = View.INVISIBLE
      itemView.card_title_label.visibility = View.VISIBLE
      itemView.event_title_skeletonview.visibility = View.INVISIBLE
      itemView.event_title.visibility = View.VISIBLE
      itemView.event_ongoing.visibility = View.VISIBLE
      itemView.event_summary_skeletonview.visibility = View.INVISIBLE
      itemView.event_on_going_skeletonview.visibility = View.INVISIBLE
    }
  }
}