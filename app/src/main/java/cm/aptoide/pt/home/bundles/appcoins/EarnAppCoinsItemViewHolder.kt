package cm.aptoide.pt.home.bundles.appcoins

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.view.View
import cm.aptoide.pt.R
import cm.aptoide.pt.home.bundles.apps.RewardApp
import cm.aptoide.pt.home.bundles.base.AppHomeEvent
import cm.aptoide.pt.home.bundles.base.HomeBundle
import cm.aptoide.pt.home.bundles.base.HomeEvent
import cm.aptoide.pt.networking.image.ImageLoader
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.earn_appcoins_item.view.*
import rx.subjects.PublishSubject
import java.text.DecimalFormat

class EarnAppCoinsItemViewHolder(val view: View,
                                 val appClicks: PublishSubject<HomeEvent>,
                                 val decimalFormatter: DecimalFormat) :
    RecyclerView.ViewHolder(view) {

  fun setApp(app: RewardApp, homeBundle: HomeBundle?,
             bundlePosition: Int) {

    val fiat = app.reward?.fiat
    itemView.reward_textview.text =
        view.context.getString(R.string.poa_app_card_short,
            fiat?.symbol + decimalFormatter.format(fiat?.amount))
    itemView.app_title_textview.text = app.name
    ImageLoader.with(itemView.context)
        .load(app.featureGraphic, R.drawable.placeholder_square, itemView.app_feature_graphic)
    ImageLoader.with(itemView.context)
        .loadWithRoundCorners(app.icon, 8, itemView.app_image, R.drawable.placeholder_square,
            object : RequestListener<Drawable> {
              override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?,
                                        isFirstResource: Boolean): Boolean {
                return false
              }

              override fun onResourceReady(resource: Drawable?, model: Any?,
                                           target: Target<Drawable>?,
                                           dataSource: DataSource?,
                                           isFirstResource: Boolean): Boolean {
                Palette.from((resource as BitmapDrawable).bitmap).maximumColorCount(6)
                    .generate { palette ->
                      itemView.app_feature_graphic.setColorFilter(0x40000000)
                      ImageLoader.with(itemView.context)
                          .loadWithColorPlaceholder(app.featureGraphic, palette.getDominantColor(
                              Color.WHITE), itemView.app_feature_graphic)

                    }
                return false
              }
            })
    itemView.app_image
    itemView.setOnClickListener {
      appClicks.onNext(
          AppHomeEvent(app, adapterPosition, homeBundle, bundlePosition, HomeEvent.Type.APP))
    }
  }

}