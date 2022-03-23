package cm.aptoide.pt.home.bundles.promotional

import android.view.View
import cm.aptoide.aptoideviews.skeleton.Skeleton
import cm.aptoide.pt.R
import cm.aptoide.pt.home.bundles.base.AppBundleViewHolder
import cm.aptoide.pt.home.bundles.base.AppComingSoonPromotionalBundle
import cm.aptoide.pt.home.bundles.base.HomeBundle
import cm.aptoide.pt.home.bundles.base.HomeEvent
import cm.aptoide.pt.networking.image.ImageLoader
import kotlinx.android.synthetic.main.card_coming_soon.view.*
import kotlinx.android.synthetic.main.card_new_package.view.action_button
import kotlinx.android.synthetic.main.card_new_package.view.action_button_skeleton
import kotlinx.android.synthetic.main.card_new_package.view.app_name
import kotlinx.android.synthetic.main.card_new_package.view.app_name_skeletonview
import kotlinx.android.synthetic.main.card_new_package.view.card_title_label
import kotlinx.android.synthetic.main.card_new_package.view.card_title_label_skeletonview
import rx.subjects.PublishSubject

class ComingSoonViewHolder(val view: View, val uiEventsListener: PublishSubject<HomeEvent>) :
  AppBundleViewHolder(view) {

  private var skeleton: Skeleton? = null

  override fun setBundle(homeBundle: HomeBundle?, position: Int) {

    if (homeBundle !is AppComingSoonPromotionalBundle) {
      throw IllegalStateException(
        this.javaClass.name + " is getting non AppComingSoonPromotionalBundle instance!"
      )
    }
    (homeBundle as? AppComingSoonPromotionalBundle)?.let { bundle ->
      if (homeBundle.content == null) {
        toggleSkeleton(true)
      } else {
        toggleSkeleton(false)

        itemView.card_title_label_text.text = homeBundle.title
        ImageLoader.with(itemView.context)
          .load(homeBundle.actionItem.featureGraphic, itemView.app_background_image)
        itemView.app_name.text = homeBundle.actionItem.title

        if (bundle.isRegisteredForNotification) {
          itemView.action_button.background =
            itemView.context.resources.getDrawable(R.drawable.btn_ghost_theme_grey_fog_light)
          itemView.action_button.setTextColor(
            itemView.context
              .resources.getColor(R.color.grey)
          )
          itemView.coming_soon_text.text =
            itemView.context.getString(R.string.promotional_new_active)
          itemView.action_button.text = itemView.context.getString(R.string.cancel)

          itemView.action_button.setOnClickListener {
            fireAppClickEvent(bundle, HomeEvent.Type.CANCEL_NOTIFY_ME)
          }

        } else {
          itemView.action_button.background =
            itemView.context.resources.getDrawable(R.drawable.btn_ghost_theme_normal)
          itemView.action_button.setTextColor(
            itemView.context
              .resources.getColor(R.color.default_orange_gradient_end)
          )
          itemView.coming_soon_text.text =
            itemView.context.getString(R.string.promotional_soon_in_aptoide)
          itemView.action_button.text =
            itemView.context.getString(R.string.promotional_notify_button)

          itemView.action_button.setOnClickListener {
            fireAppClickEvent(bundle, HomeEvent.Type.NOTIFY_ME)
          }
        }
      }
    }
  }

  private fun fireAppClickEvent(homeBundle: AppComingSoonPromotionalBundle, type: HomeEvent.Type) {
    uiEventsListener.onNext(
      HomeEvent(
        homeBundle, 0,
        type
      )
    )
  }

  private fun toggleSkeleton(showSkeleton: Boolean) {
    if (showSkeleton) {
      skeleton?.showSkeleton()
      itemView.card_title_label_skeletonview.visibility = View.VISIBLE
      itemView.card_title_label.visibility = View.INVISIBLE
      itemView.app_name_skeletonview.visibility = View.VISIBLE
      itemView.app_name.visibility = View.INVISIBLE
      itemView.aptoide_icon_skeleton.visibility = View.VISIBLE
      itemView.aptoide_icon.visibility = View.INVISIBLE
      itemView.text_skeleton_1.visibility = View.VISIBLE
      itemView.text_skeleton_2.visibility = View.VISIBLE
      itemView.coming_soon_text.visibility = View.INVISIBLE
      itemView.action_button_skeleton.visibility = View.VISIBLE
      itemView.action_button.visibility = View.INVISIBLE
    } else {
      skeleton?.showOriginal()
      itemView.card_title_label_skeletonview.visibility = View.INVISIBLE
      itemView.app_name_skeletonview.visibility = View.INVISIBLE
      itemView.app_name.visibility = View.VISIBLE
      itemView.aptoide_icon_skeleton.visibility = View.INVISIBLE
      itemView.aptoide_icon.visibility = View.VISIBLE
      itemView.text_skeleton_1.visibility = View.INVISIBLE
      itemView.text_skeleton_2.visibility = View.INVISIBLE
      itemView.coming_soon_text.visibility = View.VISIBLE
      itemView.action_button_skeleton.visibility = View.INVISIBLE
      itemView.action_button.visibility = View.VISIBLE
    }
  }
}