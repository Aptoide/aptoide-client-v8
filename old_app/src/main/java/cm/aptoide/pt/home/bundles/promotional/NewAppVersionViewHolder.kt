package cm.aptoide.pt.home.bundles.promotional

import android.view.View
import cm.aptoide.aptoideviews.skeleton.Skeleton
import cm.aptoide.pt.R
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.home.bundles.base.*
import cm.aptoide.pt.networking.image.ImageLoader
import kotlinx.android.synthetic.main.card_new_app_version.view.*
import kotlinx.android.synthetic.main.card_new_package.view.action_button
import kotlinx.android.synthetic.main.card_new_package.view.action_button_skeleton
import kotlinx.android.synthetic.main.card_new_package.view.app_background_image
import kotlinx.android.synthetic.main.card_new_package.view.app_icon
import kotlinx.android.synthetic.main.card_new_package.view.app_icon_skeletonview
import kotlinx.android.synthetic.main.card_new_package.view.app_name
import kotlinx.android.synthetic.main.card_new_package.view.app_name_skeletonview
import kotlinx.android.synthetic.main.card_new_package.view.card_title_label
import kotlinx.android.synthetic.main.card_new_package.view.card_title_label_skeletonview
import kotlinx.android.synthetic.main.card_new_package.view.card_title_label_text
import rx.subjects.PublishSubject

class NewAppVersionViewHolder(val view: View,
                              val uiEventsListener: PublishSubject<HomeEvent>) :
    AppBundleViewHolder(view) {

  private var skeleton: Skeleton? = null

  override fun setBundle(homeBundle: HomeBundle?, position: Int) {
    if (homeBundle !is VersionPromotionalBundle) {
      throw IllegalStateException(
          this.javaClass.name + " is getting non PromotionalBundle instance!")
    }
    (homeBundle as? VersionPromotionalBundle)?.let { bundle ->
      if (homeBundle.content == null) {
        toggleSkeleton(true)
      } else {
        toggleSkeleton(false)
        ImageLoader.with(itemView.context)
            .loadWithRoundCorners(homeBundle.app.icon, 32, itemView.app_icon,
                R.attr.placeholder_square)
        itemView.card_title_label_text.text = homeBundle.title
        ImageLoader.with(itemView.context)
            .load(homeBundle.app.featureGraphic, itemView.app_background_image)
        itemView.app_name.text = homeBundle.app.name
        itemView.version_name.text = homeBundle.versionName
        itemView.action_button.setOnClickListener {
          fireAppClickEvent(homeBundle)
        }
        itemView.setOnClickListener {
          fireAppClickEvent(homeBundle)
        }

        setButtonText(homeBundle.downloadModel)
      }
    }
  }

  private fun setButtonText(model: DownloadModel) {
    when (model.action) {
      DownloadModel.Action.UPDATE -> itemView.action_button.text =
          itemView.resources.getString(R.string.appview_button_update)
      DownloadModel.Action.INSTALL -> itemView.action_button.text =
          itemView.resources.getString(R.string.appview_button_install)
      DownloadModel.Action.OPEN -> itemView.action_button.text =
          itemView.resources.getString(R.string.appview_button_open)
      DownloadModel.Action.DOWNGRADE -> itemView.action_button.text =
          itemView.resources.getString(R.string.appview_button_downgrade)
      DownloadModel.Action.MIGRATE -> itemView.action_button.text =
          itemView.resources.getString(R.string.promo_update2appc_appview_update_button)
    }
  }

  private fun fireAppClickEvent(homeBundle: PromotionalBundle) {
    uiEventsListener.onNext(
        AppHomeEvent(homeBundle.app, 0, homeBundle, adapterPosition,
            HomeEvent.Type.INSTALL_PROMOTIONAL))
  }

  private fun toggleSkeleton(showSkeleton: Boolean) {
    if (showSkeleton) {
      skeleton?.showSkeleton()
      itemView.card_title_label_skeletonview.visibility = View.VISIBLE
      itemView.card_title_label.visibility = View.INVISIBLE
      itemView.app_icon_skeletonview.visibility = View.VISIBLE
      itemView.app_icon.visibility = View.INVISIBLE
      itemView.app_name_skeletonview.visibility = View.VISIBLE
      itemView.app_name.visibility = View.INVISIBLE
      itemView.version_text_skeleton.visibility = View.VISIBLE
      itemView.version_name_skeleton.visibility = View.VISIBLE
      itemView.version_name.visibility = View.INVISIBLE
      itemView.action_button_skeleton.visibility = View.VISIBLE
      itemView.action_button.visibility = View.INVISIBLE
    } else {
      skeleton?.showOriginal()
      itemView.card_title_label_skeletonview.visibility = View.INVISIBLE
      itemView.card_title_label.visibility = View.VISIBLE
      itemView.app_icon_skeletonview.visibility = View.INVISIBLE
      itemView.app_icon.visibility = View.VISIBLE
      itemView.app_name_skeletonview.visibility = View.INVISIBLE
      itemView.app_name.visibility = View.VISIBLE
      itemView.version_text_skeleton.visibility = View.INVISIBLE
      itemView.version_name_skeleton.visibility = View.INVISIBLE
      itemView.version_name.visibility = View.VISIBLE
      itemView.action_button_skeleton.visibility = View.INVISIBLE
      itemView.action_button.visibility = View.VISIBLE
    }
  }
}