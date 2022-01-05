package cm.aptoide.pt.home.bundles.promotional

import android.view.View
import cm.aptoide.aptoideviews.skeleton.Skeleton
import cm.aptoide.pt.R
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.home.bundles.base.*
import cm.aptoide.pt.networking.image.ImageLoader
import kotlinx.android.synthetic.main.card_new_package.view.*
import rx.subjects.PublishSubject

class NewAppViewHolder(val view: View,
                       val uiEventsListener: PublishSubject<HomeEvent>) :
    AppBundleViewHolder(view) {

  private var skeleton: Skeleton? = null

  override fun setBundle(homeBundle: HomeBundle?, position: Int) {
    if (homeBundle !is BonusPromotionalBundle) {
      throw IllegalStateException(
          this.javaClass.name + " is getting non BonusPromotionalBundle instance!")
    }
    (homeBundle as? BonusPromotionalBundle)?.let { bundle ->
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

        itemView.bonus_appc_view.setPercentage(bundle.bonusPercentage)
        if (!bundle.app.hasAppcBilling()) {
          itemView.bonus_appc_view.visibility = View.INVISIBLE
          itemView.appcoins_icon.setImageDrawable(
              itemView.context.resources.getDrawable(R.mipmap.ic_launcher))
          itemView.appcoins_system_text.text =
              itemView.context.getText(R.string.promotional_new_in_aptoide)
          itemView.card_title_label.visibility = View.VISIBLE
        } else {
          itemView.action_button.setBackgroundDrawable(itemView.context.resources
              .getDrawable(R.drawable.appc_gradient_rounded))
        }

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
      itemView.bonus_appc_skeleton_view.visibility = View.VISIBLE
      itemView.appcoins_icon_skeleton.visibility = View.VISIBLE
      itemView.appcoins_icon.visibility = View.INVISIBLE
      itemView.appcoins_system_text_skeleton_1.visibility = View.VISIBLE
      itemView.appcoins_system_text_skeleton_2.visibility = View.VISIBLE
      itemView.appcoins_system_text.visibility = View.INVISIBLE
      itemView.action_button_skeleton.visibility = View.VISIBLE
      itemView.action_button.visibility = View.INVISIBLE
    } else {
      skeleton?.showOriginal()
      itemView.card_title_label_skeletonview.visibility = View.INVISIBLE
      itemView.app_icon_skeletonview.visibility = View.INVISIBLE
      itemView.app_icon.visibility = View.VISIBLE
      itemView.app_name_skeletonview.visibility = View.INVISIBLE
      itemView.app_name.visibility = View.VISIBLE
      itemView.bonus_appc_skeleton_view.visibility = View.INVISIBLE
      itemView.appcoins_icon_skeleton.visibility = View.INVISIBLE
      itemView.appcoins_icon.visibility = View.VISIBLE
      itemView.appcoins_system_text_skeleton_1.visibility = View.INVISIBLE
      itemView.appcoins_system_text_skeleton_2.visibility = View.INVISIBLE
      itemView.appcoins_system_text.visibility = View.VISIBLE
      itemView.action_button_skeleton.visibility = View.INVISIBLE
      itemView.action_button.visibility = View.VISIBLE
    }
  }
}